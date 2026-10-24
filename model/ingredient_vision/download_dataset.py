"""Standalone dataset downloader CLI; use main.py for the full workflow."""

from __future__ import annotations

import argparse
import subprocess
import sys
import tarfile
from pathlib import Path

from src.data.downloaders import DOWNLOADERS, download_dataset


DEFAULT_RAW_DIR = Path(__file__).resolve().parent / "data" / "raw"


def main() -> int:
    parser = argparse.ArgumentParser(description="Download a CAM2COOK vision dataset.")
    parser.add_argument("--dataset", choices=tuple(DOWNLOADERS), required=True)
    parser.add_argument("--data-dir", type=Path, default=DEFAULT_RAW_DIR)
    parser.add_argument(
        "--max-samples",
        type=int,
        default=5000,
        help="Open Images samples per split; use 0 for no limit.",
    )
    parser.add_argument(
        "--classes",
        nargs="+",
        default=["Apple", "Banana", "Bread", "Carrot", "Egg", "Milk", "Tomato"],
    )
    args = parser.parse_args()
    if args.max_samples < 0:
        parser.error("--max-samples must be zero or greater")
    try:
        download_dataset(
            args.dataset,
            args.data_dir / args.dataset,
            {"classes": args.classes, "max_samples": args.max_samples},
        )
    except (
        OSError,
        RuntimeError,
        subprocess.CalledProcessError,
        tarfile.TarError,
        ValueError,
    ) as exc:
        print(f"Dataset download failed: {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
