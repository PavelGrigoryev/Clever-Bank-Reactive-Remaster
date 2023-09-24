# Clever-Bank application

## Author: [Grigoryev Pavel](https://pavelgrigoryev.github.io/GrigoryevPavel/)

### Technologies that I used on the project:

* Java 17
* Gradle 8.2.1
* Lombok plugin 8.2.2
* Postgresql 15.4
* Liquibase plugin 2.2.0
* Jooq plugin 8.2
* Spring-boot-starter-data-r2dbc
* Spring-boot-starter-jooq
* Spring-boot-starter-validation
* Spring-boot-starter-webflux
* Spring-boot-starter-aop
* R2dbc-postgresql 42.6.0
* Testcontainers-postgresql 1.19.0
* Testcontainers-r2dbc 1.19.0
* Spring-boot-starter-test
* Reactor-test
* Mapstruct 1.5.3.Final

### Instructions to run application locally:

1. You must install [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html),
   [Intellij IDEA Ultimate](https://www.jetbrains.com/idea/download/)
   and [Postgresql](https://www.postgresql.org/download/) (P.S: Postgresql can be deployed in docker).
2. In Postgresql you need to create a database. As an example: "clever_bank" . Sql: CREATE DATABASE clever_bank
3. In [application.yaml](src/main/resources/application.yaml) in line № 4 enter your username for Postgresql, in the
   line № 5 enter your password for Postgresql.
4. In [gradle.properties](gradle.properties) in line № 2 enter your username for Postgresql, in line № 3 enter your
   password for Postgresql.
5. When you launch the application, Liquibase will create tables and fill them with default values. Jooq will generate
   the entities. And the scheduler will start which will regularly, according to the schedule, check whether it is
   necessary to accrue interest on the account balance at the end of the month and save the exchange rate from the api
   of the National Bank of the Republic of Belarus.
6. The application is ready to use.

### Http Requests

* [transactions.http](src/main/resources/http/transactions.http) for transactions
* [accounts.http](src/main/resources/http/accounts.http) for accounts
* [banks.http](src/main/resources/http/banks.http) for banks
* [users.http](src/main/resources/http/users.http) for users

### Integration tests

1. 132 integration tests written.
2. You can run tests for this project by running in the project root:

```
./gradlew test
```

### [Bank check](src/main/resources/check/BankCheck.txt)

```text

-------------------------------------------------------------
|                       Банковский чек                      |
| Чек:                                                    5 |
| 2023-09-24                                       15:20:43 |
| Тип транзакции:                                     Обмен |
| Банк отправителя:                                Сбербанк |
| Банк получателя:                              Газпромбанк |
| Счет отправителя:      QR2Q PA57 LB3E LHT3 HCZ2 V4MV XL6M |
| Счет получателя:       19CM 9B6S FFF7 0N1Y M8UY AXCE RMJV |
| Сумма отправителя:                               3000 RUB |
| Сумма получателя:                               29.18 EUR |
-------------------------------------------------------------

-------------------------------------------------------------
|                       Банковский чек                      |
| Чек:                                                    6 |
| 2023-09-24                                       15:20:43 |
| Тип транзакции:                                   Перевод |
| Банк отправителя:                                Сбербанк |
| Банк получателя:                                      ВТБ |
| Счет отправителя:      6RE0 UZ6A 1I3X YK92 MEUR E5GX 13CW |
| Счет получателя:       0UGT 45HU 37CW ZWMQ JZWK 7GLM ZBOT |
| Сумма:                                             20 BYN |
-------------------------------------------------------------

-------------------------------------------------------------
|                       Банковский чек                      |
| Чек:                                                    7 |
| 2023-09-24                                       15:20:43 |
| Тип транзакции:                                Пополнение |
| Банк отправителя:                              Альфа-Банк |
| Банк получателя:                              Газпромбанк |
| Счет получателя:       55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9 |
| Сумма:                                         200.52 EUR |
-------------------------------------------------------------

-------------------------------------------------------------
|                       Банковский чек                      |
| Чек:                                                    8 |
| 2023-09-24                                       15:20:43 |
| Тип транзакции:                                    Снятие |
| Банк отправителя:                              Альфа-Банк |
| Банк получателя:                              Газпромбанк |
| Счет получателя:       ZMEJ L8W1 YNCU JRK6 XOYG Z4R1 IDIJ |
| Сумма:                                        -200.52 EUR |
-------------------------------------------------------------

```

### [Transaction statement](src/main/resources/check/TransactionStatement.txt)

```text

                             Выписка
                            Альфа-Банк
Клиент                         | Орлов Олег Олегович
Счет                           | FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62
Валюта                         | EUR
Дата открытия                  | 2018-03-01
Период                         | 2023-09-23 - 2023-09-23
Дата и время формирования      | 2023-09-24,  15:22:30
Остаток                        | 3500.00 EUR
    Дата      |           Примечание                  |    Сумма
----------------------------------------------------------------------
2023-09-23    | Пополнение      от Орлов              | 255 EUR
2023-09-23    | Снятие          от Орлов              | -255 EUR
2023-09-23    | Перевод         от Орлов              | -20 EUR

```

### [Information about the amount of funds spent and received](src/main/resources/check/AmountStatement.txt)

```text

                  Выписка по деньгам
                      Альфа-Банк
Клиент                         | Орлов Олег Олегович
Счет                           | FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62
Валюта                         | EUR
Дата открытия                  | 2018-03-01
Период                         | 2023-09-20 - 2023-09-20
Дата и время формирования      | 2023-09-21,  18:10:31
Остаток                        | 2997.76 EUR
              Приход      |     Уход
          --------------------------------
               37.76      |          540
                
```

## Functionality

Summery application can:

***

### TransactionController

***

#### POST replenishment and withdrawal of funds from the account

Request:

* account_sender_id = sender's account number
* account_recipient_id = recipient's account number
* sum = amount of money
* type = type, can be REPLENISHMENT or WITHDRAWAL

```json
{
  "account_sender_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "account_recipient_id": "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9",
  "sum": 255,
  "type": "REPLENISHMENT"
}
```

Response Status 201:

```json
{
  "transaction_id": 15,
  "date": "2023-09-24",
  "time": "15:43:53",
  "currency": "EUR",
  "type": "REPLENISHMENT",
  "bank_sender_name": "Альфа-Банк",
  "bank_recipient_name": "Газпромбанк",
  "account_recipient_id": "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9",
  "sum": 255,
  "old_balance": 3265.00,
  "new_balance": 3520.00
}
```

Response Status 400:

```json
{
  "exception": "Insufficient funds in the account! You want to change 3520.01, but you have only 3520.00"
}
```

Response Status 404:

```json
{
  "exception": "Account with ID G5QZ 6B43 A6XG AHNK CO6S PSO6 718 is not found!"
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "field_name": "sum",
      "message": "must be greater than 0"
    },
    {
      "field_name": "type",
      "message": "Available types are: REPLENISHMENT or WITHDRAWAL"
    }
  ]
}
```

#### POST transfer from one account to another

Request:

* account_sender_id = sender's account number
* account_recipient_id = recipient's account number
* sum = amount of money

```json
{
  "account_sender_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "account_recipient_id": "ZMEJ L8W1 YNCU JRK6 XOYG Z4R1 IDIJ",
  "sum": 20
}
```

Response Status 201:

```json
{
  "transaction_id": 16,
  "date": "2023-09-24",
  "time": "15:46:36",
  "currency": "EUR",
  "type": "TRANSFER",
  "bank_sender_name": "Альфа-Банк",
  "bank_recipient_name": "Газпромбанк",
  "account_sender_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "account_recipient_id": "ZMEJ L8W1 YNCU JRK6 XOYG Z4R1 IDIJ",
  "sum": 20,
  "sender_old_balance": 3451.52,
  "sender_new_balance": 3431.52,
  "recipient_old_balance": 1060.00,
  "recipient_new_balance": 1080.00
}
```

Response Status 400:

```json
{
  "exception": "Account with ID 2PT0 PNBG BILW BDZN IYT6 DXU8 8NC5 is closed since 2018-12-31"
}
```

Response Status 404:

```json
{
  "exception": "Account with ID G5QZ 6B43 A6XG AHNK CO6S PSO6 718 is not found!"
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "field_name": "sum",
      "message": "must be greater than 0"
    }
  ]
}
```

#### POST transfer from one account to another with currency exchange at the rate of the National Bank of the Republic of Belarus

Request:

* account_sender_id = sender's account number
* account_recipient_id = recipient's account number
* sum = amount of money

```json
{
  "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "account_recipient_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "sum": 10
}
```

Response Status 201:

```json
{
  "transaction_id": 18,
  "date": "2023-09-24",
  "time": "15:53:46",
  "currency_sender": "BYN",
  "currency_recipient": "EUR",
  "type": "EXCHANGE",
  "bank_sender_name": "Клевер-Банк",
  "bank_recipient_name": "Альфа-Банк",
  "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "account_recipient_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "sum_sender": 10,
  "sum_recipient": 2.88,
  "sender_old_balance": 4460.00,
  "sender_new_balance": 4450.00,
  "recipient_old_balance": 3411.52,
  "recipient_new_balance": 3414.40
}
```

Response Status 400:

```json
{
  "exception": "Account with ID 2PT0 PNBG BILW BDZN IYT6 DXU8 8NC5 is closed since 2018-12-31"
}
```

Response Status 404:

```json
{
  "exception": "Account with ID G5QZ 6B43 A6XG AHNK CO6S PSO6 718 is not found!"
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "field_name": "sum",
      "message": "must be greater than 0"
    }
  ]
}
```

#### POST generating statements of user transactions over a period of time

Request:

* from = from what date to generate the statement
* to = by what date
* account_id = account number

```json
{
  "from": "2023-09-21",
  "to": "2023-09-23",
  "account_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62"
}
```

Response Status 201:

```json
{
  "bank_name": "Альфа-Банк",
  "lastname": "Орлов",
  "firstname": "Олег",
  "surname": "Олегович",
  "account_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "currency": "EUR",
  "opening_date": "2018-03-01",
  "from": "2023-09-21",
  "to": "2023-09-23",
  "formation_date": "2023-09-24",
  "formation_time": "15:57:06",
  "balance": 3414.40,
  "transactions": [
    {
      "date": "2023-09-23",
      "type": "EXCHANGE",
      "user_lastname": "Зайцева",
      "sum_sender": 10,
      "sum_recipient": 2.88
    },
    {
      "date": "2023-09-23",
      "type": "REPLENISHMENT",
      "user_lastname": "Орлов",
      "sum_sender": 255,
      "sum_recipient": 255
    },
    {
      "date": "2023-09-23",
      "type": "WITHDRAWAL",
      "user_lastname": "Орлов",
      "sum_sender": 255,
      "sum_recipient": 255
    },
    {
      "date": "2023-09-23",
      "type": "TRANSFER",
      "user_lastname": "Орлов",
      "sum_sender": 20,
      "sum_recipient": 20
    }
  ]
}
```

Response Status 404:

```json
{
  "exception": "It is not possible to create a transaction statement because you do not have any transactions for this period of time : from 2023-09-20 to 2023-09-20"
}
```

#### POST information about the amount of funds spent and received over a period of time

Request:

* from = from what date to generate the statement
* to = by what date
* account_id = account number

```json
{
  "from": "2023-09-20",
  "to": "2023-09-24",
  "account_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62"
}
```

Response Status 201:

```json
{
  "bank_name": "Альфа-Банк",
  "lastname": "Орлов",
  "firstname": "Олег",
  "surname": "Олегович",
  "account_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "currency": "EUR",
  "opening_date": "2018-03-01",
  "from": "2023-09-20",
  "to": "2023-09-24",
  "formation_date": "2023-09-24",
  "formation_time": "15:59:52",
  "balance": 3414.40,
  "spent_funds": 100,
  "received_funds": 14.40
}
```

Response Status 404:

```json
{
  "exception": "Account with ID G5QZ 643 A6XG AHNK CO6S PSO6 718Q is not found!"
}
```

#### GET find transaction by id

Path variable:

* id = transaction ID

Response Status 200:

```json
{
  "id": 2,
  "date": "2023-09-23",
  "time": "15:32:32",
  "type": "EXCHANGE",
  "bank_sender_id": 2,
  "bank_recipient_id": 5,
  "account_sender_id": "QR2Q PA57 LB3E LHT3 HCZ2 V4MV XL6M",
  "account_recipient_id": "19CM 9B6S FFF7 0N1Y M8UY AXCE RMJV",
  "sum_sender": 3000,
  "sum_recipient": 29.25
}
```

Response Status 404:

```json
{
  "exception": "Transaction with ID 20 is not found!"
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "field_name": "findById.id",
      "message": "must be greater than 0"
    }
  ]
}
```

#### GET find all transactions by account_sender_id

Path variable:

* account_sender_id = senders ID

Request param:

* offset = how much to skip
* limit = how much to show

Response Status 200:

```json
[
  {
    "id": 4,
    "date": "2023-09-23",
    "time": "16:33:09",
    "type": "EXCHANGE",
    "bank_sender_id": 1,
    "bank_recipient_id": 3,
    "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
    "account_recipient_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
    "sum_sender": 10,
    "sum_recipient": 2.88
  },
  {
    "id": 10,
    "date": "2023-09-24",
    "time": "00:30:21",
    "type": "EXCHANGE",
    "bank_sender_id": 1,
    "bank_recipient_id": 3,
    "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
    "account_recipient_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
    "sum_sender": 10,
    "sum_recipient": 2.88
  }
]
```

Response Status 409:

```json
{
  "violations": [
    {
      "field_name": "limit",
      "message": "must be greater than or equal to 1"
    }
  ]
}
```

#### GET find all transactions by account_recipient_id

Path variable:

* account_recipient_id = recipients ID

Request param:

* offset = how much to skip
* limit = how much to show

Response Status 200:

```json
[
  {
    "id": 1,
    "date": "2023-09-23",
    "time": "13:34:02",
    "type": "REPLENISHMENT",
    "bank_sender_id": 3,
    "bank_recipient_id": 5,
    "account_sender_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
    "account_recipient_id": "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9",
    "sum_sender": 255,
    "sum_recipient": 255
  },
  {
    "id": 5,
    "date": "2023-09-23",
    "time": "23:15:42",
    "type": "WITHDRAWAL",
    "bank_sender_id": 3,
    "bank_recipient_id": 5,
    "account_sender_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
    "account_recipient_id": "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9",
    "sum_sender": 255,
    "sum_recipient": 255
  }
]
```

Response Status 409:

```json
{
  "violations": [
    {
      "field_name": "offset",
      "message": "must be greater than or equal to 1"
    }
  ]
}
```
