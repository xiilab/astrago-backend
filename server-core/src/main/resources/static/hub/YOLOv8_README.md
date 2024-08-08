# 1. Outline

### 1.1 Object detection

Object detection은 컴퓨터 비전(Vision)의 한 분야로, 이미지나 영상 내에서 객체의 위치를 식별하고 분류하는 기술입니다.
다양한 응용 분야에 사용되며, 보안 감시, 자율 주행 차량, 얼굴 인식, 제품 검사 등에 사용됩니다.
Object detection 시스템은 일반적으로 두 가지 주요 작업을 수행합니다.
첫째, 이미지 내의 개체를 둘러싸는 경계상자(bounding box)를 탐지하고, 둘째, 각 객체의 종류를 분류합니다.

### 1.2 YOLO Model

YOLO(You Only Look Once)는 Object detection 모델 중 하나로, 높은 속도와 정확도를 가집니다. YOLO의 핵심 아이디어는 이미지를 단 한번만 보고 (Object detection을 위해 이미지를 단일 네트워크 패스를 통해 처리함으로써) 객체의 위치와 분류를 동시에 예측하는 것입니다. 해당 모델은 실시간 처리가 필요한 응용 프로그램에서 특히 유용합니다.

### 1.3 Project Guide

1. 일상적인 문제나 업무상의 문제를 해결하기 위해 이미지나 영상에서 감지하고자 하는 대상(Target)을 선택합니다.
2. 모델 학습을 위한 데이터를 확보합니다. 공개 데이터셋이나 대상이 포함된 이미지와 영상을 수집합니다.
3. 수집된 이미지와 영상을 분석하여 대상을 감지하고 해당 특징을 기록합니다.
4. 노이즈(잔상, 흐림, 초점 문제, 먼지, 반사 등)로 인해 대상의 특징이 정상 데이터에 비해 현저히 낮을 경우, 전처리(preprocessing)과정을 통해 보정하거나 제거합니다.
5. 전처리된 데이터를 분석하여 데이터 가공 기준을 정립하고 필요한 경우 별도의 문서를 작성합니다.
6. 데이터 가공 기준에 따라 수집된 데이터를 가공하고 검수하며, 모델 학습을 위한 입력 형식에 맞게 라벨링 주석을 변경합니다.
7. 모델을 학습하고 결과를 분석합니다.

# 2. 데이터셋 준비

YOLOv8 모델을 학습시키기 위해서는 가공된 학습 데이터가 필요합니다.
학습 데이터는 이미지 파일과 해당 이미지에 대한 객체 검출 대상이 라벨링 된 주석(Annotation) 파일이 필요합니다.

### 2.1 파일 구조

다수의 데이터가 필요하므로 학습을 위한 데이터셋을 다음과 같은 형태로 구성합니다.

1. 데이터셋에 data.yaml 파일 생성

- data.yaml 파일 내에 train, val, test 폴더명을 절대 경로로 입력하고 class_name을 작성합니다.

ex) 아래와 비슷한 형식으로 yaml파일을 작성

```yaml
train: /tmp/datasets/train/images 
val: /tmp/datasets/valid/images
test: /tmp/datasets/test/images

nc: 3
names: ['football', 'player', 'referee']

roboflow:
  workspace: roboflow-100
  project: soccer-players-5fuqs
  version: 2
  license: CC BY 4.0
  url: https://universe.roboflow.com/roboflow-100/soccer-players-5fuqs/dataset/2
```

2. 데이터셋 내에 train, valid, test 폴더로 구성하여 각 폴더에 이미지 및 주석(Annotation) 파일 업로드.

    - train폴더 안의 데이터셋으로 모델의 학습에 사용되고, valid 폴더 안의 데이터 데이터셋으로 모델의 학습 중 성능을 평가하는 데 사용됩니다. 마지막으로 test 폴더 안의 데이터셋으로 모델의 최종 성능을 평가하는 데 사용됩니다.
    - 전체 데이터셋을 Train:Validation:Test = 60%:20%:20%로 분할하는 경우가 많지만, 데이터의 특성과 사용 사례에 따라 비율을 조정할 수 있습니다.

ex) yolov8_sample_datasets의 data.yaml 파일 생성 및 하위폴더로 train, valid, test 구성

```jsx
 yolov8_sample_datasets
├── data.yaml
├── test
│   ├── images
│   │   ├── 0000001000_v1.png
│   │   ├── 0000001001_v1.png
│   │   ├── 0000001002_v1.png
│   │   ├── 0000001003_v1.png
│   │   ├── 0000001004_v1.txt
│   │   └── ...
│   └── labels
│       ├── 0000001000_v1.txt
│       ├── 0000001001_v1.txt
│       ├── 0000001002_v1.txt
│       ├── 0000001003_v1.txt
│       ├── 0000001004_v1.txt
│       └── ...
├── train
│   ├── images
│   │   ├── 0000002000_v1.png
│   │   ├── 0000002001_v1.png
│   │   ├── 0000002002_v1.png
│   │   ├── 0000002003_v1.png
│   │   ├── 0000002004_v1.png
│   │   └── ...
│   └── labels
│       ├── 0000002000_v1.txt
│       ├── 0000002001_v1.txt
│       ├── 0000002002_v1.txt
│       ├── 0000002003_v1.txt
│       ├── 0000002004_v1.txt
│       └── ...
└── valid
    ├── images
    │   ├── 0000003000_v1.png
    │   ├── 0000003001_v1.png
    │   ├── 0000003002_v1.png
    │   ├── 0000003003_v1.png
    │   ├── 0000003004_v1.png
    └── labels
        ├── 0000003000_v1.txt
        ├── 0000003001_v1.txt
        ├── 0000003002_v1.txt
        ├── 0000003003_v1.txt
        ├── 0000003004_v1.txt
        └── ...

```

### 2.2 train, valid, test 폴더에 이미지 및 주석(Annotation) 파일 구성

Yolov8 모델 학습을 위해서는 가공된 학습 데이터가 필요합니다. 학습 데이터는 이미지와 이미지내 검출 대상이 라벨링 된 주석(Annotation) 파일이 필요합니다. 이미지 파일과 주석 파일은 같은 파일 이름으로 1:1로 매칭됩니다.

1. 이미지 파일

- 이미지는 jpg 또는 png 파일을 지원합니다.

ex) park_person.jpg

![park_person](https://raw.githubusercontent.com/xiilab/astrago-hub/master/YOLOv8/images/park_person.png)

2. 주석(Annotation) 파일
   Object detection 모델을 학습할 예정이므로 bounding box형태로 가공하며 가공된 결과는 txt 파일로 아래와 같이 저장합니다.

```jsx
<object-class> <width> <height> <x_center> <y_center> 
0 0.47 0.65 0.1 0.3
```

ex) park_person.txt

```jsx
0 0.47 0.65 0.1 0.3
```

# 3. 학습 파라미터


| 파라미터       | int/float/str | 설명                                                   | 예시                                |
| -------------- | ------------- | ------------------------------------------------------ | ----------------------------------- |
| data_dir       | str           | 데이터셋 경로                                          | /DATA/yolo_dataset/train, val, test |
| image_size     | int           | 입력 이미지 사이즈, 입력한 수치대로 resize             | select: 320 512 640 768 960 1280    |
| epochs         | int           | 전체 데이터셋이 네트워크를 통해 전달되고 학습되는 횟수 | 100                                 |
| batch_size     | int           | select: 1, 2, 4, 8, 16, 32                             | 8                                   |
| learning_rate  | float         | 모델의 가중치를 업데이트하는 데 사용되는 스텝의 크기   | 0.01 or 0.001 …                    |
| save_model_dir | str           | 학습된 모델 저장경로                                   | http://best.pt, last.pt             |

# 4. 결과

1. Metric

Object detection에서 성능 평가 지표는 모델이 얼마나 잘 객체를 탐지하고 분류하는지를 측정하는 데 중요합니다. 주요 평가 지표로는 정밀도(Precision), 재현율(Recall), AP(Average Precision), mAP(mean Average Precision) 등이 있습니다. 이 지표들은 모델이 생성한 예측이 얼마나 정확한지(정밀도), 모델이 실제 객체를 얼마나 잘 찾아내는지(재현율), 그리고 전반적인 성능(평균 정밀도)을 평가합니다.

2. Results

   학습된 결과파일인 모델의 가중치 {저장파일명}.pt는 별도 다운로드 가능합니다.