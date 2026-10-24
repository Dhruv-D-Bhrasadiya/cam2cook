# Hyperparameter ranges and training protocol

These are starting search ranges, not universal optima. Tune on the validation
set using the chosen task and framework. Keep the test set untouched until
model selection is complete. For each experiment, record the random seed,
dataset/split version, class mapping, preprocessing, framework version, and
checkpoint.

The terminal pipeline's current concrete sweep is in
[`configs/pipeline.json`](./configs/pipeline.json): 30 epochs, image sizes
416/640, batches 8/16, learning rates 0.001/0.01, weight decay 0.0005, and
patience 10. `python main.py train --all --dry-run` prints the full Cartesian
grid before training. Edit that JSON to change or expand the sweep; it only
applies to registered detector architectures and datasets marked for
inclusion.

## Shared training controls

| Parameter | Initial range / values | Guidance |
|---|---|---|
| Input resolution | Classifier: 160, 192, 224, 256 px; detector: 320, 416, 512, 640 px | Include likely deployment size. Higher resolution can improve small-object recall but costs latency and memory. |
| Batch size | 16-128 for classification; 4-32 for detection | Use the largest stable batch that fits. When constrained, use gradient accumulation and report effective batch size. |
| Optimizer | AdamW or SGD with momentum 0.9 | Compare AdamW for fine-tuning with SGD for detector recipes where appropriate; keep other settings controlled. |
| Weight decay | 1e-6-1e-3 (AdamW); 1e-5-5e-4 (SGD) | Tune on validation; avoid transferring an optimizer's default decay between frameworks. |
| Training duration | 20-100 epochs; early-stop patience 8-15 epochs | Save the best validation checkpoint; do not select the final epoch automatically. |
| Warmup | 0-5 epochs, or 0-10% of total steps | Useful when fine-tuning or using a larger initial learning rate. |
| Scheduler | cosine decay, one-cycle, or step decay | Choose one policy and record its warmup, minimum LR, and total steps. |
| Label smoothing | 0-0.1 for classification | Start at 0 or 0.05; use only if labels are noisy enough to justify it. |
| Random seed | At least 3 seeds for finalists | Report mean and spread; one lucky run is not a reliable selection result. |

## Classification search

Use only for one-label-per-image tasks or an auxiliary baseline.

| Parameter | Suggested range |
|---|---|
| Learning rate, pretrained backbone | 1e-5-3e-4 |
| Learning rate, new classification head | 1e-4-3e-3 |
| Fine-tuning | Train head for 1-5 epochs, then unfreeze all or the final stages; compare against end-to-end fine-tuning |
| Augmentation | Random resized crop (scale 0.7-1.0), horizontal flip (p 0.5 where label-preserving), mild color jitter (0.1-0.3), rotation up to 10-20 degrees |
| MixUp / CutMix | alpha 0.1-0.4; compare enabled vs disabled |
| Dropout (head) | 0.0-0.4 |
| Class imbalance | Weighted sampler or class-weighted loss; compare macro-F1 and per-class recall |

Avoid transformations that create implausible food appearance or change the
label. Use validation metrics aligned to the product, such as top-1 accuracy,
macro-F1, and per-class recall.

## Object-detection search

| Parameter | Suggested range |
|---|---|
| Initial LR, pretrained | 1e-5-1e-3; follow the selected implementation's batch-size scaling |
| Initial LR, training from scratch | 1e-3-1e-2 only when training recipe and dataset size support it; prefer pretrained weights for small datasets |
| Warmup | 0-5 epochs or 0-10% of total iterations |
| IoU threshold for assignment/NMS | Tune around 0.45-0.7 for NMS; training assignment thresholds are architecture-specific |
| Confidence threshold | Sweep 0.05-0.5 on validation; set for product precision/recall trade-off, not by convention |
| Maximum detections per image | 10-100, based on expected scene density |
| Box loss / classification loss weights | Use framework defaults first; explore 0.5x-2x only when diagnosis indicates imbalance |
| Mosaic / multi-image augmentation | Off or low probability for final epochs; compare with and without because synthetic composites can shift the image domain |
| Geometric augmentation | Scale 0.5-1.5, translation 0-0.1, horizontal flip p 0.5 when valid; keep boxes transformed consistently |
| Color augmentation | HSV/hue and brightness/saturation around 0.1-0.3; avoid unrealistic shifts |
| Multi-scale training | Approximately 0.8-1.2x base resolution if supported and affordable |

Do not tune detector assignment/loss internals across architectures as if they
were equivalent. Start from the implementation's documented recipe, change
one group of parameters at a time, and preserve the best validation
checkpoint. Assess AP at IoU 0.5 and COCO-style mAP (0.5:0.95), per-class
recall/precision, and performance on small/occluded ingredients.

## Deployment-focused tuning

| Parameter / choice | Values to compare |
|---|---|
| Export input size | At least the training resolution and one smaller practical size |
| Quantization | Float baseline vs dynamic range / float16 where supported vs full integer INT8 |
| INT8 calibration | Representative, labeled/unlabeled production-like images; start with several hundred diverse images |
| Quantization-aware training | Fine-tune for 5-30 epochs if post-training INT8 loses material accuracy |
| Post-processing | Match box decoding, confidence threshold, class-aware NMS, and maximum detections across framework and app |
| Measurements | Model file size, peak memory, cold start, median and p95 end-to-end latency, and task metric on actual target devices |

Prefer per-channel weight quantization when the export/runtime supports it.
Verify that input normalization, channel order, resize/letterbox, and output
decoding are identical between validation and the app. Quantized accuracy
must be re-evaluated using the exported artifact, not inferred from the
floating-point checkpoint.

## Suggested search strategy

1. Fix the split, taxonomy, and preprocessing; run one documented baseline.
2. Do a coarse LR sweep (for example, 1e-5, 3e-5, 1e-4, 3e-4 for pretrained
   models), then narrow around the best stable region.
3. Tune input size, augmentation strength, and class-imbalance handling in
   separate controlled experiments.
4. Train at least three seeds for leading configurations. Select using
   validation quality plus measured mobile constraints.
5. Evaluate once on the held-out test set; then repeat evaluation after
   export/quantization and document any change.
