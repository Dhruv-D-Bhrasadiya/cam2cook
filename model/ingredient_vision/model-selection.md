# Model selection for ingredient recognition

## Decide the output task first

For a camera view containing multiple ingredients, compare **object
detectors** and evaluate box-level precision/recall and mAP. A classifier
cannot say where each ingredient is. If the user photographs one item at a
time, compare image classifiers and top-k accuracy instead. Instance
segmentation is a later option when overlapping objects need precise masks;
it costs more annotation and on-device compute.

No architecture compensates for a taxonomy mismatch, weak labels, or a
training set unlike the phone-camera domain. Establish a kitchen-image
benchmark before selecting the winner. Keep the same data splits, input
resolution, augmentation budget, and evaluation protocol across candidates.

## Candidate matrix

| Family / candidate | Use in comparison | Strengths | Watch-outs | Paper / implementation |
|---|---|---|---|---|
| MobileNetV3 Small / Large | Classifier baseline; small backbone option for a detector | Designed for mobile inference; widely supported | Small models may miss subtle classes; accuracy depends on input size and domain | [Paper](https://arxiv.org/abs/1905.02244) · [TensorFlow Models](https://github.com/tensorflow/models/tree/master/official/vision) |
| MobileNetV4 (U / Hybrid) | Newer mobile classification/backbone candidate | Mobile-oriented architecture family; includes hardware-aware variants | Check framework/export support for the specific variant and target runtime | [Paper](https://arxiv.org/abs/2404.10518) · [TensorFlow Models](https://github.com/tensorflow/models/tree/master/official/vision) |
| EfficientNet-Lite0 | Compact classifier / feature extractor | Mobile inference and TensorFlow Lite deployment are established use cases | Compare measured latency; FLOPs alone do not predict phone performance | [EfficientNet paper](https://arxiv.org/abs/1905.11946) · [TensorFlow Lite image classification guide](https://www.tensorflow.org/lite/examples/image_classification/overview) |
| EfficientNetV2-S | Higher-accuracy classification comparison | Efficient training and strong image-classification performance | Larger than mobile-first baselines; quantization and device latency must be measured | [Paper](https://arxiv.org/abs/2104.00298) · [TensorFlow Models](https://github.com/tensorflow/models/tree/master/official/vision) |
| MobileViT-XXS / XS | Transformer-style compact classifier/backbone | Useful alternative if convolutional baselines plateau | May be less efficient on some mobile runtimes; test memory and operator support | [Paper](https://arxiv.org/abs/2110.02178) · [Apple implementation](https://github.com/apple/ml-cvnets) |
| SSD MobileNetV2 | Small object-detection baseline | Mature TensorFlow Lite / Model Maker path; practical mobile reference | Dense, small or overlapping objects can be difficult | [TensorFlow Lite Model Maker](https://www.tensorflow.org/lite/models/modify/model_maker/object_detection) · [TensorFlow Models](https://github.com/tensorflow/models/tree/master/research/object_detection) |
| EfficientDet-Lite0 | Mobile detection accuracy/size comparison | Built for efficient multi-scale detection; TFLite-oriented variants exist | Deployment/export support differs by training implementation | [EfficientDet paper](https://arxiv.org/abs/1911.09070) · [TensorFlow Lite Model Maker](https://www.tensorflow.org/lite/models/modify/model_maker/object_detection) |
| YOLOX-Nano / YOLOX-Tiny | Real-time detector comparison | Lightweight variants; good speed/accuracy trade-off candidates | Export and preprocessing/NMS must match runtime exactly | [Paper](https://arxiv.org/abs/2107.08430) · [Megvii project](https://github.com/Megvii-BaseDetection/YOLOX) |
| YOLOv8n / YOLO11n | Widely used small detector baselines | Fast experimentation and convenient export tooling | Ultralytics uses AGPL-3.0 or a commercial license; assess obligations before app distribution | [YOLOv8 project](https://github.com/ultralytics/ultralytics) · [YOLO11 docs](https://docs.ultralytics.com/models/yolo11/) |
| RTMDet-tiny | Modern real-time detector alternative | Strong accuracy/speed candidate in the MMDetection ecosystem | Export pipeline may take more work than a TFLite-first model | [Paper](https://arxiv.org/abs/2212.07784) · [OpenMMLab implementation](https://github.com/open-mmlab/mmdetection) |
| CenterNet with a mobile backbone | Anchor-free detector baseline | Simple center-based detection formulation | Test small-object recall and mobile export performance before investing | [Paper](https://arxiv.org/abs/1904.07850) · [TensorFlow Models](https://github.com/tensorflow/models/tree/master/research/object_detection) |

Papers and repositories are linked for reproducibility, not as an endorsement.
Check each project's current license, supported checkpoints, preprocessing,
and export path. Model licenses and dataset licenses are separate. Some
projects support TFLite, ONNX, or Core ML more readily than others; verify
with the exact model/version being considered.

## Practical comparison sequence

1. **Classification sanity check:** MobileNetV3 Small, EfficientNet-Lite0,
   then MobileViT-XXS if there is enough data. This catches class-map and
   preprocessing issues cheaply; it is not the final multi-object solution.
2. **Detection baseline:** SSD MobileNetV2 or EfficientDet-Lite0 using
   reviewed ingredient boxes.
3. **Fast detector challengers:** YOLOX-Nano and one small YOLO variant;
   include RTMDet-tiny only if the implementation/export effort is justified.
4. **Tune resolution and quantization** for the target phone. Compare
   validation mAP/recall, per-class results, model size, peak memory, cold
   start, and end-to-end latency on representative devices.
5. **Pick by product requirements:** missed ingredients may be more costly
   than false detections, and a marginal accuracy gain may not justify a
   larger/slower artifact. Validate the chosen exported model on the held-out
   kitchen test set.

Record exact model variant, framework/version, pretrained checkpoint and its
license, class map, preprocessing, thresholds, export options, and measured
phone/runtime for every result. Use TensorFlow Lite for an Android-first
deployment path and evaluate Core ML conversion on iOS; an ONNX export is
useful for interoperability but is not itself a mobile deployment plan.

### Further techniques after a sound baseline

- Transfer learning from a legally usable pretrained checkpoint.
- Class-balanced sampling or loss for genuinely underrepresented classes.
- Knowledge distillation from a larger detector into a mobile student.
- Post-training integer quantization, followed by quantization-aware training
  if accuracy drops too much.
- Instance segmentation only if boxes cannot support the required user
  experience.

Compare each technique against the same untouched test set and the same
device-side measurement procedure.

## Models currently wired into the terminal pipeline

The first executable MVP comparison is intentionally narrower than the
research candidates above: `configs/pipeline.json` registers **YOLOv8n** and
**YOLO11n** as Ultralytics detectors. The `train --all` command sweeps those
two pretrained checkpoints on datasets marked `sweep: true`. Classification
benchmarks and detector families listed above are not yet training adapters;
do not infer that downloading them makes them compatible with the detection
training command. Check model and framework licenses before distributing
weights or integrating them into a commercial mobile app.
