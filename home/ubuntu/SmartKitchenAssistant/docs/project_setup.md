# 안드로이드 개발 환경 설정

## 개발 환경 요구사항

### 기본 요구사항
- Android Studio 최신 버전
- JDK 11 이상
- Gradle 7.0 이상
- Android SDK 31 이상 (Android 12)
- 최소 지원 API 레벨: 24 (Android 7.0 Nougat)
- 타겟 API 레벨: 33 (Android 13)

### 필요한 라이브러리 및 종속성
1. **UI 컴포넌트**
   - AndroidX Core KTX: 코틀린 확장 기능
   - AndroidX AppCompat: 이전 버전 호환성
   - Material Design Components: 머티리얼 디자인 UI
   - ConstraintLayout: 복잡한 레이아웃 구성
   - RecyclerView: 목록 표시
   - ViewPager2: 스와이프 가능한 화면
   - Fragment: 화면 모듈화

2. **아키텍처 컴포넌트**
   - ViewModel: UI 데이터 관리
   - LiveData: 데이터 변경 관찰
   - Room: 로컬 데이터베이스 관리
   - Navigation: 화면 간 이동
   - WorkManager: 백그라운드 작업 관리
   - DataStore: 설정 데이터 저장

3. **네트워크 및 데이터 처리**
   - Retrofit2: REST API 통신
   - OkHttp3: HTTP 클라이언트
   - Gson: JSON 파싱
   - Glide: 이미지 로딩 및 캐싱

4. **블루투스 및 IoT 연결**
   - Bluetooth LE 라이브러리
   - Android Nearby API

5. **음성 인식 및 처리**
   - Google Assistant API
   - Speech Recognition API
   - Text-to-Speech API

6. **바코드 스캐닝**
   - ML Kit 바코드 스캐닝
   - ZXing 라이브러리

7. **테스트**
   - JUnit: 단위 테스트
   - Espresso: UI 테스트
   - Mockito: 모킹 프레임워크

## 프로젝트 구조

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/smartkitchen/assistant/
│   │   │   ├── data/
│   │   │   │   ├── local/          # 로컬 데이터베이스
│   │   │   │   ├── remote/         # 원격 API 호출
│   │   │   │   ├── repository/     # 데이터 저장소
│   │   │   │   └── model/          # 데이터 모델
│   │   │   ├── bluetooth/          # 블루투스 연결 관리
│   │   │   ├── voice/              # 음성 인식 및 처리
│   │   │   ├── barcode/            # 바코드 스캐닝
│   │   │   ├── utils/              # 유틸리티 클래스
│   │   │   ├── ui/                 # UI 컴포넌트
│   │   │   │   ├── recipe/         # 레시피 관련 화면
│   │   │   │   ├── timer/          # 타이머 관련 화면
│   │   │   │   ├── inventory/      # 재고 관리 화면
│   │   │   │   ├── mealplan/       # 식단 계획 화면
│   │   │   │   ├── settings/       # 설정 화면
│   │   │   │   └── common/         # 공통 UI 컴포넌트
│   │   │   └── di/                 # 의존성 주입
│   │   └── res/
│   │       ├── layout/             # 레이아웃 XML
│   │       ├── drawable/           # 이미지 리소스
│   │       ├── values/             # 문자열, 색상, 스타일 등
│   │       └── navigation/         # 네비게이션 그래프
│   └── test/                       # 테스트 코드
└── build.gradle                    # 앱 수준 빌드 설정
```

## 빌드 구성

### build.gradle (프로젝트 수준)
```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0'
        classpath 'androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

### build.gradle (앱 수준)
```gradle
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'
    id 'androidx.navigation.safeargs.kotlin'
}

android {
    compileSdkVersion 33
    
    defaultConfig {
        applicationId "com.smartkitchen.assistant"
        minSdkVersion 24
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = '11'
    }
    
    buildFeatures {
        viewBinding true
        dataBinding true
    }
}

dependencies {
    // AndroidX Core
    implementation 'androidx.core:core-ktx:1.10.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    
    // UI Components
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    implementation 'androidx.viewpager2:viewpager2:1.0.0'
    
    // Architecture Components
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.1'
    implementation 'androidx.room:room-runtime:2.5.1'
    implementation 'androidx.room:room-ktx:2.5.1'
    kapt 'androidx.room:room-compiler:2.5.1'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.5.3'
    implementation 'androidx.navigation:navigation-ui-ktx:2.5.3'
    implementation 'androidx.work:work-runtime-ktx:2.8.1'
    implementation 'androidx.datastore:datastore-preferences:1.0.0'
    
    // Network & Data Processing
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.10.0'
    implementation 'com.google.code.gson:gson:2.10'
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    kapt 'com.github.bumptech.glide:compiler:4.15.1'
    
    // Bluetooth & IoT
    implementation 'androidx.bluetooth:bluetooth:1.0.0-alpha01'
    
    // Voice Recognition
    implementation 'com.google.android.gms:play-services-auth:20.5.0'
    implementation 'com.google.android.libraries.assistant:assistant:1.0.0'
    
    // Barcode Scanning
    implementation 'com.google.mlkit:barcode-scanning:17.1.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    testImplementation 'org.mockito:mockito-core:5.2.0'
}
```

## 앱 권한 설정 (AndroidManifest.xml)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.smartkitchen.assistant">

    <!-- 인터넷 접근 권한 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- 블루투스 권한 -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    
    <!-- 위치 권한 (블루투스 LE 스캔에 필요) -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
    <!-- 카메라 권한 (바코드 스캔에 필요) -->
    <uses-permission android:name="android.permission.CAMERA" />
    
    <!-- 마이크 권한 (음성 인식에 필요) -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    
    <!-- 저장소 권한 -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
    <!-- 기기 절전 모드 방지 (요리 중 화면 켜짐 유지) -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
    <!-- 블루투스 기능 필수 선언 -->
    <uses-feature android:name="android.hardware.bluetooth_le" android:required="true" />
    
    <!-- 카메라 기능 필수 선언 -->
    <uses-feature android:name="android.hardware.camera" android:required="true" />
    
    <application
        android:name=".SmartKitchenApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.SmartKitchenAssistant">
        
        <activity
            android:name=".ui.MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>
</manifest>
```
