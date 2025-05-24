@echo off
REM Minefest-Core Emergency Process Stop Protocol
REM Updated to preserve Gradle daemon and prevent freezing

echo ================================================
echo Minefest-Core Emergency Process Stop Protocol
echo ================================================
echo.

echo Checking for hanging Minecraft/Forge processes...
echo.

REM Priority 1: Target only Minecraft/Forge processes (preserves Gradle daemon)
echo [Priority 1] Targeting LaunchTesting processes only...
for /f "tokens=1" %%i in ('jps -m ^| findstr /C:"LaunchTesting"') do (
    echo Killing Minecraft process PID: %%i
    taskkill /F /PID %%i
)

echo.
echo Checking if any LaunchTesting processes remain...
jps -m | findstr /C:"LaunchTesting"
if %ERRORLEVEL% == 0 (
    echo.
    echo Some processes still running. Trying PowerShell approach...
    echo.
    
    REM Priority 2: PowerShell alternative
    echo [Priority 2] Using PowerShell for better process filtering...
    powershell "Get-Process java | Where-Object {$_.CommandLine -like '*LaunchTesting*'} | ForEach-Object { Write-Host 'Killing process:' $_.Id; Stop-Process $_ -Force }"
) else (
    echo All LaunchTesting processes stopped successfully!
)

echo.
echo ================================================
echo Emergency stop complete!
echo.
echo If you still have issues:
echo - Use: ./gradlew --stop   (to restart Gradle daemon)
echo - Last resort: taskkill /F /IM java.exe (kills ALL Java)
echo ================================================
echo.
pause 