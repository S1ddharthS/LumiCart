@echo off
echo ====================================
echo  Compiling AetherShop Java Engine...
echo ====================================

if not exist out mkdir out

javac -d out src\AetherShopServer.java src\algorithms\*.java src\handlers\*.java

if %ERRORLEVEL% == 0 (
    echo.
    echo  Compilation successful!
    echo  Output directory: out\
) else (
    echo.
    echo  Compilation FAILED. Check errors above.
    exit /b 1
)
