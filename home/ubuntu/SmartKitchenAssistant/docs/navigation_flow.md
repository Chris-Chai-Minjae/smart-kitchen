# 스마트 요리 보조 시스템 네비게이션 플로우

## 개요

스마트 요리 보조 시스템의 네비게이션 구조는 사용자가 요리 과정에서 필요한 다양한 기능에 쉽게 접근할 수 있도록 설계되었습니다. 주요 화면 간의 이동과 상호작용 흐름을 명확히 정의하여 직관적인 사용자 경험을 제공합니다.

## 주요 네비게이션 구성요소

### 1. 하단 내비게이션 바

앱의 주요 섹션에 빠르게 접근할 수 있는 기본 네비게이션 수단입니다.

- **홈**: 앱의 시작점, 추천 레시피, 오늘의 식단, 주요 알림 표시
- **레시피**: 레시피 검색, 브라우징, 카테고리별 분류
- **타이머**: 다중 타이머 관리 및 제어
- **재고**: 식품 재고 관리, 바코드 스캔, 쇼핑 목록
- **계획**: 식단 계획 및 영양 분석

### 2. 앱 바 (상단)

- 현재 화면 제목 표시
- 검색, 설정, 공유 등의 액션 버튼 제공
- 뒤로 가기 기능

### 3. 탭 레이아웃

특정 화면 내에서 관련 콘텐츠를 카테고리별로 구분하여 표시합니다.

- **레시피 화면**: 추천, 최근, 즐겨찾기, 카테고리별
- **레시피 상세 화면**: 재료, 단계, 영양 정보
- **재고 화면**: 전체, 냉장고, 냉동실, 식료품장

## 상세 네비게이션 플로우

### 1. 앱 시작 및 온보딩

```
앱 실행
└── 첫 실행 시
    ├── 온보딩 화면 1: 앱 소개
    ├── 온보딩 화면 2: 주요 기능 설명
    ├── 온보딩 화면 3: 블루투스 기기 연결 안내
    ├── 온보딩 화면 4: 음성 명령 설정
    └── 사용자 프로필 설정
        └── 홈 화면
└── 이후 실행 시
    └── 홈 화면
```

### 2. 홈 화면에서의 네비게이션

```
홈 화면
├── 추천 레시피 카드 선택
│   └── 레시피 상세 화면
├── 오늘의 식단 선택
│   └── 식단 상세 화면
├── 재고 알림 선택
│   └── 재고 관리 화면
├── 검색 버튼
│   └── 검색 화면
└── 설정 버튼
    └── 설정 화면
```

### 3. 레시피 탐색 및 요리 과정

```
레시피 화면
├── 레시피 목록
│   └── 레시피 상세 화면
│       ├── 탭: 재료
│       │   └── 재고 확인
│       │       └── 쇼핑 목록에 추가
│       ├── 탭: 단계
│       ├── 탭: 영양 정보
│       ├── 시작하기 버튼
│       │   └── 요리 진행 화면
│       │       ├── 단계 이동 (이전/다음)
│       │       ├── 타이머 제어
│       │       └── 완료 버튼
│       │           └── 요리 완료 화면
│       │               └── 홈 화면
│       └── 식단 계획에 추가 버튼
│           └── 식단 계획 선택 화면
│               └── 식단 계획 화면
└── 필터/카테고리 선택
    └── 필터링된 레시피 목록
```

### 4. 타이머 관리

```
타이머 화면
├── 타이머 추가 버튼
│   └── 타이머 설정 화면
│       └── 타이머 화면 (새 타이머 추가됨)
├── 활성 타이머 카드
│   ├── 일시정지/재개 버튼
│   └── 취소 버튼
└── 타이머 프리셋 선택
    └── 타이머 화면 (프리셋 타이머 추가됨)
```

### 5. 재고 관리 및 쇼핑

```
재고 화면
├── 재고 항목 선택
│   └── 재고 항목 상세/편집 화면
│       └── 재고 화면 (업데이트됨)
├── 항목 추가 버튼
│   ├── 수동 입력 화면
│   │   └── 재고 화면 (항목 추가됨)
│   └── 바코드 스캔 화면
│       └── 제품 정보 확인 화면
│           └── 재고 화면 (항목 추가됨)
├── 쇼핑 목록 버튼
│   └── 쇼핑 목록 화면
│       ├── 항목 체크/해제
│       ├── 항목 추가
│       └── 전체 구매 완료 버튼
│           └── 재고 자동 업데이트
└── 필터 탭 선택
    └── 필터링된 재고 목록
```

### 6. 식단 계획

```
식단 계획 화면
├── 날짜 선택
│   └── 선택된 날짜의 식단
├── 식단 추가 버튼
│   └── 식사 유형 선택 (아침/점심/저녁/간식)
│       └── 레시피 검색/선택 화면
│           └── 식단 계획 화면 (업데이트됨)
├── 기존 식단 항목 선택
│   └── 식단 항목 편집 화면
│       └── 식단 계획 화면 (업데이트됨)
└── 쇼핑 목록 생성 버튼
    └── 기간 선택 화면
        └── 생성된 쇼핑 목록 화면
```

### 7. 설정 및 기기 연결

```
설정 화면
├── 프로필 관리
│   └── 프로필 편집 화면
├── 블루투스 기기 관리
│   └── 기기 연결 화면
│       ├── 기기 스캔
│       ├── 기기 연결/해제
│       └── 기기 설정
├── 음성 인식 설정
│   └── 음성 명령 설정 화면
│       ├── 명령어 커스터마이징
│       └── 음성 인식 테스트
└── 앱 환경설정
    ├── 알림 설정
    ├── 테마 설정
    ├── 데이터 백업/복원
    └── 앱 정보
```

## 컨텍스트 인식 네비게이션

사용자의 현재 상황과 작업에 맞춰 적절한 네비게이션 옵션을 제공합니다.

### 요리 중 네비게이션

요리 진행 화면에서는 일반적인 하단 내비게이션 바 대신 요리 관련 컨트롤에 집중된 UI를 제공합니다:

```
요리 진행 화면
├── 단계 이동 (이전/다음)
├── 타이머 제어
├── 재료 확인 버튼
│   └── 재료 오버레이 표시
├── 기술 비디오 버튼
│   └── 비디오 오버레이 재생
└── 종료 버튼
    └── 종료 확인 대화상자
        ├── 확인 → 홈 화면
        └── 취소 → 요리 진행 화면
```

### 음성 명령 네비게이션

음성 명령을 통한 네비게이션 흐름:

```
음성 명령 "레시피 찾아줘"
└── 음성 검색 활성화
    └── 검색 결과 화면

음성 명령 "다음 단계"
└── 요리 진행 화면에서 다음 단계로 이동

음성 명령 "타이머 5분 설정"
└── 타이머 생성 및 시작
```

## 딥 링크 및 외부 네비게이션

앱 외부에서의 진입점을 정의합니다:

```
알림 선택
├── 타이머 완료 알림 → 타이머 화면
├── 유통기한 임박 알림 → 해당 재고 항목 상세
└── 오늘의 식단 알림 → 오늘의 식단 화면

공유 링크 열기
└── 공유된 레시피 상세 화면

위젯 선택
├── 타이머 위젯 → 타이머 화면
├── 오늘의 식단 위젯 → 오늘의 식단 화면
└── 빠른 레시피 위젯 → 선택된 레시피 상세 화면
```

## 오류 및 예외 처리 네비게이션

오류 상황에서의 사용자 흐름을 정의합니다:

```
연결 오류 발생
└── 오류 화면
    ├── 재시도 버튼 → 이전 작업 재시도
    └── 오프라인 모드 전환 → 제한된 기능의 홈 화면

블루투스 기기 연결 실패
└── 연결 문제 해결 화면
    ├── 문제 해결 단계 안내
    └── 설정으로 이동 버튼 → 블루투스 설정 화면
```

## 접근성 네비게이션

접근성 기능을 사용하는 사용자를 위한 대체 네비게이션 경로:

```
TalkBack 활성화 시
└── 화면 요소 순차적 접근
    └── 요소 선택 → 해당 액션 수행

음성 제어 전용 모드
└── 모든 UI 요소에 음성 명령으로 접근
    └── 명령어 안내 항상 표시
```

## 네비게이션 구현 기술

### Android Jetpack Navigation Component

Navigation Component를 사용하여 화면 간 이동을 관리합니다:

```xml
<!-- nav_graph.xml 예시 -->
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name="com.smartkitchen.assistant.ui.home.HomeFragment"
        android:label="홈">
        <action
            android:id="@+id/action_homeFragment_to_recipeDetailFragment"
            app:destination="@id/recipeDetailFragment" />
    </fragment>

    <fragment
        android:id="@+id/recipeDetailFragment"
        android:name="com.smartkitchen.assistant.ui.recipe.RecipeDetailFragment"
        android:label="레시피 상세">
        <argument
            android:name="recipeId"
            app:argType="long" />
        <action
            android:id="@+id/action_recipeDetailFragment_to_cookingFragment"
            app:destination="@id/cookingFragment" />
    </fragment>

    <fragment
        android:id="@+id/cookingFragment"
        android:name="com.smartkitchen.assistant.ui.cooking.CookingFragment"
        android:label="요리 진행">
        <argument
            android:name="recipeId"
            app:argType="long" />
    </fragment>
    
    <!-- 추가 화면들... -->
</navigation>
```

### 네비게이션 패턴

1. **Single Activity, Multiple Fragments**: 하나의 MainActivity와 여러 Fragment로 구성
2. **BottomNavigationView**: 주요 섹션 간 이동
3. **ViewPager2 + TabLayout**: 관련 콘텐츠 그룹 내 이동
4. **NestedNavigation**: 섹션별 독립적인 네비게이션 그래프

### 전환 애니메이션

자연스러운 화면 전환을 위한 애니메이션 정의:

```xml
<!-- 슬라이드 전환 예시 -->
<slide
    android:duration="300"
    android:interpolator="@android:interpolator/fast_out_slow_in"
    android:slideEdge="right" />
```

## 네비게이션 상태 관리

### 딥 링크 처리

```kotlin
// 딥 링크 처리 예시
val navController = findNavController(R.id.nav_host_fragment)
val navInflater = navController.navInflater
val graph = navInflater.inflate(R.navigation.nav_graph)

// 딥 링크 데이터 추출
intent.data?.let { uri ->
    when {
        uri.pathSegments.contains("recipe") -> {
            val recipeId = uri.lastPathSegment?.toLongOrNull() ?: return@let
            val args = Bundle().apply {
                putLong("recipeId", recipeId)
            }
            navController.navigate(R.id.recipeDetailFragment, args)
        }
        // 다른 딥 링크 케이스 처리
    }
}
```

### 백 스택 관리

```kotlin
// 백 스택 관리 예시
navController.navigate(R.id.action_homeFragment_to_recipeDetailFragment, args) {
    // 이전 화면을 백 스택에서 제거하여 중복 방지
    popUpTo(R.id.homeFragment) {
        inclusive = false
    }
}
```

## 네비게이션 테스트 계획

1. **사용자 플로우 테스트**: 주요 사용 사례에 따른 화면 이동 검증
2. **백 스택 테스트**: 뒤로 가기 동작 및 상태 유지 확인
3. **딥 링크 테스트**: 외부 링크 및 알림을 통한 진입 검증
4. **회전 및 구성 변경 테스트**: 화면 회전 시 네비게이션 상태 유지 확인
5. **접근성 테스트**: 스크린 리더 및 음성 명령을 통한 네비게이션 검증
