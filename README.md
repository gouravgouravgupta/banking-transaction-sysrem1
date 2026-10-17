# Banking Transaction Management System

Java 8 | Spring Boot 2.7 | Hibernate/JPA | H2 (default) or PostgreSQL | Multithreading

## What it demonstrates
- **Secure fund transfer**: atomic debit+credit in one DB transaction, BigDecimal money, validation, failed attempts audited via REQUIRES_NEW.
- **Multithreading**: `BatchTransferService` runs transfers concurrently on a fixed `ExecutorService` (CompletableFuture).
- **Deadlock-safe locking**: pessimistic row locks, always acquired in sorted account-number order.
- **Singleton**: `TransactionReferenceGenerator` (thread-safe holder idiom).
- **DAO**: `AccountDao` / `TransactionDao` interfaces + `EntityManager` implementations.
- **DB/JVM optimisation**: indexes on transaction account columns, JDBC batching, pool sized for workers, paginated/limited history.

## Run
    mvn spring-boot:run                                      # H2 in-memory
    mvn spring-boot:run -Dspring-boot.run.profiles=postgres  # PostgreSQL
    mvn test                                                 # concurrency + failure tests

Suggested JVM flags for load testing: `-Xms512m -Xmx512m -XX:+UseG1GC`

## Endpoints
| Method | Path                                   | Body                                        |
|--------|----------------------------------------|---------------------------------------------|
| POST   | /api/accounts                          | {"holderName":"Asha","openingBalance":1000} |
| GET    | /api/accounts/{no}                     |                                             |
| GET    | /api/accounts/{no}/transactions?limit= |                                             |
| POST   | /api/transactions/deposit              | {"accountNumber":"AC..","amount":100}       |
| POST   | /api/transactions/withdraw             | {"accountNumber":"AC..","amount":50}        |
| POST   | /api/transactions/transfer             | {"fromAccount":"AC..","toAccount":"AC..","amount":25} |
| POST   | /api/transactions/batch-transfer       | [ transfer objects ]  (processed in parallel) |

Note: no authentication is included in this project (add Spring Security/JWT if you want it for the demo).
