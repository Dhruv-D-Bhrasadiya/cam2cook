"""Train one configured detector and save its best checkpoint."""

from __future__ import annotations

from pathlib import Path
from typing import Any

from .models.factory import create_model


def train_detector(
    model_name: str,
    dataset_yaml: Path,
    hyperparameters: dict[str, Any],
    project_dir: Path,
    run_name: str,
    device: str | None,
) -> Path:
    model = create_model(model_name)
    model.train(
        data=str(dataset_yaml),
        project=str(project_dir),
        name=run_name,
        exist_ok=True,
        device=device,
        **hyperparameters,
    )
    best_weights = project_dir / run_name / "weights" / "best.pt"
    if not best_weights.is_file():
        raise FileNotFoundError(
            f"Training finished but the expected checkpoint was not created: {best_weights}"
        )
    return best_weights


def evaluate_detector(
    weights: Path, dataset_yaml: Path, device: str | None = None, split: str = "val"
) -> dict[str, float]:
    model = create_model(str(weights))
    results = model.val(data=str(dataset_yaml), device=device, split=split)
    metrics = results.results_dict
    return {
        key: float(value)
        for key, value in metrics.items()
        if isinstance(value, (int, float))
    }


def export_detector(weights: Path, export_format: str) -> Path:
    model = create_model(str(weights))
    exported = model.export(format=export_format)
    return Path(exported)
