@echo off
:: Minefest-Core Emergency Process Stop Script
:: Follows Priority 1-4 escalation to preserve Gradle daemon

echo ========================================================
echo   Minefest-Core Emergency Process Stop Protocol
echo ========================================================
echo.
echo Following proper escalation to preserve Gradle daemon...
echo.

:: Priority 1: Target only Minecraft/Forge processes (LaunchTesting)
echo [PRIORITY 1] Targeting only Minecraft/Forge processes...
for /f "tokens=1" %%i in ('jps -m ^| findstr /C:"LaunchTesting"') do (
    echo Stopping Minecraft process PID: %%i
    taskkill /F /PID %%i >nul 2>&1
)

:: Check if any LaunchTesting processes remain
jps -m | findstr /C:"LaunchTesting" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [SUCCESS] ✅ All Minecraft processes stopped - Gradle daemon preserved
    goto :end
)

:: Priority 2: PowerShell alternative (if available)
echo [PRIORITY 2] Using PowerShell targeted approach...
powershell -Command "Get-Process java | Where-Object {$_.ProcessName -eq 'java' -and $_.CommandLine -like '*LaunchTesting*'} | Stop-Process -Force" >nul 2>&1

:: Check again
jps -m | findstr /C:"LaunchTesting" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [SUCCESS] ✅ PowerShell approach succeeded - Gradle daemon preserved
    goto :end
)

:: Priority 3: Gradle daemon restart (if processes persist)
echo [PRIORITY 3] Gradle daemon clean restart...
./gradlew --stop
timeout /t 2 >nul

:: Priority 4: Check if manual intervention needed
jps -m | findstr /C:"LaunchTesting" >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [WARNING] ⚠️ Persistent processes detected
    echo.
    echo Remaining processes require manual intervention:
    jps -m | findstr /C:"LaunchTesting"
    echo.
    echo Manual options:
    echo   1. Wait 30 seconds and try again
    echo   2. Identify specific hanging process and kill by PID
    echo   3. Request user approval for aggressive taskkill
    echo.
    set /p MANUAL_CHOICE="Choose option (1-3): "
    if "%MANUAL_CHOICE%"=="1" (
        echo Waiting 30 seconds...
        timeout /t 30 >nul
        goto :retry
    )
    if "%MANUAL_CHOICE%"=="3" (
        echo [WARNING] This will kill ALL Java processes including Gradle daemon
        set /p CONFIRM="Type 'CONFIRM' to proceed with aggressive kill: "
        if "%CONFIRM%"=="CONFIRM" (
            echo [EMERGENCY] Using aggressive taskkill - will require Gradle restart
            taskkill /F /IM java.exe
            echo [COMPLETED] All Java processes terminated - restart Gradle with ./gradlew build
        ) else (
            echo [CANCELLED] Aggressive kill cancelled - use manual PID targeting
        )
    )
    goto :end
)

:retry
:: Retry Priority 1
for /f "tokens=1" %%i in ('jps -m ^| findstr /C:"LaunchTesting"') do (
    echo Retry stopping PID: %%i
    taskkill /F /PID %%i >nul 2>&1
)

:end
echo.
echo [COMPLETED] Emergency stop protocol finished
echo.
echo ✅ Development environment preserved - Gradle daemon should remain active
echo 🚀 Ready to continue development with: ./gradlew runServer
echo.
pause 