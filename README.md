# 📚 BookStore API

A RESTful backend for an online bookstore, built with **Spring Boot**, **Spring Security (JWT)** and **MySQL**. It covers the full shopping flow: browsing and searching books, favorites, a shopping cart, order placement with stock control, order cancellation with refunds, and (simulated) payments, with separate **USER** and **ADMIN** roles. The whole stack runs with one command using **Docker Compose**.

## Features

- **Authentication & authorization**: register / login, stateless JWT, BCrypt password hashing, role-based access (`USER`, `ADMIN`) enforced with `@PreAuthorize`.
- **Catalog**: public book listing and details; admin-only create / update / delete; discount support with an automatically computed `finalPrice`.
- **Dynamic search**: filter by keyword (title/description) and price range using JPA Specifications.
- **Cart**: add, update, remove items and clear the cart; a cart is created for every user at registration.
- **Favorites**: add / remove / list favorite books and check whether a book is a favorite.
- **Orders**:
  - Checkout turns the cart into an order and validates stock.
  - Stock rows are locked with **pessimistic locking**, which prevents overselling under concurrent requests.
  - The price at purchase is stored on each order item, so later price changes never alter past orders.
  - Users can cancel `PENDING` or `PAID` orders. Stock is restored and payments are refunded in a single transaction.
  - Admins manage order status.
- **Payments**: simulated payment flow with retry support after a failed attempt.
- **Error handling**: a centralized `@RestControllerAdvice` returns consistent JSON errors (400, 401, 403, 404, 409, 500) without leaking internals.
- **Configuration**: all secrets come from environment variables, with nothing hardcoded.
- **Tests**: unit tests (JUnit 5, Mockito) for order and JWT logic.
- **Admin seeding**: an admin account is created on first startup.

## Tech Stack

| Layer | Technology |
|---|---|
| Language / Framework | Java 25, Spring Boot 4.1 |
| Security | Spring Security, JWT (jjwt 0.12.5), BCrypt |
| Persistence | Spring Data JPA (Hibernate), MySQL 8 |
| Validation | Jakarta Bean Validation |
| Build | Maven (wrapper included) |
| Testing | JUnit 5, Mockito |
| Deployment | Docker, Docker Compose |

## Project Structure

```
src/main/java/com/AL_Tayep/BookStore
├── Security/       JWT filter & provider, security config, admin seeding
├── controllers/    REST controllers (auth, items, users, carts, favorites, orders, payments)
├── Services/       Business logic, search specification builder
├── Repositories/   Spring Data JPA repositories
├── models/         JPA entities, composite keys, enums
├── Dto/            Request / response records, grouped by feature
└── Exceptions/     Custom exceptions + global exception handler
```

## Data Model

```
User  1—1 Cart   1—* CartItem  *—1 Item
User  1—* Order  1—* OrderItem *—1 Item
Order 1—* Payment
User  1—* FavItem *—1 Item
```

`CartItem`, `OrderItem` and `FavItem` use composite primary keys.

## Getting Started

### Option 1: Docker (recommended)

Requires only [Docker](https://www.docker.com/products/docker-desktop/).

```bash
git clone https://github.com/<your-username>/BookStore.git
cd BookStore

cp .env.example .env        # Windows PowerShell: copy .env.example .env
# edit .env and set your own values

docker compose up --build
```

The API is available at `http://localhost:8080`. Data is kept in a Docker volume. Stop with `docker compose down` (add `-v` to also delete the database).

### Option 2: Run locally

Requires JDK 25 and MySQL 8 running on `localhost:3306`.

```bash
export DB_PASSWORD=your-mysql-password
export ADMIN_PASSWORD=your-admin-password
export JWT_SECRET=a-random-string-of-at-least-32-characters

./mvnw spring-boot:run
```

On Windows PowerShell use `$env:DB_PASSWORD="..."` instead of `export`.

> The default profile uses `ddl-auto=create-drop`, so tables are recreated on every local start. Set `spring.jpa.hibernate.ddl-auto=update` to keep data.

### Configuration

| Variable | Required | Description |
|---|---|---|
| `DB_PASSWORD` | yes | MySQL root password |
| `ADMIN_PASSWORD` | yes | Password of the seeded admin account |
| `JWT_SECRET` | yes | Signing key for tokens (at least 32 characters) |
| `DB_URL` | no | JDBC URL (defaults to local MySQL; set automatically in Docker) |
| `DB_USERNAME` | no | Database user (default `root`) |
| `ADMIN_EMAIL` | no | Admin email (default `admin@bookstore.com`) |

### Default admin

On first startup an admin is created with the email from `ADMIN_EMAIL` and the password from `ADMIN_PASSWORD`.

## Running the Tests

```bash
./mvnw test
```

The unit tests need no database. They cover checkout, stock control, order cancellation and JWT handling.

## API Overview

Send the token as `Authorization: Bearer <token>`.

### Auth (public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a user, returns a JWT |
| POST | `/api/auth/login` | Log in, returns a JWT |

### Items

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/items` | Public | List all items |
| GET | `/api/items/{id}` | Public | Item details |
| POST | `/api/items/search` | Public | Search by keyword / price range |
| POST | `/api/items` | Admin | Create item |
| PUT | `/api/items/{id}` | Admin | Update item |
| DELETE | `/api/items/{id}` | Admin | Delete item |

### Cart

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/carts/users` | User | View my cart |
| POST | `/api/carts/users` | User | Add item to cart |
| PUT | `/api/carts/users/items/{itemId}` | User | Update item quantity |
| DELETE | `/api/carts/users/items/{itemId}` | User | Remove item |
| DELETE | `/api/carts/users` | User | Clear cart |
| GET | `/api/carts` | Admin | List all carts |

### Favorites

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/favorites/users/` | User | List my favorites |
| POST | `/api/favorites/users/items/{itemId}` | User | Add favorite |
| DELETE | `/api/favorites/users/items/{itemId}` | User | Remove favorite |
| GET | `/api/favorites/users/items/{itemId}` | User | Is this item a favorite? |

### Orders & Payments

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/orders/users` | User | Create order from cart |
| GET | `/api/orders/users/{orderId}` | User | Get my order |
| PATCH | `/api/orders/users/{orderId}/cancel` | User | Cancel a pending or paid order |
| POST | `/api/orders/users/{orderId}/payment` | User | Pay for an order |
| GET | `/api/orders/users/{orderId}/payment` | User | My payments for an order |
| GET | `/api/orders` | Admin | List all orders |
| GET | `/api/orders/by-user/{userId}` | Admin | Orders of one user |
| GET | `/api/orders/{orderId}` | Admin | Get any order |
| PATCH | `/api/orders/{orderId}/status` | Admin | Update order status |
| GET | `/api/payments` | Admin | List all payments |
| GET | `/api/payments/{paymentId}` | Admin | Payment details |

### Users

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/users` | Admin | List users |
| GET | `/api/users/{id}` | Admin | Get user |
| DELETE | `/api/users/{id}` | Admin | Delete user |
| PUT | `/api/users` | User | Update my profile |
| PATCH | `/api/users/password` | User | Change my password |

## Example Requests

```bash
# Register
curl -X POST localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Ali","email":"ali@example.com","password":"Passw0rd!"}'

# Search books (public)
curl -X POST localhost:8080/api/items/search \
  -H "Content-Type: application/json" \
  -d '{"keyWord":"java","minPrice":10,"MaxPrice":50}'

# Add to cart, then check out
curl -X POST localhost:8080/api/carts/users \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"itemId":1,"amount":2}'

curl -X POST localhost:8080/api/orders/users \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"deliveryAddress":"12 Nile St, Cairo","phoneNumber1":"01000000000"}'
```

## Order Lifecycle

`PENDING` → `PAID` → `SHIPPED` → `DELIVERED`

A `PENDING` or `PAID` order can be `CANCELLED`. Stock is restored and any completed payment is marked `REFUNDED`.

## Error Format

```json
{
  "timestamp": "2026-10-31T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Item not found: 99",
  "path": "/api/items/99"
}
```

## Notes & Limitations

- Payments are **simulated**: no real payment gateway is integrated.
- This is a backend only; the HTML files under `static/` are minimal placeholders.

## Roadmap

- [x] Secrets moved to environment variables
- [x] Docker Compose setup (app + MySQL)
- [x] Unit tests for order and JWT logic
- [ ] OpenAPI / Swagger documentation
- [ ] Integration tests with Testcontainers
- [ ] Pagination for list endpoints
- [ ] Refresh tokens

## Author

**AL Tayep**: [GitHub](https://github.com/MOHAMEDAL-TAYEP)
