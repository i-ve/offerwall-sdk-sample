# offerwall-sdk-sample
오퍼월 SDK Sample 프로젝트입니다.

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
compile 'kr.ive:offerwall_sdk:1.1.5'
```
gradle 파일을 수정하게 되면 Android Studio에서 `Sync Now`버튼이 보이게 됩니다. 
Sync를 하게 되면 메이븐 저장소에서 오퍼월 SDK 라이브러리를 다운로드 받게 됩니다.(로컬 저장소에 다운받기 때문에 프로젝트에서는 볼 수 없습니다)

### 1-3. AndroidManifest.xml 파일의 application안에 다음 내용을 추가합니다.
```xml
<meta-data android:name="ive_sdk_appcode" android:value="TIdKKXBq9C" />
<meta-data android:name="ive_sdk_offerwall_title" android:value="오퍼월 테스트 프로젝트" />
```
* `ive_sdk_app_code`의 값에는 발급받은 앱코드를 삽입합니다. 위에서 사용하고 있는 값은 테스트를 위한 용도입니다.
* `ive_sdk_offerwall_title`의 값에는 오퍼월 액티비티의 제목으로 사용할 텍스트를 넣습니다.

## 2. 오퍼월 SDK 사용하기
### 2-1. 유저 데이터<a name="user_data"></a>

오퍼월 Activity나 Fragment를 열때 `IveOfferwall.UserData` 객체를 전달해줘야합니다.

#### 2-1-1. 생성자

`IveOfferwall.UserData` 의 생성자에는 유니크한 유저의 아이디 값을 넣어줘야 합니다.

```java
IveOfferwall.UserData userData = new IveOfferwall.UserData("uniqueUserId");
```

`IveOfferwall.UserData` 에는 유저 아이디 외에도 추가 정보를 넣을 수 있는 setter 메서드가 제공됩니다.

#### 2-1-2. 나이 입력

`IveOfferwall.UserData.setAge()` 메서드는 유저의 나이를 입력하는 메서드입니다. 유저의 나이를 알 수 있는 경우에만 이 메서드를 호출합니다.

```java
userData.setAge(20);
```

#### 2-1-3. 성별 입력

`IveOfferwall.UserData.setSex()` 메서드는 유저의 성별을 입력하는 메서드입니다. 유저의 성별을 알 수 있는 경우에만 이 메서드를 호출합니다.

```java
userData.setSex(IveOfferwall.Sex.FEMALE);
```

성별에 해당하는 값은 `IveOfferwall.Sex` Enum에 있는 `FEMALE` 또는 `MALE` 중 하나의 값을 넣어줍니다.

### 2-2. 오퍼월 Activity 열기

#### 2-2-1. IveOfferwall.openActivity()

오퍼월 `Activity`를 여는 기본적인 방법입니다.

```java
IveOfferwall.openActivity(activity, userData, style);
```

2번째 인자인 userData는  [2-1. 유저 데이터](#user_data) 에서 설명한 유저 데이터 객체입니다. 

3번째 인자인 style은 옵션으로 넣을 수 있습니다. 자세한 내용은 [3. 스타일링](#styling)을 참고해주세요.

#### 2-2-2. IveOfferwall.openActivityForResult()

다음의 코드를 사용하면 오퍼월 `Activity`가 `Destroy`될 때 `onActivityResult()` 콜백을 받을 수 있습니다.

```java
IveOfferwall.openActivityForResult(activity, userData, requestCode, style);
```

위 메서드에서 3번째 인자인 `requestCode`는 `onActivityResult()`에서 첫번째 인자인 `requestCode`로 넘어 갑니다.

4번째 인자인 style은 옵션으로 넣을 수 있습니다. 자세한 내용은 [3. 스타일링](#styling)을 참고해주세요.

### 2-3. 오퍼월 Fragment 생성

```java
IveOfferwall.createFragment(context, userData, style)
```
- 리턴값 : 생성된 `Fragment`

3번째 인자인 style은 옵션으로 넣을 수 있습니다. 자세한 내용은 [3. 스타일링](#styling)을 참고해주세요.

### 2-4. 유저 포인트 얻기

```java
String transactionKey = IveOfferwall.getPoint(context, userData, IveOfferwall.GetPointListener);
```
IveOfferwall.getPoint()를 호출하면 `트랜잭션 키`를 반환합니다. 이 `트랜잭션 키`는 GetPointListener에서 결과를 받을 때 유효성을 검사하는 용도로 사용합니다.

유저가 획득한 포인트는 비동기 방식으로 받아와서 `IveOfferwall.GetPointListener`로 전달됩니다.

#### 2-4-1. IveOfferwall.GetPointListener 인터페이스
아이브 서버로부터 유저 포인트를 가져옵니다.

```java
public void onGetPointComplete(boolean isSuccess, long point, String errorMessage, String hash)
```
* isSuccess : 성공 여부
* point : 포인트
* errorMessage : 실패인 경우 에러 메시지
* hash : 해쉬값

`isSuccess`가 `true`인 경우 [트랜잭션의 유효성을 검사](#validate_transaction)해야합니다.

### 2-5. 유저 포인트 사용하기
```java
String transactionKey = IveOfferwall.usePoint(context, userId, point, IveOfferwall.UsePointListener);
```
IveOfferwall.usePoint()를 호출하면 `트랜잭션 키`를 반환합니다. 이 `트랜잭션 키`는 UsePointListener에서 결과를 받을 때 유효성을 검사하는 용도로 사용합니다.

point만큼의 포인트를 사용합니다. 사용 결과는 `IveOfferwall.UsePointListener`를 통해 전달됩니다.

#### 2-5-1. IveOfferwall.UsePointListener 인터페이스
아이브 서버로부터 유저 포인트 사용 결과를 받습니다.
```java
public void onUsePointComplete(boolean isSuccess, long remainPoint, String errorMessage, String hash);
```
* isSuccess : 성공 여부
* remainPoint : 사용 후 남은 포인트
* errorMessage : 실패인 경우 에러 메시지
* hash : 해쉬값

`isSuccess`가 `true`인 경우 [트랜잭션의 유효성을 검사](#validate_transaction)해야합니다.

### 2-6. 트랜잭션의 유효성 검사<a name="validate_transaction"></a>

`IveOfferwall.getPoint()` 나 `IveOfferwall.usePoint()` 를 하는 경우, 트랜잭션이 유효한지 검사해야합니다.

getPoint와 usePoint 둘다 체크하는 방법은 동일하므로, getPoint를 기준으로 설명을 하겠습니다.

유효성을 검사는 다음 메서드로 하게됩니다.

```java
boolean isValid = IveOfferwall.isValidTransaction(userId, point, transactionKey, hash);
```

* userId : IveOfferwall.getPoint()에 사용한 userId
* point : GetPointListener에서 받은 결과 포인트
* transactionKey : IveOfferwall.getPoint()를 호출하면 즉시 반환되는 트랜잭션 키
* hash : GetPointListener에서 결과와 함께 받는 해쉬값

위 메서드의 결과 값이 `true`인 경우에만 유효한 트랜잭션이므로 정상적인 처리를 진행하시면 됩니다.

### 3. 스타일링<a name="styling"></a>

`IveOfferwall.openActivity()`나 `IveOfferwall.createFragment()` 등의 메서드를 사용할 때 마지막 인자인 `IveOfferwallStyle`을 추가해 줌으로써 커스텀 스타일링을 할 수 있습니다.

스타일링 설정 방법은 액티비티와 프래그먼트가 동일하므로 액티비티를 기준으로 설명하겠습니다.

`IveOfferwall.openActivity()` 메서드는 `activity`, `userId`를 인자로 받는데, 옵션으로 마지막 인자에 `style`을 추가할 수 있습니다.

즉, 다음과 같이 사용함으로써 기본 스타일을 이용하거나

```java
IveOfferwall.openActivity(activity, userId);
```

또는 다음과 같이 사용함으로써 커스텀 스타일을 사용할 수 있습니다.

```java
IveOfferwall.openActivity(activity, userId, style);
```

IveOfferwallStyle 객체를 설정하는 예제는 다음과 같습니다.

```java
IveOfferwallStyle style = new IveOfferwallStyle();
style.setColor(IveOfferwallStyle.Color.STATUS_BAR, ContextCompat.getColor(this, android.R.color.holo_blue_dark));
style.setColor(IveOfferwallStyle.Color.TOOL_BAR_BG, ContextCompat.getColor(this, android.R.color.holo_blue_light));
style.setColor(IveOfferwallStyle.Color.TOOL_BAR_TEXT, ContextCompat.getColor(this, android.R.color.white));
style.setColor(IveOfferwallStyle.Color.BUTTON_BG, ContextCompat.getColor(this, android.R.color.widget_edittext_dark));
style.setColor(IveOfferwallStyle.Color.BUTTON_TEXT, ContextCompat.getColor(this, android.R.color.background_light));
style.setColor(IveOfferwallStyle.Color.ACCENT_TEXT, ContextCompat.getColor(this, android.R.color.holo_blue_light));
style.setType(IveOfferwallStyle.Type.NORMAL);

 IveOfferwall.openActivity(activity, userId, style);
```

위 예제를 보면 알 수 있듯이  `style.setType()`메서드를 통해서 오퍼월의 타입을 변경할 수 있고, `style.setColor()` 메서드를 통해서 각 부분의 스타일을 변경할 수 있습니다.



### 3-1. 오퍼월 타입

오퍼월의 타입은 `IveOfferwallStyle.Type.BIG` 과 `IveOfferwallStyle.Type.NORMAL` 2가지 타입을 지원합니다.

위에서 생성한 `IveOfferwallStyle` 객체에 `setType()` 메서드를 이용해서 오퍼월의 타입을 설정해줄 수 있습니다.

| 타입                          | 설명                    |
| ----------------------------- | ----------------------- |
| IveOfferwallStyle.Type.NORMAL | 일반적인 오퍼월 스타일  |
| IveOfferwallStyle.Type.BIG    | 큰 이미지 오퍼월 스타일 |



### 3-2. 색상 키

변경 가능한 색상키는 다음과 같습니다.

| 키                               | 설명                                                         |
| -------------------------------- | ------------------------------------------------------------ |
| IveOfferwall.Color.STATUS_BAR    | 기기 상단의 상태바 색상을 설정합니다.                        |
| IveOfferwall.Color.TOOL_BAR_BG   | 툴바의 배경색을 설정합니다.                                  |
| IveOfferwall.Color.TOOL_BAR_TEXT | 툴바의 글자색을 설정합니다.                                  |
| IveOfferwall.Color.BUTTON_BG     | 광고 참여 버튼의 배경색을 설정합니다.                        |
| IveOfferwall.Color.BUTTON_TEXT   | 광고 참여 버튼의 글자색을 설정합니다.                        |
| IveOfferwall.Color.ACCENT_TEXT   | 강조 텍스트 색상을 설정합니다.(광고 참여를 누르면 나오는 다이얼로그에서의 강조 글자 등이 있습니다. |

### 3-3. 색상 값

색상 값은 리소스 아이디가 아닌 실제 색상에 해당하는 int 값을 넣어야합니다.

색상 값을 얻을 수 있는 몇가지 방법들을 소개합니다.

#### 3-3-1. ContextCompat.getColor() 사용

`ContextCompat.getColor()`메서드의 두번째 인자에 색상의 리소스 아이디를 넣어서 실제 색상을 구할 수 있습니다.

```java
int color = ContextCompat.getColor(this, android.R.color.widget_edittext_dark);
```

#### 3-3-2. Color 클래스 사용

[Color](https://developer.android.com/reference/android/graphics/Color.html) 클래스에서 색상 값을 얻을 수 있는 몇가지 방법이 있습니다.

```
int textColor = Color.parseColor("#fffefefe");
int bgColor = Color.argb(255, 100, 20, 10);
```





## 4. Trouble Shooting

### 4-1. support 라이브러리를 찾을 수 없는 경우

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

### 4-2. support 라이브러리의 버전 문제

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

## 5. SDK 변경 이력

### v 1.1.5

- AndroidManifest 파일에서 allowBackup 속성을 제거

### v 1.1.4

* 문의화면에서 CPA 광고는 기본 내용을 자동 삽입하도록 수정

### v 1.1.3

* 일부 버그 수정 

### v 1.1.2

* 오퍼월 Activity/Fragment를 열때 userId 값 대신 UserData 객체를 받도록 수정

### v 1.1.0

* 오퍼월 타입을 선택할 수 있는 기능 추가(BIG / NORMAL)
* NORMAL 타입 UI 개선

### v 1.0.9

* getPoint / usePoint 시 트랜잭션 유효성을 체크할 수 있는 기능 추가
* 중요 컴포넌트들의 색상을 변경할 수 있는 스타일링 기능 추가

### v 1.0.8

* 안드로이드 Oreo 대응

### v 1.0.7

* 클릭형 광고에 대한 처리 프로세스 추가 

### v 1.0.4

* 서버에서 설정된 포인트 명을 받아와서 보여주도록 변경

### v 1.0.3

* 라이브러리 호환성을 위해 support 라이브러리 버전을 25.4.0으로 변경 

### v 1.0.2

* IveOfferwall.openActivityForResult() 메서드 추가
* support 라이브러리 버전을 27.0.0으로 변경
* 버그 수정
