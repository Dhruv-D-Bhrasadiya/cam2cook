"""Validate and prepare source datasets as Ultralytics YOLO datasets."""

from __future__ import annotations

import json
import shutil
from pathlib import Path
from typing import Any


IMAGE_SUFFIXES = {".jpg", ".jpeg", ".png", ".webp", ".bmp"}


def _copy_yolo_split(source: Path, destination: Path) -> int:
    images_source = source / "images"
    labels_source = source / "labels"
    if not images_source.is_dir() or not labels_source.is_dir():
        raise FileNotFoundError(
            f"Expected YOLO export folders 'images' and 'labels' under {source}"
        )

    image_files = [
        path for path in images_source.rglob("*") if path.suffix.lower() in IMAGE_SUFFIXES
    ]
    copied = 0
    for image_path in image_files:
        relative_image = image_path.relative_to(images_source)
        label_path = labels_source / relative_image.with_suffix(".txt")
        if not label_path.is_file():
            continue
        target_image = destination / "images" / relative_image
        target_label = destination / "labels" / relative_image.with_suffix(".txt")
        target_image.parent.mkdir(parents=True, exist_ok=True)
        target_label.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(image_path, target_image)
        shutil.copy2(label_path, target_label)
        copied += 1
    return copied


def _write_dataset_yaml(path: Path, class_names: list[str]) -> None:
    # JSON string literals are valid YAML scalars and safely quote class names.
    lines = [
        f"path: {json.dumps(str(path.parent.resolve()))}",
        "train: images/train",
        "val: images/val",
        "names:",
    ]
    lines.extend(f"  {index}: {json.dumps(name)}" for index, name in enumerate(class_names))
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def prepare_open_images(raw_dir: Path, output_dir: Path, options: dict[str, Any]) -> Path:
    source = raw_dir / "open-images-yolo"
    metadata_path = source / "classes.json"
    classes = options["classes"]
    if metadata_path.is_file():
        with metadata_path.open(encoding="utf-8") as metadata_file:
            metadata = json.load(metadata_file)
        classes = metadata.get("classes", classes) if isinstance(metadata, dict) else None
    if not isinstance(classes, list) or not classes or not all(
        isinstance(class_name, str) and class_name for class_name in classes
    ):
        raise ValueError("Open Images class names are missing; download with --classes.")
    output_dir.mkdir(parents=True, exist_ok=True)
    train_count = _copy_yolo_split(source / "train", output_dir / "train")
    val_count = _copy_yolo_split(source / "validation", output_dir / "val")
    if not train_count or not val_count:
        raise ValueError(
            "Open Images export needs labeled images in both train and validation "
            f"splits; found train={train_count}, val={val_count}."
        )
    _write_dataset_yaml(output_dir / "dataset.yaml", classes)
    print(f"Prepared Open Images YOLO data: train={train_count}, val={val_count}")
    return output_dir / "dataset.yaml"


def prepare_custom_yolo(raw_dir: Path, output_dir: Path, options: dict[str, Any]) -> Path:
    try:
        import yaml
    except ImportError as exc:
        raise RuntimeError(
            "Reading custom YOLO dataset configs requires PyYAML. Install "
            "Ultralytics with 'python -m pip install ultralytics'."
        ) from exc

    dataset_yaml = Path(options["dataset_yaml"])
    if not dataset_yaml.is_absolute():
        dataset_yaml = raw_dir.parent.parent / dataset_yaml
    if not dataset_yaml.is_file():
        raise FileNotFoundError(f"YOLO dataset YAML does not exist: {dataset_yaml}")
    with dataset_yaml.open(encoding="utf-8") as config_file:
        config = yaml.safe_load(config_file)
    if not isinstance(config, dict) or not all(
        key in config for key in ("train", "val", "names")
    ):
        raise ValueError("YOLO dataset YAML must define train, val, and names.")
    names = config["names"]
    if not isinstance(names, (list, dict)) or not names:
        raise ValueError("YOLO dataset YAML must provide at least one class name.")
    dataset_root = Path(config.get("path", dataset_yaml.parent))
    if not dataset_root.is_absolute():
        dataset_root = (dataset_yaml.parent / dataset_root).resolve()
    for split in ("train", "val"):
        split_paths = config[split] if isinstance(config[split], list) else [config[split]]
        if not split_paths:
            raise ValueError(f"YOLO dataset YAML has no {split} paths.")
        for split_path in split_paths:
            path = Path(split_path)
            if not path.is_absolute():
                path = dataset_root / path
            if not path.exists():
                raise FileNotFoundError(f"YOLO {split} path does not exist: {path}")
    return dataset_yaml.resolve()


PREPARERS = {
    "open-images": prepare_open_images,
    "custom-yolo": prepare_custom_yolo,
}


def prepare_dataset(
    name: str,
    raw_dir: Path,
    output_dir: Path,
    options: dict[str, Any] | None = None,
) -> Path:
    """Prepare a configured detection dataset and return its dataset YAML."""
    try:
        preparer = PREPARERS[name]
    except KeyError as exc:
        raise ValueError(
            f"Dataset '{name}' does not have a detection-data preparation adapter."
        ) from exc
    return preparer(raw_dir, output_dir, options or {})
