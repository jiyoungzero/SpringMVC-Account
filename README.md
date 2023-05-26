# SpringMVC-Account
### 1️⃣ STACK & 기능
- Spring boot와 Java를 활용
- 단위테스트를 작성하여 작성한 코드를 검증
- DB는 H2 DB(memory DB 모드)를 활용
- DB를 접근하는 방법은 Spring data JPA를 활용
- Embedded redis를 활용
- API Request body와 Response body는 json 타입으로 표현
- 각각의 API들은 각자의 요청과 응답 객체 구조를 가짐. 

### 2️⃣ 프로젝트 소개
- Account(계좌) 시스템은 사용자와 계좌의 정보를 저장하고 있으며, 외부 시스템에서 거래를 요청할 경우 거래 정보를 받아서 계좌에서 잔액을 거래금액만큼 줄이거나(결제), 거래금액만큼 늘리는(결제 취소) 거래 관리 기능을 제공하는 시스템입니다.
- 구현의 편의를 위해 사용자 생성 등의 관리는 API로 제공하지 않고 프로젝트 시작 시 자동으로 데이터가 입력되도록 하며, 계좌 추가/해지/확인, 거래 생성/거래 취소/거래 확인의 6가지 API를 제공한다.
* 거래금액을 늘리거나 줄이는 과정에서 여러 쓰레드 혹은 인스턴스에서 같은 계좌에 접근할 경우 동시성 이슈로 인한 lost update가 발생할 수 있으므로 이 부분을 해결해야만 한다.

### 3️⃣ 각 API 명세서

✅ 계좌 관련 API
1) 계좌 생성<br>
a. 파라미터 : 사용자 아이디, 초기 잔액<br>
b. 결과<br>
i. 실패 : 사용자 없는 경우, 계좌가 10개(사용자당 최대 보유 가능 계좌) 인 경우 실패 응답<br>
ii. 성공<br>
  - 계좌번호는 10자리 랜덤 숫자
  - 응답정보 : 사용자 아이디, 생성된 계좌 번호, 등록일시(LocalDateTime)

[ 계좌생성 시 주의사항 ]
- 계좌 생성 시 계좌 번호는 10자리의 정수로 구성되며, 기존에 동일 계좌 번호가 있는지 중복체크를 해야 한다.
- 기본적으로 계좌번호는 순차 증가 방식으로 생성한다.

2) 계좌 해지<br>
a. 파라미터 : 사용자 아이디, 계좌 번호<br>
b. 결과<br>
i. 실패 : 사용자 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우, 계좌가 이미 해지 상태인 경우, 잔액이 있는 경우 실패 응답<br>
ii. 성공<br>
- 응답 : 사용자 아이디, 계좌번호, 해지일시

3) 계좌 확인<br>
a. 파라미터 : 사용자 아이디<br>
b. 결과<br>
i. 실패 : 사용자 없는 경우 실패 응답<br>
ii. 성공<br>
- 응답 : (계좌번호, 잔액) 정보를 Json list 형식으로 응답

✅ 거래(Transaction) 관련 API
1) 잔액 사용<br>
a. 파라미터 : 사용자 아이디, 계좌 번호, 거래 금액<br>
b. 결과<br>
i. 실패 : 사용자 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우, 계좌가 이미 해지 상태인 경우, 거래금액이 잔액보다 큰 경우, 거래금액이 너무 작거나 큰 경우 실패 응답<br>
ii. 성공<br>
- 응답 : 계좌번호, transaction_result, transaction_id, 거래금액, 거래일시

2) 잔액 사용 취소<br>
a. 파라미터 : transaction_id, 계좌번호, 거래금액<br>
b. 결과<br>
i. 실패 : 원거래 금액과 취소 금액이 다른 경우, 트랜잭션이 해당 계좌의 거래가 아닌경우 실패 응답<br>
ii. 성공<br>
- 응답 : 계좌번호, transaction_result, transaction_id, 취소 거래금액, 거래일시

3) 거래 확인<br>
a. 파라미터 : transaction_id<br>
b. 결과<br>
i. 실패 : 해당 transaction_id 없는 경우 실패 응답<br>
ii. 성공<br>
- 응답 : 계좌번호, 거래종류(잔액 사용, 잔액 사용 취소), transaction_result, transaction_id, 거래금액, 거래일시<br>
- 성공거래 뿐 아니라 실패한 거래도 거래 확인할 수 있도록 합니다.
