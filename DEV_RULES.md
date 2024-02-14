# 1. 백엔드 개발규칙

## 1. 개발 규칙

- sonarqube 및 ide의 기능으로 중복 및 코드 오류 사항을 꼭 해결한다.
- SOLID 원칙에 따라 개발한다.
- 동료의 PR을 꼭 확인하고, 코드 리뷰를 진행한다.
- common module에 꼭 필요한 경우가 아니라면 개발하지 않는다.
- test code를 작성한다.
- interface를 설계하고 상속받아 개발한다.

## 2. 네이밍 규칙

### 1. 패키지

- 패키지명은 클래스명과 쉽게 구분하기 위해 **소문자로 작성**한다.
- 패키지명은 가급적 **한 단어의 명사를 사용**한다.

    ```
    ex) 나쁜 예: com.xiilab.project.memberobject
    ex) 좋은 예: com.xiilab.project.member.object
    ```

### 2. 클래스

- 클래스명에는 **파스칼 표기법**을 사용한다.
    - 단어의 첫 시작은 항상 대문자
    ```java
    public class HelloWorld { }
    ```

- 인터페이스에는 특별한 접두사나 접미사를 사용하지 않고 **파스칼 표기법을 사용**한다.
- 인터페이스를 구현한 클래스에는 특별한 접두사나 접미사를 사용하지 않고 **파스칼 표기법을 사용**한다.
- 추상 클래스에는 특별한 접두사나 접미사를 사용하지 않고 파스칼 표기법을 사용한다.

### 3. 메소드

- 메소드명에는 **카멜 표기법을 사용**한다.
    ```java
    public class HelloWorld { 
        public void sendMessage(String message) {}
    }
    ```
- 메소드명은 **동사/전치사로 시작**한다.
- 필드에 접근하는 메소드명의 접두사는 **‘get’, ‘set’을 사용**한다.

    ```java
    public class HelloWorld { 
        public void setMessage() {this.message = message;}
        public String getMessage() { return message; }
    }
    ```

- 데이터를 **조회하는 메소드명의 접두사는 find를 사용**한다.

    ```java
    public class HelloWorld {
        public String findData(String data) {
            String str = "Hello world!";
            return str.indexOf(data);
        }
    }
    ```

    - 데이터를 **입력하는 메소드명의 접두사는 input을 사용**한다.

        ```java
        public class HelloWorld {
            public void inputData(HashMap data){
                data.put("hello", "world");
          }
        }
        ```

- 데이터를 **변경하는 메소드명의 접두사는 modify를 사용**한다.

    ```java
    public class HelloWorld {
        public void modifyData(HashMap data){
            // 값 변경
            data.put("A", "B");
        }
    }
    ```

- 데이터를 **삭제하는 메소드명의 접두사는 delete를 사용**한다.

    ```java
    public class HelloWorld {
        public void deleteData(HashMap<String, String> data, String data){
        data.remove(data);
    }
    }
    ```

- 데이터를 **초기화하는 메소드명의 접두사는 init을 사용**한다.

    ```java
    public class HelloWorld {
        public void initData(String data) {
            data = "";
        }
    }
    ```

- 반환값의 타입이 **boolean인 메소드는 접두사로 is를 사용**한다.

    ```java
    public class HelloWorld {
        public boolean isData(String data) { return data.isEmpty(); }
    }
    ```

- 데이터를 **불러오는 메소드명의 접두사는 load를 사용**한다.
    - get: 필드에 접근
    - find: 전달받은 파라미터로 데이터에서 정보를 찾음

    ```java
    public class HelloWorld {
        public void loadData() {}
    }
    ```

- 데이터가 **있는지 확인하는 메소드명의 접두사는 has를 사용**한다.

    ```java
    public class HelloWorld {
        public boolean hasData() {}
    }
    ```

- 보다 지능적인 **set이 요구될 떄 사용하는 메소드명의 접두사는 register를 사용**한다.

    ```java
    public class HelloWorld {
        public void registerAccount() {}
    }
    ```

- 새로운 객체를 만든 뒤 해당 객체를 리턴해주는 메소드명의 접두사는 create를 사용한다.

    ```java
    public class HelloWorld {
        public void createAccount() {}
    }
    ```

- 해당 객체를 다른 형태의 객체로 변환해주는 메소드명의 접두사는 to를 사용한다.

    ```java
    public class HelloWorld {
        public void toString(){}
    }
    ```

- 해당 객체가 복수인지 단일인지 구분하는 메서드명의 접미사는 s를 사용한다.

    ```java
    public class HelloWorld {
        public void getMembers() {}
    }
    ```

- B를 기준으로 A를 하겠다는 메소드명의 전치사는 By를 사용한다.

    ```java
    public class HelloWorld {
        public void getUserByName(String name) {}
    }
    ```

### 4. 변수

- 변수와 메소드의 파라미터에는 카멜표기법을 사용한다.
- 변수에 약어를 사용하지 않고 모든 의미를 충분히 담는다.
- 한 글자로 된 이름을 사용하지 않는다.
- 선언된 지점에서 초기화하며, 가능한 사용범위를 최소화한다.
- 반복문에서 인덱스로 사용할 변수는 i,j,k 등으로 사용한다.
- 지역변수와 멤버변수는 변수명 앞에 밑줄을 사용하여 구별한다.
- boolean타입의 변수는 접두사로 is를 사용한다
    - Ex) isCheck

### 5. 상수

- 상수는 대문자로 작성하고 복합어인 경우 ‘_’를 사용하여 단어를 구분한다.
    - Ex) public final int SPECIAL_NUMBER = 1;

## 2. PR 전 점검 항목

- 툴에 네이버 코딩 컨벤션인 “캠퍼스 핵데이 Java 코딩 컨벤션”이 적용 되었는가?
    - 캠퍼스 핵데이 Java 코딩 컨벤션 공식문서

    <aside>
    👉 https://naver.github.io/hackday-conventions-java/

    </aside>

    - InteliJ 자바 프로젝트 코딩 컨벤션 적용방법

    <aside>
      👉 [https://velog.io/@nefertiri/인텔리제이-자바-프로젝트-코딩-컨벤션-적용하기](https://velog.io/@nefertiri/%EC%9D%B8%ED%85%94%EB%A6%AC%EC%A0%9C%EC%9D%B4-%EC%9E%90%EB%B0%94-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%BD%94%EB%94%A9-%EC%BB%A8%EB%B2%A4%EC%85%98-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0)

    </aside>

- setter 없이 구현했는가?
    - 핵심 로직을 구현하는 도메인 객체에 setter를 쓰지 않고 구현했는가? 단, DTO는 허용한다.
- 코드 한 줄에 점(.)을 하나만 허용했는가?

    ```java
    public class HelloWorld {
	    public void nonSetter() {
            User user = User.builder()
                    .email(signupRequest.getEmail())
                    .name(signupRequest.getUserName())
                    .password(passwordEncoder.encode(signupRequest.getPassword()))
                    .build();
        }
    }
    ```

- 메소드의 인자 수를 제한했는가?
    - 4개 이상의 인자는 허용하지 않는다. 3개도 가능하면 줄이기 위해 노력해 본다.
- 메소드가 한가지 일만 담당하도록 구현했는가?
- 클래스를 작게 유지하기 위해 노력했는가?
- 메서드당 line을 10까지만 허용
- 길이가 길어지면 메서드로 분리

## 3. Global Exception (전역 예외 처리)

### 1. 사용 이유

- 예외처리가 늘어나면서 예외 처리에 대한 중복 코드가 늘어 유지보수 어려움

### 2. 사용 방법
  기존 예외 처리 부분
  ```java
    throw new IllegalArgumentException("message");
  ```
  다음과 같이 변경
  ```java
    // RestAPI Error
    throw new RestApiException();
    // K8S Error
    throw new K8sException();
    // Common Error
    throw new CommonException();
  ```
### 3. Message 처리 방법

- astrago-backend/module-common/src/main/java/com/xiilab/modulecommon/exception/errorcode
- 위치에 Enum Code 생성 혹은 추가 하여 아래 예시와 같이 사용

### 4. 예시
  ```java
  public static String formatObjectMapper(String metric) {
	try {
		return objectMapper.readTree(metric).get("data").get("result").elements().next().get("value").get(1).asText();
	} catch (JsonProcessingException e) {
		throw new CommonException(CommonErrorCode.DATA_FORMAT_FAIL);
	}
  }
  ```