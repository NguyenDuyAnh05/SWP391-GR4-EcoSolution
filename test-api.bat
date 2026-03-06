@echo off
REM API Smoke Test Script for EcoSolution Auth APIs
REM Run this after starting the Spring Boot app on port 8081

echo ========================================
echo EcoSolution Auth API Smoke Test
echo ========================================
echo.

set BASE_URL=http://127.0.0.1:8081
set EMAIL=test_citizen_%RANDOM%@test.local
set PASSWORD=password123
set NAME=Test Citizen

echo Test User: %EMAIL%
echo.

REM Test 1: Register
echo [TEST 1] POST /api/v1/auth/register
curl -i -X POST "%BASE_URL%/api/v1/auth/register" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"%EMAIL%\",\"password\":\"%PASSWORD%\",\"name\":\"%NAME%\"}"
echo.
echo.

REM Test 2: Login
echo [TEST 2] POST /api/v1/auth/login
curl -s -X POST "%BASE_URL%/api/v1/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"%EMAIL%\",\"password\":\"%PASSWORD%\"}" > login_response.json
type login_response.json
echo.
echo.

REM Extract token (manual for now)
echo Please copy the accessToken value from above and set it as TOKEN variable
echo Then run the remaining tests...
pause

REM Test 3: Protected endpoint without token
echo [TEST 3] GET /api/v1/users/collectors (No Token)
curl -i -X GET "%BASE_URL%/api/v1/users/collectors"
echo.
echo.

REM Test 4: Protected endpoint with bad token
echo [TEST 4] GET /api/v1/users/collectors (Bad Token)
curl -i -X GET "%BASE_URL%/api/v1/users/collectors" ^
  -H "Authorization: Bearer invalid.token.value"
echo.
echo.

REM Test 5: Protected endpoint with citizen token (requires manual TOKEN)
if not "%TOKEN%"=="" (
  echo [TEST 5] GET /api/v1/users/collectors (Citizen Token)
  curl -i -X GET "%BASE_URL%/api/v1/users/collectors" ^
    -H "Authorization: Bearer %TOKEN%"
  echo.
  echo.
)

echo ========================================
echo Tests Complete
echo ========================================
pause

