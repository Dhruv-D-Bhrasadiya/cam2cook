from __future__ import annotations

import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

from main import load_config, parameter_grid
from src.data.downloaders import download_open_images
from src.data.prepare import _copy_yolo_split, _write_dataset_yaml


class WorkflowTests(unittest.TestCase):
    def test_training_grid_covers_each_configured_value(self) -> None:
        config = load_config()
        grid = parameter_grid(config)
        expected_runs = 1
        for values in config["hyperparameter_grid"].values():
            expected_runs *= len(values)
        self.assertEqual(len(grid), expected_runs)
        self.assertEqual(len(config["models"]), 2)
        self.assertTrue(config["datasets"]["open-images"]["sweep"])

    def test_prepare_copies_only_images_with_matching_yolo_labels(self) -> None:
        with tempfile.TemporaryDirectory() as temporary_directory:
            root = Path(temporary_directory)
            source_images = root / "source" / "images"
            source_labels = root / "source" / "labels"
            source_images.mkdir(parents=True)
            source_labels.mkdir(parents=True)
            (source_images / "tomato.jpg").write_bytes(b"image")
            (source_labels / "tomato.txt").write_text("0 0.5 0.5 0.4 0.4\n")
            (source_images / "unlabeled.jpg").write_bytes(b"image")

            copied = _copy_yolo_split(root / "source", root / "prepared")

            self.assertEqual(copied, 1)
            self.assertTrue((root / "prepared" / "images" / "tomato.jpg").is_file())
            self.assertFalse((root / "prepared" / "images" / "unlabeled.jpg").exists())

    def test_dataset_yaml_quotes_class_names(self) -> None:
        with tempfile.TemporaryDirectory() as temporary_directory:
            yaml_path = Path(temporary_directory) / "dataset.yaml"

            _write_dataset_yaml(yaml_path, ["Bell pepper", "Tomato"])

            content = yaml_path.read_text(encoding="utf-8")
            self.assertIn('"Bell pepper"', content)
            self.assertIn('"Tomato"', content)

    def test_open_images_download_does_not_pass_dataset_dir(self) -> None:
        classes = ["Apple", "Banana"]
        with tempfile.TemporaryDirectory() as temporary_directory:
            raw_dir = Path(temporary_directory)
            with patch("fiftyone.zoo.load_zoo_dataset") as load_dataset:
                download_open_images(
                    raw_dir,
                    {"classes": classes, "max_samples": 150},
                )

            self.assertEqual(load_dataset.call_count, 2)
            for call_args in load_dataset.call_args_list:
                self.assertEqual(call_args.args, ("open-images-v7",))
                self.assertNotIn("dataset_dir", call_args.kwargs)
                self.assertEqual(call_args.kwargs["classes"], classes)
                self.assertEqual(call_args.kwargs["max_samples"], 150)
            self.assertTrue(
                (raw_dir / "open-images-yolo" / "classes.json").is_file()
            )


if __name__ == "__main__":
    unittest.main()
