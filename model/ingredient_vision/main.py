"""Terminal entry point for dataset download, preparation, and training."""

from __future__ import annotations

import argparse
import itertools
import json
import re
import shutil
import subprocess
import sys
import tarfile
from datetime import datetime
from pathlib import Path
from typing import Any

from src.data.downloaders import DOWNLOADERS, download_dataset
from src.data.prepare import PREPARERS, prepare_dataset
from src.training import evaluate_detector, export_detector, train_detector


ROOT = Path(__file__).resolve().parent
CONFIG_PATH = ROOT / "configs" / "pipeline.json"
RAW_DIR = ROOT / "data" / "raw"
PROCESSED_DIR = ROOT / "data" / "processed"
RUNS_DIR = ROOT / "checkpoints" / "runs"


def load_config() -> dict[str, Any]:
    with CONFIG_PATH.open(encoding="utf-8") as config_file:
        return json.load(config_file)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Download datasets, prepare labels, and train CAM2COOK food detectors."
    )
    subparsers = parser.add_subparsers(dest="command", required=True)

    subparsers.add_parser("list", help="List configured datasets, models, and sweep values.")

    download_parser = subparsers.add_parser("download", help="Download one or more datasets.")
    download_parser.add_argument("--dataset", action="append", choices=tuple(DOWNLOADERS))
    download_parser.add_argument(
        "--all", action="store_true", help="Download every configured downloadable dataset."
    )
    download_parser.add_argument(
        "--max-samples",
        type=int,
        help="Open Images samples per split; use 0 for no limit.",
    )
    download_parser.add_argument("--classes", nargs="+")

    prepare_parser = subparsers.add_parser("prepare", help="Prepare detection data.")
    prepare_parser.add_argument("--dataset", choices=tuple(PREPARERS), required=True)
    prepare_parser.add_argument("--dataset-yaml", type=Path)

    train_parser = subparsers.add_parser("train", help="Train one detector.")
    train_parser.add_argument("--dataset", choices=tuple(PREPARERS))
    train_parser.add_argument("--model", help="Configured model key from configs/pipeline.json.")
    train_parser.add_argument("--epochs", type=int)
    train_parser.add_argument("--imgsz", type=int)
    train_parser.add_argument("--batch", type=int)
    train_parser.add_argument("--lr0", type=float)
    train_parser.add_argument("--weight-decay", type=float)
    train_parser.add_argument("--patience", type=int)
    train_parser.add_argument(
        "--device", default=None, help="Ultralytics device, e.g. cpu, 0 (default: automatic)."
    )
    train_parser.add_argument(
        "--all",
        action="store_true",
        help="Run every configured detection dataset x model x hyperparameter combination.",
    )
    train_parser.add_argument(
        "--dry-run", action="store_true", help="Print the sweep without starting training."
    )

    test_parser = subparsers.add_parser("test", help="Evaluate trained weights on a dataset.")
    test_parser.add_argument("--dataset", choices=tuple(PREPARERS), required=True)
    test_parser.add_argument("--weights", type=Path, required=True)
    test_parser.add_argument("--dataset-yaml", type=Path)
    test_parser.add_argument(
        "--split",
        choices=("val", "test"),
        default="val",
        help="Evaluate on validation by default; use test for a held-out split.",
    )
    test_parser.add_argument("--device", default=None)

    export_parser = subparsers.add_parser("export", help="Export trained weights for deployment.")
    export_parser.add_argument("--weights", type=Path, required=True)
    export_parser.add_argument(
        "--format",
        choices=("tflite", "onnx", "coreml", "openvino"),
        required=True,
    )

    return parser.parse_args()


def list_options(config: dict[str, Any]) -> None:
    print("Configured datasets:")
    for name, settings in config["datasets"].items():
        marker = (
            " (included in detection sweep)"
            if name in PREPARERS and settings.get("sweep", False)
            else ""
        )
        print(f"  {name}: {settings['task']}{marker}")
    print("Configured detector models:")
    for name, checkpoint in config["models"].items():
        print(f"  {name}: {checkpoint}")
    print("Configured sweep values:")
    for parameter, values in config["hyperparameter_grid"].items():
        print(f"  {parameter}: {values}")


def selected_downloads(
    args: argparse.Namespace, config: dict[str, Any]
) -> list[str]:
    if args.all and args.dataset:
        raise ValueError("Use either --all or --dataset, not both.")
    if not args.all and not args.dataset:
        raise ValueError("Select at least one --dataset or use --all.")
    if args.all:
        return [
            name
            for name, settings in config["datasets"].items()
            if settings.get("download") and name in DOWNLOADERS
        ]
    return args.dataset


def do_download(args: argparse.Namespace, config: dict[str, Any]) -> None:
    if args.max_samples is not None and args.max_samples < 0:
        raise ValueError("--max-samples must be zero or greater.")
    names = selected_downloads(args, config)
    for name in names:
        options = {
            "classes": args.classes or config["datasets"][name].get("classes", []),
            "max_samples": args.max_samples
            if args.max_samples is not None
            else config["datasets"][name].get("max_samples_per_split", 5000),
        }
        if name == "open-images" and not options["classes"]:
            raise ValueError(
                "Open Images requires labels. Set classes in pipeline.json or pass --classes."
            )
        print(f"\n=== Downloading {name} ===")
        download_dataset(name, RAW_DIR / name, options)


def prepared_yaml(name: str, config: dict[str, Any], custom_yaml: Path | None) -> Path:
    options = dict(config["datasets"][name])
    if custom_yaml is not None:
        options["dataset_yaml"] = str(custom_yaml)
    raw_dir = RAW_DIR / name
    output_dir = PROCESSED_DIR / name
    return prepare_dataset(name, raw_dir, output_dir, options)


def parameter_grid(config: dict[str, Any]) -> list[dict[str, Any]]:
    grid = config["hyperparameter_grid"]
    keys = list(grid)
    return [
        dict(zip(keys, values))
        for values in itertools.product(*(grid[key] for key in keys))
    ]


def run_name(dataset: str, model: str, params: dict[str, Any]) -> str:
    slug = "_".join(f"{key}-{value}" for key, value in params.items())
    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S-%f")
    safe = re.sub(r"[^A-Za-z0-9_.-]+", "-", f"{dataset}_{model}_{slug}")
    return f"{safe}_{timestamp}"


def do_train(args: argparse.Namespace, config: dict[str, Any]) -> None:
    if args.all and (args.dataset or args.model):
        raise ValueError("Use --all by itself; it selects all configured datasets and models.")
    if args.all and any(
        getattr(args, name) is not None
        for name in ("epochs", "imgsz", "batch", "lr0", "weight_decay", "patience")
    ):
        raise ValueError("Use --all by itself; it uses all values from the configured grid.")
    if not args.all and (not args.dataset or not args.model):
        raise ValueError("Training one run requires both --dataset and --model.")
    if not args.all and args.model not in config["models"]:
        raise ValueError(
            f"Model '{args.model}' is not configured. Choose from: "
            f"{', '.join(config['models'])}"
        )

    datasets = (
        [
            name
            for name, settings in config["datasets"].items()
            if name in PREPARERS and settings.get("sweep", False)
        ]
        if args.all
        else [args.dataset]
    )
    models = list(config["models"]) if args.all else [args.model]
    params_list = parameter_grid(config) if args.all else [
        {
            key: value
            for key, value in {
                "epochs": args.epochs,
                "imgsz": args.imgsz,
                "batch": args.batch,
                "lr0": args.lr0,
                "weight_decay": args.weight_decay,
                "patience": args.patience,
            }.items()
            if value is not None
        }
    ]

    total = len(datasets) * len(models) * len(params_list)
    print(f"Training runs selected: {total}")
    if args.dry_run:
        for dataset, model, params in itertools.product(datasets, models, params_list):
            print(f"  {dataset} | {model} | {params}")
        return

    dataset_yamls = {
        dataset: prepared_yaml(dataset, config, None) for dataset in datasets
    }
    for dataset, model, params in itertools.product(datasets, models, params_list):
        name = run_name(dataset, model, params)
        print(f"\n=== Training {name} ===")
        best_weights = train_detector(
            config["models"][model],
            dataset_yamls[dataset],
            params,
            RUNS_DIR,
            name,
            args.device,
        )
        print(f"Best weights: {best_weights}")


def do_test(args: argparse.Namespace, config: dict[str, Any]) -> None:
    dataset_config = dict(config["datasets"][args.dataset])
    if args.dataset_yaml is not None:
        dataset_config["dataset_yaml"] = str(args.dataset_yaml)
    data_yaml = prepare_dataset(
        args.dataset,
        RAW_DIR / args.dataset,
        PROCESSED_DIR / args.dataset,
        dataset_config,
    )
    metrics = evaluate_detector(args.weights, data_yaml, args.device, args.split)
    for name, value in sorted(metrics.items()):
        print(f"{name}: {value:.6f}")


def do_export(args: argparse.Namespace) -> None:
    exported = export_detector(args.weights, args.format)
    destination = ROOT / "exports" / exported.name
    destination.parent.mkdir(parents=True, exist_ok=True)
    if exported.resolve() != destination.resolve():
        if exported.is_dir():
            shutil.copytree(exported, destination, dirs_exist_ok=True)
        else:
            shutil.copy2(exported, destination)
    print(f"Exported model: {destination}")


def main() -> int:
    args = parse_args()
    config = load_config()
    try:
        if args.command == "list":
            list_options(config)
        elif args.command == "download":
            do_download(args, config)
        elif args.command == "prepare":
            dataset_config = dict(config["datasets"][args.dataset])
            if args.dataset_yaml is not None:
                dataset_config["dataset_yaml"] = str(args.dataset_yaml)
            result = prepare_dataset(
                args.dataset,
                RAW_DIR / args.dataset,
                PROCESSED_DIR / args.dataset,
                dataset_config,
            )
            print(f"Prepared dataset config: {result}")
        elif args.command == "train":
            do_train(args, config)
        elif args.command == "test":
            do_test(args, config)
        else:
            do_export(args)
    except (
        KeyError,
        OSError,
        RuntimeError,
        subprocess.CalledProcessError,
        tarfile.TarError,
        ValueError,
    ) as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
