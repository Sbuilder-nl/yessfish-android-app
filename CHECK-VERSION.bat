@echo off
echo ========================================
echo YessFish Android - Version Checker
echo ========================================
echo.

echo Checking if Kotlin plugin is present...
echo.

findstr /C:"org.jetbrains.kotlin.android" build.gradle >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] You have the CORRECT version!
    echo.
    echo Found Kotlin plugin:
    findstr "kotlin" build.gradle
    echo.
    echo You can now build the APK:
    echo   gradlew.bat clean
    echo   gradlew.bat assembleDebug
) else (
    echo [ERROR] You have the WRONG version!
    echo.
    echo The Kotlin plugin is MISSING from build.gradle
    echo This means you downloaded an OLD version.
    echo.
    echo SOLUTION:
    echo 1. Delete this folder completely
    echo 2. Download the 'anderoid' folder again from the server
    echo 3. Run this check script again
    echo.
    echo Server path:
    echo   sftp://root@185.165.242.58:2223/root/afbeeldingen/yessfish/app/anderoid/
)

echo.
echo ========================================
pause
