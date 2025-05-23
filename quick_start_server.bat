@echo off
:: Quick Start Server - Simple launcher for development
echo ================================================================
echo  Minefest-Core Development Server
echo ================================================================
echo.

:: Accept EULA automatically for development
if not exist "run\eula.txt" (
    echo Creating EULA acceptance for development...
    mkdir run 2>nul
    echo eula=true > run\eula.txt
    echo EULA automatically accepted
)

echo Building mod and starting server...
echo.

:: Build and deploy to both environments, then start server
call gradlew.bat buildAll runServer

echo.
echo ================================================================
echo  Server stopped. Check console output above for any issues.
echo ================================================================
echo.
pause 