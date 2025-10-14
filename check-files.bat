@echo off
echo ========================================
echo YessFish Android - File Verification
echo ========================================
echo.

set ERRORS=0

echo Checking required files...
echo.

REM Check gradle wrapper properties
if exist "gradle\wrapper\gradle-wrapper.properties" (
    echo [OK] gradle-wrapper.properties
    findstr "gradle-8.0" gradle\wrapper\gradle-wrapper.properties >nul
    if %ERRORLEVEL% EQU 0 (
        echo     ^|-- Version: Gradle 8.0 (Correct!)
    ) else (
        echo     ^|-- [WARNING] Not Gradle 8.0!
        set ERRORS=1
    )
) else (
    echo [MISSING] gradle-wrapper.properties
    set ERRORS=1
)
echo.

REM Check gradlew.bat
if exist "gradlew.bat" (
    echo [OK] gradlew.bat
) else (
    echo [MISSING] gradlew.bat
    set ERRORS=1
)
echo.

REM Check build.gradle files
if exist "build.gradle" (
    echo [OK] build.gradle (root)
) else (
    echo [MISSING] build.gradle (root)
    set ERRORS=1
)
echo.

if exist "app\build.gradle" (
    echo [OK] app\build.gradle
) else (
    echo [MISSING] app\build.gradle
    set ERRORS=1
)
echo.

REM Check AndroidManifest.xml
if exist "app\src\main\AndroidManifest.xml" (
    echo [OK] AndroidManifest.xml
) else (
    echo [MISSING] AndroidManifest.xml
    set ERRORS=1
)
echo.

REM Check MainActivity
if exist "app\src\main\java\com\yessfish\app\MainActivity.kt" (
    echo [OK] MainActivity.kt
) else (
    echo [MISSING] MainActivity.kt
    set ERRORS=1
)
echo.

echo ========================================
if %ERRORS% EQU 0 (
    echo.
    echo [SUCCESS] All required files present!
    echo.
    echo You can now build the APK:
    echo.
    echo Option 1: Open in Android Studio
    echo   File -^> Open -^> Select this folder
    echo.
    echo Option 2: Command line
    echo   gradlew.bat clean
    echo   gradlew.bat assembleDebug
    echo.
    echo APK output: app\build\outputs\apk\debug\app-debug.apk
    echo.
) else (
    echo.
    echo [ERROR] Some files are missing!
    echo.
    echo Please re-download the anderoid folder from:
    echo sftp://root@185.165.242.58:2223/root/afbeeldingen/yessfish/app/anderoid
    echo.
    echo See GRADLE-FIX.md for more information.
    echo.
)
echo ========================================
echo.
pause
