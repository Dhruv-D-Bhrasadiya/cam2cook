# Ingredient vision training workspace

This directory is the home for experiments that load datasets, train and test
computer-vision models, and export weights for the CAM2COOK mobile app. The
existing notebook in `model/` is left untouched.

## Workspace layout

```text
ingredient_vision/
  data/
    raw/          downloaded source datasets (not committed)
    processed/    prepared splits and annotations (not committed)
  configs/        reproducible experiment configurations
  src/
    data/         downloaders and dataset preparation adapters
    models/       model factory
    training.py   train, evaluate, and export functions
  tests/          data and export consistency checks
  checkpoints/    training checkpoints (not committed)
  exports/        app-ready model files (not committed)
  main.py         unified terminal workflow
  download_dataset.py
  requirements.txt
  configs/pipeline.json
  datasets.md
  model-selection.md
  hyperparameters.md
```

This pipeline implements the **MVP 1 — Food Inventory** recognition step:
detect multiple known food objects in a captured image so the user can confirm
and correct candidates before inventory entry. The configured Open Images
classes are an initial benchmark taxonomy, not a guarantee of recognizing
everything a person may buy to eat. Packaged products, regional foods, new
classes, and visually ambiguous items need licensed, representative labeled
photos. Treat unknown/low-confidence predictions as a request for user
confirmation; never silently claim complete coverage.

Dataset files and model binaries are intentionally excluded from Git. Record
dataset source, version, license, split seed, class map, preprocessing, and
export settings with each experiment. Do not ship an export until its license
and on-device performance have been checked.

## Getting started

Use Python 3.9 or newer. From this directory, install training dependencies:

```powershell
# Install the detector framework; PyTorch is installed as its dependency.
python -m pip install -r .\requirements.txt

# See configured datasets, models, and hyperparameter values.
python .\main.py list

# Download the MVP Open Images detection dataset (requires FiftyOne).
python .\main.py download --dataset open-images

# Prepare YOLO data, train one model, and evaluate its best checkpoint.
python .\main.py prepare --dataset open-images
python .\main.py train --dataset open-images --model yolov8n --epochs 30 --imgsz 416 --batch 8
python .\main.py test --dataset open-images --weights .\checkpoints\runs\RUN_NAME\weights\best.pt

# Export the trained checkpoint for mobile integration.
python .\main.py export --weights .\checkpoints\runs\RUN_NAME\weights\best.pt --format tflite

# Preview all configured combinations, then run the full sweep when ready.
python .\main.py train --all --dry-run
python .\main.py train --all
```

Run commands from this directory (`model/ingredient_vision`).
`main.py download --all` downloads every configured public dataset, including
classification-only comparisons that are not included in the MVP detector
training sweep. Some sources require Kaggle credentials, Git, or substantial
disk space. Check each source's terms first; `--all` does not accept licenses
on your behalf.

`train --all` runs the Cartesian product of datasets marked `"sweep": true`,
configured detector models, and every value combination in
`configs/pipeline.json`. It can take substantial GPU time. The default sweep
currently uses only the prepared Open Images detector dataset and two nano
YOLO checkpoints. The manual `custom-yolo` dataset is excluded until you set
its dataset YAML and opt it into the sweep.

Use `python .\main.py --help` and `python .\main.py <command> --help` for CLI
options. `download_dataset.py` is also available for a focused single-dataset
download. Read [datasets.md](./datasets.md) for setup and annotation
requirements, [model-selection.md](./model-selection.md) for comparison
rationale, and [hyperparameters.md](./hyperparameters.md) for search
guidance.

## Training and mobile handoff

Use reviewed box annotations in YOLO format for custom kitchen images, with
separate `train` and `val` splits and stable class IDs. Keep a separate
held-out test split in your own dataset and do not tune against it. The
configured Open Images export is a development benchmark; it does not replace
a kitchen-domain test set.

The `test` command evaluates on the dataset YAML's validation split.
Ultralytics stores `best.pt` under `checkpoints/runs/RUN_NAME/weights/`. Exported
artifacts are copied to `exports/`. Benchmark the exact exported model on
target phones and document input size, color order, normalization, class
IDs/names, confidence/NMS behavior, and model format before app integration.
For a custom YAML that declares a separate `test` path, pass `--split test`
for the final held-out evaluation.

Suggested deployment targets are TensorFlow Lite (including full integer
quantization) for Android and Core ML for Apple devices. Validate exported
predictions against the original framework before integrating the artifact.
