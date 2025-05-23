@echo off
:: Minefest-Core Deployment Script (Windows)
:: Deploys the project to development client, local server, and remote production server

setlocal enabledelayedexpansion

echo ========================================================
echo   Minefest-Core Enhanced Deployment Script (Windows)
echo ========================================================
echo.

:: Configuration - Edit these for your server
if "%DEPLOY_USER%"=="" set DEPLOY_USER=minecraft
if "%DEPLOY_HOST%"=="" set DEPLOY_HOST=your-server.com
if "%DEPLOY_PATH%"=="" set DEPLOY_PATH=/home/minecraft/server
set LOCAL_BUILD_PATH=server

:: Client configuration - CurseForge path
set CURSEFORGE_CLIENT_PATH=c:\Users\rstic\curseforge\minecraft\Instances\Minefest (1)\mods

echo Configuration:
echo   Local Development: run/mods/
echo   Local Server: server/mods/
echo   CurseForge Client: %CURSEFORGE_CLIENT_PATH%
echo   Remote Server: %DEPLOY_USER%@%DEPLOY_HOST%:%DEPLOY_PATH%
echo.

:: Build the project locally
echo [INFO] Building project for all environments...
if exist gradlew.bat (
    call gradlew.bat buildAll
) else (
    echo ERROR: gradlew.bat not found
    pause
    exit /b 1
)

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo [SUCCESS] Build completed - mod deployed to development environments
echo.

:: Deploy to CurseForge client
echo [INFO] Deploying to CurseForge client...
if exist "%CURSEFORGE_CLIENT_PATH%" (
    :: Remove old versions
    del "%CURSEFORGE_CLIENT_PATH%\minefest-essentials-*.jar" 2>nul
    
    :: Copy new version
    for %%f in (build\libs\minefest-essentials-*.jar) do (
        echo Copying %%f to CurseForge client...
        copy "%%f" "%CURSEFORGE_CLIENT_PATH%\" >nul
        if !ERRORLEVEL! EQU 0 (
            echo [SUCCESS] ✅ CurseForge client updated with %%~nxf
        ) else (
            echo [ERROR] ❌ Failed to copy to CurseForge client
        )
    )
) else (
    echo [WARNING] ⚠️ CurseForge client path not found: %CURSEFORGE_CLIENT_PATH%
    echo Skipping client deployment
)
echo.

:: Ask if user wants to deploy to remote server
set /p DEPLOY_REMOTE="Deploy to remote server %DEPLOY_HOST%? (y/N): "
if /i not "%DEPLOY_REMOTE%"=="y" (
    echo [INFO] Skipping remote server deployment
    goto :local_complete
)

:: Check if configuration is set for remote deployment
if "%DEPLOY_HOST%"=="your-server.com" (
    echo ERROR: DEPLOY_HOST not configured for remote deployment
    echo Please set DEPLOY_HOST environment variable:
    echo   set DEPLOY_HOST=your-server.com
    echo   %0
    echo.
    echo Or edit this script directly.
    pause
    exit /b 1
)

:: Check if we have ssh/scp available (Git Bash, WSL, or OpenSSH)
where ssh >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: SSH not found. Please install one of:
    echo   - Git for Windows (includes Git Bash with SSH)
    echo   - Windows Subsystem for Linux (WSL)
    echo   - OpenSSH for Windows
    echo.
    echo Or use the deploy.sh script from Git Bash/WSL
    pause
    exit /b 1
)

:: Test SSH connection
echo [INFO] Testing SSH connection to %DEPLOY_HOST%...
ssh -o ConnectTimeout=10 -o BatchMode=yes %DEPLOY_USER%@%DEPLOY_HOST% exit >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Cannot connect to %DEPLOY_HOST%
    echo Make sure:
    echo   1. SSH key is set up for %DEPLOY_USER%@%DEPLOY_HOST%
    echo   2. Server is reachable
    echo   3. User has permission to access %DEPLOY_PATH%
    pause
    exit /b 1
)

echo [SUCCESS] SSH connection successful
echo.

:: Create backup on server
echo [INFO] Creating backup on server...
ssh %DEPLOY_USER%@%DEPLOY_HOST% "if [ -d '%DEPLOY_PATH%' ]; then backup_name='minefest-backup-$(date +%%Y%%m%%d-%%H%%M%%S)'; cp -r '%DEPLOY_PATH%' '$backup_name'; echo 'Backup created: $backup_name'; else echo 'No existing installation found, skipping backup'; fi"

echo [SUCCESS] Backup completed
echo.

:: Deploy files to server
echo [INFO] Deploying files to server...

:: Create directory structure
ssh %DEPLOY_USER%@%DEPLOY_HOST% "mkdir -p '%DEPLOY_PATH%'"

:: Check if we have rsync (prefer rsync, fallback to scp)
where rsync >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Using rsync for file transfer...
    rsync -avz --progress --exclude=world/ --exclude=logs/ --exclude=crash-reports/ --exclude=*.log %LOCAL_BUILD_PATH%/ %DEPLOY_USER%@%DEPLOY_HOST%:%DEPLOY_PATH%/
) else (
    echo Using scp for file transfer...
    scp -r %LOCAL_BUILD_PATH%/* %DEPLOY_USER%@%DEPLOY_HOST%:%DEPLOY_PATH%/
)

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: File transfer failed
    pause
    exit /b 1
)

:: Make scripts executable
ssh %DEPLOY_USER%@%DEPLOY_HOST% "chmod +x '%DEPLOY_PATH%/run.sh'; chmod +x '%DEPLOY_PATH%/'*.sh 2>/dev/null || true"

echo [SUCCESS] Files deployed successfully
echo.

:: Update server configuration
echo [INFO] Updating server configuration...
ssh %DEPLOY_USER%@%DEPLOY_HOST% "cd '%DEPLOY_PATH%' && echo 'eula=true' > eula.txt && if [ ! -f server.properties ]; then cat > server.properties << 'EOF'
# Minefest-Core Server Configuration
server-port=25565
online-mode=false
motd=Minefest-Core Production Server
max-players=100
difficulty=easy
gamemode=survival
allow-flight=true
enable-command-block=true
spawn-protection=0
view-distance=12
simulation-distance=8
max-world-size=29999984
EOF
fi && echo 'Server configuration updated'"

echo [SUCCESS] Configuration updated
echo.

:: Verify deployment
echo [INFO] Verifying deployment...
ssh %DEPLOY_USER%@%DEPLOY_HOST% "cd '%DEPLOY_PATH%' && if [ ! -f 'mods/minefest-essentials-'*.jar ]; then echo 'ERROR: Minefest mod not found'; exit 1; fi && if [ ! -f 'run.sh' ]; then echo 'ERROR: run.sh not found'; exit 1; fi && if [ ! -f 'user_jvm_args.txt' ]; then echo 'ERROR: user_jvm_args.txt not found'; exit 1; fi && echo 'All required files present' && if command -v java >&/dev/null; then java_version=$(java -version 2>&1 | head -n1 | cut -d'\"' -f2); echo \"Java version: $java_version\"; else echo 'WARNING: Java not found on server'; fi"

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Deployment verification failed
    pause
    exit /b 1
)

echo [SUCCESS] Remote deployment verification completed
echo.
goto :complete

:local_complete
echo [INFO] Local deployment completed successfully!
echo.

:complete
:: Success message
echo [SUCCESS] 🎉 Deployment completed successfully!
echo.
echo Environments updated:
echo   ✅ Development: run/mods/
echo   ✅ Local Server: server/mods/  
echo   ✅ CurseForge Client: %CURSEFORGE_CLIENT_PATH%
if /i "%DEPLOY_REMOTE%"=="y" (
    echo   ✅ Remote Server: %DEPLOY_USER%@%DEPLOY_HOST%:%DEPLOY_PATH%
)
echo.
echo Ready to test:
echo   🎮 CurseForge Client: Launch from CurseForge app
echo   🏠 Local Development: ./gradlew runServer
echo   🚀 Local Production: cd server && start-server.bat
if /i "%DEPLOY_REMOTE%"=="y" (
    echo   🌐 Remote Production: ssh %DEPLOY_USER%@%DEPLOY_HOST% "cd %DEPLOY_PATH% && ./run.sh"
)
echo.

pause 