# offerwall-sdk-sample
IVE 오퍼월 SDK Sample 프로젝트입니다.

## 1. 프로젝트 설정
### 1-1. 프로젝트의 gradle.properties 파일의 repositories에 다음의 maven 저장소를 추가합니다.
```gradle
maven { url "https://raw.githubusercontent.com/i-ve/offerwall-sdk-mvn-repo/master/releases" }
```

최종 적용된 코드는 다음과 비슷하게 될 것 입니다.
```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://raw.githubusercontent.com/i-ve/offerwall-sdk-mvn-repo/master/releases" }
    }
}
```
주의 : 하나의 프로젝트에는 보통 2개 이상의 gradle.properties 파일이 있는데, 이 중 *모듈이 아닌 프로젝트*의 gradle.properties 파일을 수정해야합니다.

### 1-2. 모듈의 gradle.properties 파일의 dependencies에 다음 내용을 추가합니다.

```gradle
compile 'kr.ive:offerwall_sdk:1.0.0'
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
```java
IveOfferwall.openActivity(context, userId);
```

### 2-2. 오퍼월 Fragment 생성

```java
IveOfferwall.createFragment(context, userId)
```
- 리턴값 : 생성된 Fragment

### 2-3. 유저 포인트 얻기

```java
IveOfferwall.getPoint(context, userId, IveOfferwall.GetPointListener);
```
유저가 획득한 포인트는 비동기 방식으로 받아와서 IveOfferwall.GetPointListener로 전달됩니다.

#### IveOfferwall.GetPointListener 인터페이스
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
point만큼의 포인트를 사용합니다. 사용 결과는 IveOfferwall.UsePointListener를 통해 전달됩니다.

#### IveOfferwall.UsePointListener 인터페이스
아이브 서버로부터 유저 포인트 사용 결과를 받습니다.
```java
public void onUsePointComplete(boolean isSuccess, long remainPoint, String errorMessage);
```
* isSuccess : 성공 여부
* remainPoint : 사용 후 남은 포인트
* errorMessage : 실패인 경우 에러 메시지
