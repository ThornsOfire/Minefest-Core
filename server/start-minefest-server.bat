@echo off
echo ================================================================
echo  Minefest Standalone Server Launcher
echo ================================================================
echo.
echo Starting Minecraft Forge server with Minefest mod...
echo This is running as a proper Minecraft server (not dev environment)
echo.

:: Accept EULA automatically
if not exist "eula.txt" (
    echo eula=true > eula.txt
    echo EULA automatically accepted
)

:: Start the server using the Forge launcher
echo Starting server...
call run.bat nogui

echo.
echo Server stopped. Press any key to exit...
pause 