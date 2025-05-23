@echo off
:: Minefest-Core Build and Test Script
:: Quick build and deploy for testing across all environments

echo ========================================================
echo   Minefest-Core Build and Test Script
echo ========================================================
echo.

:: Build and deploy to all environments
echo [INFO] Building and deploying to all environments...
call gradlew.bat buildAll

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] ❌ Build failed
    pause
    exit /b 1
)

echo.
echo [SUCCESS] ✅ Build and deployment completed!
echo.
echo Environments updated:
echo   📁 Development: run/mods/
echo   🚀 Local Server: server/mods/
echo   🎮 CurseForge Client: CurseForge/Minefest (1)/mods/
echo.
echo Quick Start Options:
echo   [1] Test CurseForge client (recommended)
echo   [2] Start local development server  
echo   [3] Start local production server
echo   [4] Full deployment (including remote server)
echo   [5] Exit
echo.

set /p CHOICE="Choose an option (1-5): "

if "%CHOICE%"=="1" (
    echo [INFO] 🎮 Ready to test CurseForge client!
    echo.
    echo Next steps:
    echo   1. Launch CurseForge app
    echo   2. Start the "Minefest (1)" instance
    echo   3. Connect to server IP: localhost ^(if local^) or your server IP
    echo   4. Look for test broadcasts every 15 seconds!
    echo.
    echo Press any key to continue...
    pause >nul
) else if "%CHOICE%"=="2" (
    echo [INFO] 🏠 Starting local development server...
    call gradlew.bat runServer
) else if "%CHOICE%"=="3" (
    echo [INFO] 🚀 Starting local production server...
    cd server
    call start-server.bat
) else if "%CHOICE%"=="4" (
    echo [INFO] 🌐 Running full deployment...
    call deploy.bat
) else if "%CHOICE%"=="5" (
    echo [INFO] 👋 Goodbye!
    exit /b 0
) else (
    echo [ERROR] Invalid choice. Exiting.
    pause
    exit /b 1
) 