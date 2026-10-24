"""Create configured pretrained object detectors."""

from __future__ import annotations


def create_model(model_name: str):
    """Load a supported Ultralytics detector from its pretrained checkpoint."""
    try:
        from ultralytics import YOLO
    except ImportError as exc:
        raise RuntimeError(
            "Training requires Ultralytics. Install it with "
            "'python -m pip install ultralytics'."
        ) from exc
    return YOLO(model_name)
