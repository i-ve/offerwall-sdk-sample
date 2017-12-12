# offerwall-sdk-sample
IVE 오퍼월 SDK Sample 프로젝트입니다.

## 1. 프로젝트 설정
### 1-1. 프로젝트의 gradle.properties 파일의 repositories에 다음의 maven 저장소를 추가합니다.
```groovy
maven { url "https://raw.githubusercontent.com/i-ve/offerwall-sdk-mvn-repo/master/releases" }
```

최종 적용된 코드는 다음과 비슷하게 될 것 입니다.
```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://raw.githubusercontent.com/i-ve/offerwall-sdk-mvn-repo/master/releases" }
    }
}
```
주의 : 하나의 프로젝트에는 보통 2개 이상의 gradle.properties 파일이 있는데, 이 중 *모듈이 아닌 프로젝트*의 gradle.properties 파일을 수정해야합니다.

### 1-2. 모듈의 gradle.properties 파일의 dependencies에 다음 내용을 추가합니다.

```groovy
compile 'kr.ive:offerwall_sdk:1.0.6'
```
gradle 파일을 수정하게 되면 Android Studio에서 `Sync Now`버튼이 보이게 됩니다. 
Sync를 하게 되면 메이븐 저장소에서 오퍼월 SDK 라이브러리를 다운로드 받게 됩니다.(로컬 저장소에 다운받기 때문에 프로젝트에서는 볼 수 없습니다)

### 1-3. AndroidManifest.xml 파일의 application안에 다음 내용을 추가합니다.
```xml
<meta-data android:name="ive_sdk_appcode" android:value="TIdKKXBq9C" />
<meta-data android:name="ive_sdk_offerwall_title" android:value="오퍼월 테스트 프로젝트" />
```
* `ive_sdk_app_code`의 값에는 아이브에서 발급받은 앱코드를 삽입합니다. 위에서 사용하고 있는 값은 테스트를 위한 용도입니다.
* `ive_sdk_offerwall_title`의 값에는 오퍼월 액티비티의 제목으로 사용할 텍스트를 넣습니다.

## 2. 오퍼월 SDK 사용하기
### 2-1. 오퍼월 Activity 열기

#### 2-1-1. IveOfferwall.openActivity()

오퍼월 `Activity`를 여는 기본적인 방법입니다.

```java
IveOfferwall.openActivity(activity, userId);
```

#### 2-1-2. IveOfferwall.openActivityForResult()

다음의 코드를 사용하면 오퍼월 `Activity`가 `Destroy`될 때 `onActivityResult()` 콜백을 받을 수 있습니다.

```java
IveOfferwall.openActivityForResult(activity, userId, requestCode);
```

위 메서드에서 3번째 인자인 `requestCode`는 `onActivityResult()`에서 첫번째 인자인 `requestCode`로 넘어 갑니다.

### 2-2. 오퍼월 Fragment 생성

```java
IveOfferwall.createFragment(context, userId)
```
- 리턴값 : 생성된 `Fragment`

### 2-3. 유저 포인트 얻기

```java
IveOfferwall.getPoint(context, userId, IveOfferwall.GetPointListener);
```
유저가 획득한 포인트는 비동기 방식으로 받아와서 `IveOfferwall.GetPointListener`로 전달됩니다.

#### 2-3-1. IveOfferwall.GetPointListener 인터페이스
아이브 서버로부터 유저 포인트를 가져옵니다.

```java
public void onGetPointComplete(boolean isSuccess, long point, String errorMessage)
```
* isSuccess : 성공 여부
* point : 포인트
* errorMessage : 실패인 경우 에러 메시지

### 2-4. 유저 포인트 사용하기
```java
IveOfferwall.usePoint(context, userId, point, IveOfferwall.UsePointListener);
```
point만큼의 포인트를 사용합니다. 사용 결과는 `IveOfferwall.UsePointListener`를 통해 전달됩니다.

#### 2-4-1. IveOfferwall.UsePointListener 인터페이스
아이브 서버로부터 유저 포인트 사용 결과를 받습니다.
```java
public void onUsePointComplete(boolean isSuccess, long remainPoint, String errorMessage);
```
* isSuccess : 성공 여부
* remainPoint : 사용 후 남은 포인트
* errorMessage : 실패인 경우 에러 메시지

## 3. Trouble Shooting

### 3-1. support 라이브러리를 찾을 수 없는 경우

```groovy
Failed to resolve: com.android.support:appcompat-v7:25.4.0
Install Repository and sync project
Show in File
Show in Project Structure dialog
```

gradle sync 중에 위와 비슷한 메시지를 보게 된다면, 프로젝트의 `build.gradle` 파일의 `repositories`에 다음 내용을 추가합니다.

```groovy
maven { url "https://maven.google.com" }
```

### 3-2. support 라이브러리의 버전 문제

프로젝트의 `build.gradle` 파일에 `dependencies` 부분에 특정 support 라이브러리에 빨간 밑줄이 뜨며, 마우스를 가져가면 다음과 같은 메시지가 뜨는 경우가 발생할 수 있습니다.

```groovy
All com.android.support libraries must use the exact same version specification (mixing versions can lead to runtime crashes). Found versions 27.0.0, 25.3.1. Examples include com.android.support:animated-vector-drawable:27.0.0 and com.android.support:design:25.3.1 less... (⌘F1) 
There are some combinations of libraries, or tools and libraries, that are incompatible, or can lead to bugs. One such incompatibility is compiling with a version of the Android support libraries that is not the latest version (or in particular, a version lower than your targetSdkVersion.)
```

이는 모든 support 라이브러리가 같은 버전을 사용해야 함을 의미합니다.

이 문제를 해결하기 위해서는 먼저 디펜던시에 대한 트리를 확인해야 합니다.

Android Studio에 있는 Terminal에서 다음 명령을 입력합니다.

```shell
./gradlew -q dependencies app:dependencies --configuration compile
```

위 명령 중 app은 프로젝트의 모듈명이 되어야 합니다.

위 명령의 결과로 터미널 상에 디펜던시 트리를 볼 수 있고, 그 중 일부만 살펴보겠습니다.

```shell
 +--- com.android.support:appcompat-v7:25.3.1 -> 27.0.0 (*)
 \--- com.android.support:design:25.3.1
```

`com.android.support:appcompat-v7`의 경우는 `25.3.1` 버전이었는데 `27.0.0`을 사용하도록 자동 변경되었는데, `com.android.support:design`의 경우는 `25.3.1`을 그대로 사용하고 있습니다.

이로 인해서 2가지(`25.3.1`과 `27.0.0`) 버전의 support 라이브러리가 혼재돼 있는 문제가 발생한 것이므로 이를 해결해주기 위해서는 `build.gradle`의 `dependencies`에 위 결과 트리에서 낮은 버전으로 표기돼 있는 라이브러리들을 모두 추가해서 명시적으로 높은 버전을 적어 줍니다.

이 경우에는 다음 내용을 `dependencies`에 추가하면 됩니다.

```groovy
compile 'com.android.support:design:27.0.0'
```

필요한 디펜던시를 모두 명시적으로 추가한 뒤에 gradle sync를 수행하면 이 문제를 해결할 수 있습니다.

## 4. SDK 변경 이력

### v 1.0.4

* 서버에서 설정된 포인트 명을 받아와서 보여주도록 변경

### v 1.0.3

* 라이브러리 호환성을 위해 support 라이브러리 버전을 25.4.0으로 변경 

### v 1.0.2

* IveOfferwall.openActivityForResult() 메서드 추가
* support 라이브러리 버전을 27.0.0으로 변경
* 버그 수정