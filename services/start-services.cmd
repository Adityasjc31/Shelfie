@echo off
echo ========================================
echo Starting Shelfie Microservices
echo ========================================
echo.

:: Set the base directory
set BASE_DIR=%~dp0

:: Start Eureka Service
echo [1/9] Starting Eureka Service...
cd /d "%BASE_DIR%eureka-service"
start "Eureka Service" cmd /k "mvnw spring-boot:run"
echo Waiting for Eureka to initialize (30 seconds)...
timeout /t 30 /nobreak >nul

:: Start Config Service
echo [2/9] Starting Config Service...
cd /d "%BASE_DIR%config-service"
start "Config Service" cmd /k "mvnw spring-boot:run"
echo Waiting for Config Service to initialize (20 seconds)...
timeout /t 20 /nobreak >nul

:: Start API Gateway
echo [3/9] Starting API Gateway...
cd /d "%BASE_DIR%api-gateway"
start "API Gateway" cmd /k "mvnw spring-boot:run"
echo Waiting for API Gateway to initialize (15 seconds)...
timeout /t 15 /nobreak >nul

:: Start Authentication Service
echo [4/9] Starting Authentication Service...
cd /d "%BASE_DIR%authentication-service"
start "Authentication Service" cmd /k "mvnw spring-boot:run"
echo Waiting for Authentication Service to initialize (15 seconds)...
timeout /t 15 /nobreak >nul

:: Start remaining services
echo [5/9] Starting User Service...
cd /d "%BASE_DIR%user-service"
start "User Service" cmd /k "mvnw spring-boot:run"

echo [6/9] Starting Book Service...
cd /d "%BASE_DIR%book-service"
start "Book Service" cmd /k "mvnw spring-boot:run"

echo [7/9] Starting Inventory Service...
cd /d "%BASE_DIR%inventory-service"
start "Inventory Service" cmd /k "mvnw spring-boot:run"

echo [8/9] Starting Order Service...
cd /d "%BASE_DIR%order-service"
start "Order Service" cmd /k "mvnw spring-boot:run"

echo [9/9] Starting Review Service...
cd /d "%BASE_DIR%review-service"
start "Review Service" cmd /k "mvnw spring-boot:run"

echo.
echo ========================================
echo All services have been started!
echo ========================================
echo.
echo Services started in order:
echo   1. Eureka Service (Service Discovery)
echo   2. Config Service (Configuration Server)
echo   3. API Gateway
echo   4. Authentication Service
echo   5. User Service
echo   6. Book Service
echo   7. Inventory Service
echo   8. Order Service
echo   9. Review Service
echo.
echo Each service is running in its own window.
echo Close this window or press any key to exit.
pause >nul
