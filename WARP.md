# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is a full-stack photo sharing web application built with:
- **Backend**: Spring Boot 3.5.4 (Java 17) with MySQL database
- **Frontend**: Vue 3 + Vite with Element Plus UI framework
- **Authentication**: JWT-based auth with Spring Security
- **File Storage**: Local file uploads with configurable path

## Development Commands

### Backend (Spring Boot)
```bash
# Navigate to backend directory first
cd backend

# Build and run the application
./mvnw spring-boot:run

# Build without running
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw clean package

# Clean build artifacts
./mvnw clean
```

### Frontend (Vue.js)
```bash
# Navigate to frontend directory first
cd frontend

# Install dependencies
npm install

# Start development server (hot reload)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### Full Application Setup
```bash
# Start backend (from project root)
cd backend && ./mvnw spring-boot:run &

# Start frontend (from project root) 
cd frontend && npm run dev
```

## Architecture Overview

### Backend Architecture
- **Spring Boot application** with layered architecture:
  - **Controllers**: REST API endpoints (`PhotoController`, auth endpoints)
  - **Services**: Business logic (`PhotosService`, `AccountService`)  
  - **Mappers**: MyBatis Plus data access (`PhotosMapper`, `AccountMapper`)
  - **Entities/DTOs**: Data models (`Photos`, `Account`)
  - **Security**: JWT authentication with Spring Security
  - **Utils**: JWT utilities, file handling, constants

### Frontend Architecture  
- **Vue 3 SPA** with component-based structure:
  - **Pages**: Main application screens (`HomePage`, `LoginPage`, `PhotoDetail`, etc.)
  - **Components**: Reusable UI components (photo gallery, upload, user profile)
  - **Router**: Vue Router with authentication guards
  - **API Layer**: Axios-based HTTP client with interceptors
  - **State**: Local storage for JWT token management

### Key Integrations
- **Database**: MySQL with MyBatis Plus ORM
- **Authentication**: JWT tokens stored in localStorage, validated on backend
- **File Upload**: Multipart form data to `/api/photos/upload` endpoint  
- **CORS**: Configured for localhost:3000 (frontend) to localhost:8080 (backend)

## Database Configuration

The application expects a MySQL database:
- **Database name**: `db_account`  
- **Connection**: `localhost:3306`
- **Tables**: `photos`, `accounts` (managed by MyBatis Plus)

Update `backend/src/main/resources/application.properties` for your database credentials.

## File Storage

Photos are stored locally with configuration in `application.properties`:
- **Upload path**: `/Users/yaoyitong/Desktop/java/mysite/uploads/`
- **Access path**: `/api/photos/` 
- **Max file size**: 30MB

The `uploads/` directory in project root contains uploaded images.

## Authentication Flow

1. **Login**: POST to `/api/auth/login` with form data
2. **JWT Token**: Returned in `Authorization` header as `Bearer <token>`
3. **Frontend Storage**: Token saved to `localStorage`  
4. **API Requests**: Token included in `Authorization` header
5. **Route Guards**: Vue router checks token for protected routes

## Development Notes

- **Backend Port**: 8080 (Spring Boot default)
- **Frontend Port**: 5173 (Vite default)  
- **Hot Reload**: Both backend and frontend support hot reload during development
- **CORS**: Configured to allow all origins in development
- **JWT Secret**: Currently hardcoded as "secret" in application.properties
- **Photo URLs**: Backend constructs full URLs for photo access

## Key Files to Modify

- **Backend API**: `backend/src/main/java/com/example/mysite/Controller/`
- **Frontend Pages**: `frontend/src/components/pages/`
- **Database Config**: `backend/src/main/resources/application.properties`
- **Frontend API**: `frontend/src/api/`
- **Routing**: `frontend/src/router/index.js`
- **Security Config**: `backend/src/main/java/com/example/mysite/config/SecurityConfig.java`
