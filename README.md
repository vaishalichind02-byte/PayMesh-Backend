# PayMesh Backend

Spring Boot + PostgreSQL + Spring Security + JWT.

## Requirements
- Java 17
- Maven
- PostgreSQL

## PostgreSQL
Create a database:
```sql
CREATE DATABASE paymesh;
```
Default connection is:
- host: localhost
- port: 5432
- database: paymesh
- username: postgres
- password: postgres

Change these values in `src/main/resources/application.properties` if needed.

## Run in Spring Tools
1. Import the `paymesh-backend` folder as an Existing Maven Project.
2. Make sure PostgreSQL is running.
3. Run `PayMeshApplication.java` as Spring Boot App.
4. Backend: http://localhost:8080

## Demo API
Register:
POST /api/auth/register
```json
{"name":"Merchant User","email":"merchant@test.com","password":"123456","role":"MERCHANT"}
```

Login:
POST /api/auth/login
```json
{"email":"merchant@test.com","password":"123456"}
```

Use the returned JWT in:
`Authorization: Bearer <token>`

The frontend in this ZIP already handles the token automatically.
