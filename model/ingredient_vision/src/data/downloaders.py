"""Download supported datasets into the configured raw-data directory."""

from __future__ import annotations

import json
import shutil
import subprocess
import tarfile
import urllib.request
from pathlib import Path
from typing import Any


FOOD101_URL = "https://data.vision.ee.ethz.ch/cvl/food-101.tar.gz"
GROCERY_STORE_REPO = "https://github.com/marcusklasson/GroceryStoreDataset.git"


def safe_extract_tar(archive: Path, destination: Path) -> None:
    """Extract regular archive entries only, rejecting paths outside destination."""
    root = destination.resolve()
    with tarfile.open(archive, "r:gz") as tar:
        members = tar.getmembers()
        for member in members:
            target = (root / member.name).resolve()
            if not target.is_relative_to(root):
                raise ValueError(f"Archive contains an unsafe path: {member.name}")
            if member.issym() or member.islnk():
                raise ValueError(f"Archive contains an unsupported link: {member.name}")
            if not (member.isfile() or member.isdir()):
                raise ValueError(f"Archive contains an unsupported entry: {member.name}")
        tar.extractall(root, members=members)


def download_food101(raw_dir: Path, _options: dict[str, Any]) -> None:
    archive = raw_dir / "food-101.tar.gz"
    extracted = raw_dir / "food-101"
    if extracted.exists():
        print(f"Already present, skipping: {extracted}")
        return
    if not archive.exists():
        print(f"Downloading Food-101 to {archive}")
        urllib.request.urlretrieve(FOOD101_URL, archive)
    print(f"Extracting {archive}")
    safe_extract_tar(archive, raw_dir)


def download_fruits360(raw_dir: Path, _options: dict[str, Any]) -> None:
    if shutil.which("kaggle") is None:
        raise RuntimeError(
            "Fruits-360 needs the Kaggle CLI. Install it with "
            "'python -m pip install kaggle' and configure your Kaggle API token."
        )
    destination = raw_dir / "fruits360"
    destination.mkdir(parents=True, exist_ok=True)
    subprocess.run(
        [
            "kaggle",
            "datasets",
            "download",
            "-d",
            "moltean/fruits",
            "-p",
            str(destination),
            "--unzip",
        ],
        check=True,
    )


def download_grocery_store(raw_dir: Path, _options: dict[str, Any]) -> None:
    if shutil.which("git") is None:
        raise RuntimeError("The Grocery Store Dataset download requires Git.")
    destination = raw_dir / "GroceryStoreDataset"
    if destination.exists():
        print(f"Already present, skipping: {destination}")
        return
    subprocess.run(
        ["git", "clone", "--depth", "1", GROCERY_STORE_REPO, str(destination)],
        check=True,
    )


def download_open_images(raw_dir: Path, options: dict[str, Any]) -> None:
    try:
        import fiftyone as fo
    except ImportError as exc:
        raise RuntimeError(
            "Open Images download requires FiftyOne. Install it with "
            "'python -m pip install fiftyone'."
        ) from exc

    classes = options["classes"]
    max_samples = options["max_samples"]
    for split in ("train", "validation"):
        dataset = fo.zoo.load_zoo_dataset(
            "open-images-v7",
            split=split,
            label_types=["detections"],
            classes=classes,
            max_samples=max_samples or None,
        )
        export_dir = raw_dir / "open-images-yolo" / split
        export_dir.mkdir(parents=True, exist_ok=True)
        dataset.export(
            export_dir=str(export_dir),
            dataset_type=fo.types.YOLOv5Dataset,
            label_field="ground_truth",
            classes=classes,
        )
    metadata_path = raw_dir / "open-images-yolo" / "classes.json"
    metadata_path.write_text(json.dumps({"classes": classes}, indent=2), encoding="utf-8")


DOWNLOADERS = {
    "food101": download_food101,
    "fruits360": download_fruits360,
    "grocery-store": download_grocery_store,
    "open-images": download_open_images,
}


def download_dataset(
    name: str,
    raw_dir: Path,
    options: dict[str, Any] | None = None,
) -> None:
    """Download one configured dataset by registry name."""
    try:
        downloader = DOWNLOADERS[name]
    except KeyError as exc:
        raise ValueError(f"Dataset '{name}' has no downloader.") from exc
    raw_dir.mkdir(parents=True, exist_ok=True)
    downloader(raw_dir, options or {})
