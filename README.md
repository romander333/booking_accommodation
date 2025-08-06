# 🏡 Booking System — Accommodation Reservation App
## 🔹 Introduction
This project was both an interesting and valuable experience for me.
The idea came after my recent vacation, where I had to book accommodations through a third-party platform. I thought:

“Why not create something like this myself?”

That's how this system was born — with user registration, accommodation listing, booking, online payments, and status management.

## 🔹 Technologies Used
Java 17

Spring Boot (Core framework)

Spring Security + JWT (authentication/authorization)

Spring Web (REST API)

Spring Data JPA + Hibernate

Liquibase (for DB migrations)

Docker (containerization)

Stripe API (online payments integration)

Telegram Bot API (user notifications)

Ngrok (public webhook tunneling)

MapStruct (DTO ↔ Entity mapping)

JUnit & Integration Tests (testing)

## 🔹 Main Functionalities
ℹ️ All endpoints use the base path: /api

🔐 AuthController
POST /register — Register new user

POST /login — User login

👤 UserController
PUT /update-role — Update user role

PUT /update-profile — Edit profile

🏠 AccommodationController
POST /accommodation — Create accommodation

GET /accommodations — View all listings

GET /accommodation/{id} — View accommodation by ID

PUT /accommodation/{id} — Update listing

DELETE /accommodation/{id} — Delete listing

📅 BookingController
POST /booking — Create a booking

GET /bookings — View bookings by status

GET /bookings/me — View current user's bookings

GET /booking/{id} — View specific booking

PUT /booking/{id} — Update booking

PUT /booking/status/{id} — Change booking status

DELETE /booking/{id} — Cancel booking

💳 PaymentController
POST /payment — Create payment via Stripe

Stripe webhook — Automatically updates status after successful payment

## 🔹 How to Run the Project
⚠️ Required tools:

Docker

Java 17

MySQL (or container)

Ngrok account (for Stripe webhook)

🔧 Steps
Clone the repository:

git clone https://github.com/romander333/booking_accommodation.git
cd booking_accommodation
Configure .env or application.yml (Stripe keys, DB config, etc.)

Run Docker components:

docker-compose up
(Optional) Start Ngrok tunnel:

ngrok http 8080
Test the API via the provided URL (Postman, etc.)

![Architecture Diagram](images/first-architecture-diagram.png)
![Architecture Diagram](images/second-architecture-diagram.png)

## 🔹 Key Features & Challenges
Integrated Stripe API with webhook support (first-time experience)

Built custom JWT-based authentication

Used MapStruct for DTO ↔ Entity mapping

Role management (admin/user) and secure profile updates

Telegram bot sends notifications about new bookings to admin (asynchronously using Executor)

## 🔹 Swagger
Interactive API documentation available at:
http://localhost:8080/api/swagger-ui/index.html

## 🔹 Author
Roman Luchko
Telegram: @servetochka
GitHub: romander333

This is my full-cycle backend development project built from scratch.
The goal wasn’t just another CRUD app — but something real, with integrations, security, payments, and real-world application potential.