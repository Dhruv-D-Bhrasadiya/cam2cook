# Models

In this project the models used will be in part. The focus of this project is to build a GEMINI LIVE like feature but for cook, this will be done in multiple stages.

1. Build a object detection model to detect the ingredients.
2. Build a model to give recieps based on the ingredients
3. Build a VLA(Vision Language Action) model to give guidance based on ingredients, recieps and real time kitchen seen and guide the user to cook the recieps

## Breakdown of all the above model

All the model will be trained and no API's will be used, and will try to make it usable on-device.

### 1. Object Detection model ingredients

* This will be a object detection model where the task will be to detect ingredients, assign weight of individual ingredient, give proteins and other aspect of the the ingredients
* Thinking of scraping the full dataset from the internet and storing it in kaggle/drive/roboflow (NOT DECIDED YET) 
* This model should 
    - detect ingredients
