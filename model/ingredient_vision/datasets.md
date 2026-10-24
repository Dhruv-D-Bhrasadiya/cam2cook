# Dataset recommendations and download guide

## Start with the right task

CAM2COOK needs to recognize individual ingredients in ordinary kitchen
images. That is primarily an **object detection** problem: the training data
needs a box (or mask) for each visible ingredient. Image classification only
assigns labels to a whole image and is useful as a baseline, not a replacement
for localization. No image dataset alone can reliably infer an ingredient's
weight, freshness, or nutritional content.

Use the following datasets for complementary purposes; they are not
interchangeable and should not be merged before checking their label
definitions, overlap, and license terms.

| Dataset | What it contributes | Limitations / recommended use | Source |
|---|---|---|---|
| Food-101 | 101 food/dish image classes; a stable classification baseline | Mostly dishes, not ingredient boxes; use to benchmark food-image classification or pretraining experiments | [Official page and download](https://data.vision.ee.ethz.ch/cvl/datasets_extra/food-101/) · [paper](https://data.vision.ee.ethz.ch/cvl/publications/sermanet14learning.pdf) |
| Fruits-360 | Many classes of isolated fruit/vegetable images, with train/test folders | Clean, centered, often uniform-background images differ substantially from phone photos; use for a quick classification baseline, not as a real-world test set | [Kaggle dataset](https://www.kaggle.com/datasets/moltean/fruits) · [paper](https://www.mdpi.com/2306-5729/3/1/14) |
| Grocery Store Dataset | 5,125 natural smartphone images across fine- and coarse-grained grocery classes | Image-level labels, not object boxes; use for classification or transfer experiments, not as a detector training set | [Public project and download instructions](https://github.com/marcusklasson/GroceryStoreDataset) · [paper](https://arxiv.org/abs/1901.00711) |
| Open Images V7 | Large-scale images with object boxes and broad food labels | Very large and label coverage/quality varies; download a class-filtered subset first, inspect labels and per-class counts | [Dataset](https://storage.googleapis.com/openimages/web/index.html) · [paper](https://arxiv.org/abs/1811.00982) |
| SKU-110K | Dense product-box annotations in crowded retail scenes | Retail products rather than kitchen ingredients; useful as a crowded-scene detector stress test, not the primary ingredient dataset | [Project, data, and license](https://github.com/eg4000/SKU110K_CVPR19) · [paper](https://arxiv.org/abs/1904.00852) |

### Recommended collection plan

1. Establish an ingredient taxonomy (including synonyms and visually
   indistinguishable classes) and annotation rules.
2. Build a small, carefully reviewed kitchen-photo validation/test set early.
   Include varied phones, lighting, backgrounds, quantities, occlusions, and
   single/mixed ingredients. Split by capture session or source, not randomly
   by image when near-duplicates exist.
3. Use Fruits-360 only as an easy classification sanity check. Use Grocery
   Store and Open Images for detection initialization/comparison where their
   labels match the taxonomy. Fine-tune and evaluate on kitchen-domain images.
4. Add hard examples and rare ingredients deliberately. Report per-class
   precision/recall and AP, not only overall accuracy.
5. Check each source's current terms and preserve required attribution and
   license metadata. Open Images images can have image-specific licenses;
   Kaggle download access does not itself grant permission to redistribute or
   use every included image. The downloader does not bypass terms or logins.

## Download steps

Run commands from `model/ingredient_vision` in PowerShell after installing
`requirements.txt`. Downloads can be large; ensure adequate disk space. The
CLI downloads only when explicitly asked and writes source data under
`data/raw/`.

```powershell
# Show configured datasets, models, and sweep values.
python .\main.py list

# Download the MVP object-detection data.
python .\main.py download --dataset open-images

# Download a bounded subset with a custom class list.
python .\main.py download --dataset open-images --classes Apple Banana Tomato --max-samples 1000

# Fruits-360: install/configure Kaggle CLI once, then download and unzip.
python -m pip install kaggle
# Put your Kaggle API token in the location documented by Kaggle.
python .\main.py download --dataset fruits360

# Grocery Store Dataset: requires Git.
python .\main.py download --dataset grocery-store

# Download every configured public dataset (can be very large).
python .\main.py download --all

# Standalone downloader for a single dataset.
python .\download_dataset.py --dataset food101
```

Open Images needs FiftyOne, which is installed from `requirements.txt`.
Fruits-360 requires the Kaggle CLI and configured API token. `--all`
downloads all configured public sources, including classification datasets
that this MVP's detector training pipeline does not consume. It may use
substantial disk space and does not accept dataset licenses on your behalf.

### Custom kitchen data

For a product-relevant MVP, annotate photos of actual groceries in YOLO
object-detection format. Set `custom-yolo.dataset_yaml` in
`configs/pipeline.json` to a dataset YAML with reviewed class names and
separate train/validation paths. To include custom data in `train --all`, set
`"sweep": true` after verifying the YAML and dataset are ready. Keep a
separate held-out test split for final evaluation.

The Open Images class list is an editable initial taxonomy. Open Images does
not cover every consumable or every packaged product. Expand it with legally
usable, representative local grocery and kitchen photos, preserve the
dataset's class mapping, and design the mobile confirmation flow to let users
correct or add items the model does not know.

When overriding Open Images labels with `--classes`, the downloader records
that exact label order beside the exported annotations. Dataset preparation
uses that saved order to generate the training YAML, so class IDs stay aligned.
