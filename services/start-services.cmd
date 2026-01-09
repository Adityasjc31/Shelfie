@echo off
REM ==========================================
REM Digital Bookstore - Start All Services
REM ==========================================
REM This script starts all microservices in the correct order,
REM waiting for each service to be ready before starting the next.
REM
REM Order: eureka-service -> config-service -> api-gateway -> 
REM        user-service -> authentication-service -> inventory-service ->
REM        book-service -> order-service -> review-service
REM ==========================================

setlocal enabledelayedexpansion

REM Configuration
set "BASE_DIR=%~dp0"
set "MAX_WAIT_TIME=120"
set "CHECK_INTERVAL=5"

REM Service ports
set "EUREKA_PORT=8761"
set "CONFIG_PORT=8888"
set "GATEWAY_PORT=9080"
set "USER_PORT=9081"
set "AUTH_PORT=9082"
set "INVENTORY_PORT=9083"
set "BOOK_PORT=9084"
set "ORDER_PORT=9085"
set "REVIEW_PORT=9086"

echo.
echo ==========================================
echo  Digital Bookstore - Service Launcher
echo ==========================================
echo.

REM Function to wait for a service to be ready
REM %1 = service name, %2 = port

:start_eureka
echo [1/9] Starting Eureka Service on port %EUREKA_PORT%...
cd /d "%BASE_DIR%eureka-service"
start "Eureka Service" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "Eureka Service" %EUREKA_PORT%
if errorlevel 1 goto :error

:start_config
echo [2/9] Starting Config Service on port %CONFIG_PORT%...
cd /d "%BASE_DIR%config-service"
start "Config Service" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "Config Service" %CONFIG_PORT%
if errorlevel 1 goto :error

:start_gateway
echo [3/9] Starting API Gateway on port %GATEWAY_PORT%...
cd /d "%BASE_DIR%api-gateway"
start "API Gateway" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "API Gateway" %GATEWAY_PORT%
if errorlevel 1 goto :error

:start_user
echo [4/9] Starting User Service on port %USER_PORT%...
cd /d "%BASE_DIR%user-service"
start "User Service" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "User Service" %USER_PORT%
if errorlevel 1 goto :error

:start_auth
echo [5/9] Starting Authentication Service on port %AUTH_PORT%...
cd /d "%BASE_DIR%authentication-service"
start "Authentication Service" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "Authentication Service" %AUTH_PORT%
if errorlevel 1 goto :error

:start_inventory
echo [6/9] Starting Inventory Service on port %INVENTORY_PORT%...
cd /d "%BASE_DIR%inventory-service"
start "Inventory Service" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "Inventory Service" %INVENTORY_PORT%
if errorlevel 1 goto :error

:start_book
echo [7/9] Starting Book Service on port %BOOK_PORT%...
cd /d "%BASE_DIR%book-service"
start "Book Service" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "Book Service" %BOOK_PORT%
if errorlevel 1 goto :error

:start_order
echo [8/9] Starting Order Service on port %ORDER_PORT%...
cd /d "%BASE_DIR%order-service"
start "Order Service" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "Order Service" %ORDER_PORT%
if errorlevel 1 goto :error

:start_review
echo [9/9] Starting Review Service on port %REVIEW_PORT%...
cd /d "%BASE_DIR%review-service"
start "Review Service" cmd /c "mvnw.cmd spring-boot:run"
call :wait_for_service "Review Service" %REVIEW_PORT%
if errorlevel 1 goto :error

echo.
echo ==========================================
echo  All services started successfully!
echo ==========================================
echo.
echo Service URLs:
echo   Eureka Dashboard:    http://localhost:%EUREKA_PORT%
echo   API Gateway:         http://localhost:%GATEWAY_PORT%
echo   Config Service:      http://localhost:%CONFIG_PORT%
echo   User Service:        http://localhost:%USER_PORT%
echo   Auth Service:        http://localhost:%AUTH_PORT%
echo   Inventory Service:   http://localhost:%INVENTORY_PORT%
echo   Book Service:        http://localhost:%BOOK_PORT%
echo   Order Service:       http://localhost:%ORDER_PORT%
echo   Review Service:      http://localhost:%REVIEW_PORT%
echo.
echo Press any key to exit (services will continue running)...
pause >nul
goto :eof

REM ==========================================
REM Function: Wait for service to be ready
REM Arguments: %1 = service name, %2 = port
REM ==========================================
:wait_for_service
set "SERVICE_NAME=%~1"
set "PORT=%~2"
set "ELAPSED=0"

echo     Waiting for %SERVICE_NAME% to be ready...

:wait_loop
REM Check if port is responding using PowerShell
powershell -Command "try { $null = (New-Object System.Net.Sockets.TcpClient).Connect('localhost', %PORT%); exit 0 } catch { exit 1 }" >nul 2>&1
if %errorlevel%==0 (
    echo     [OK] %SERVICE_NAME% is ready on port %PORT%
    echo.
    exit /b 0
)

REM Check timeout
if %ELAPSED% geq %MAX_WAIT_TIME% (
    echo     [ERROR] Timeout waiting for %SERVICE_NAME% on port %PORT%
    exit /b 1
)

REM Wait and increment counter
timeout /t %CHECK_INTERVAL% /nobreak >nul
set /a ELAPSED+=CHECK_INTERVAL
echo     Waiting... (%ELAPSED%s / %MAX_WAIT_TIME%s)
goto :wait_loop

:error
echo.
echo ==========================================
echo  ERROR: Failed to start all services
echo ==========================================
echo  Please check the service windows for errors.
echo.
pause
exit /b 1
