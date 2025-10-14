# ✅ Build Verificatie Checklist

## Voordat je de APK build:

### 1. Controleer of deze bestanden aanwezig zijn:

```
anderoid/
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties  ← MOET ER ZIJN! (Nieuw toegevoegd)
├── gradlew.bat                        ← MOET ER ZIJN! (Nieuw toegevoegd)
├── build.gradle                       ← Android project build file
├── settings.gradle                    ← Project settings
└── app/
    ├── build.gradle                   ← App module build file
    └── src/
        └── main/
            ├── AndroidManifest.xml
            └── java/com/yessfish/app/
                └── MainActivity.kt
```

### 2. Controleer Gradle wrapper versie:

Open `gradle/wrapper/gradle-wrapper.properties` en controleer:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
```

**✅ GOED**: Gradle 8.0
**❌ FOUT**: Gradle 9.0 of andere versie

---

## Build Opties

### Optie A: Android Studio (Makkelijkst)

1. **Open Android Studio**
2. **File → Open**
3. **Selecteer de `anderoid` folder**
4. **Wacht op "Gradle sync finished"** (~2-5 min eerste keer)
5. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
6. **Wacht op "BUILD SUCCESSFUL"**
7. **APK locatie**: `app/build/outputs/apk/debug/app-debug.apk`

### Optie B: Command Line

Open **Command Prompt** in de `anderoid` folder:

```cmd
gradlew.bat clean
gradlew.bat assembleDebug
```

**Output verwacht**:
```
BUILD SUCCESSFUL in 2m 34s
```

**APK locatie**: `app\build\outputs\apk\debug\app-debug.apk`

---

## Troubleshooting

### Als je nog steeds Gradle 9.0 error krijgt:

1. **Verwijder oude Gradle cache:**
   ```cmd
   rmdir /s /q %USERPROFILE%\.gradle\wrapper\dists\gradle-9.0-milestone-1-bin
   ```

2. **Forceer Gradle wrapper opnieuw:**
   In Android Studio:
   - **File → Settings**
   - **Build, Execution, Deployment → Build Tools → Gradle**
   - **Use Gradle from**: Selecteer **'gradle-wrapper.properties file'**
   - **Click OK**
   - **File → Sync Project with Gradle Files**

3. **Of handmatig Gradle downloaden:**
   - Download: https://services.gradle.org/distributions/gradle-8.0-bin.zip
   - Pak uit naar: `C:\gradle\gradle-8.0`
   - Voeg toe aan PATH in Windows environment variables

---

## Success Indicatoren

### Build Successful

Je ziet in de output:

```
> Configure project :app
...
BUILD SUCCESSFUL in 2m 34s
45 actionable tasks: 45 executed
```

### APK Gevonden

```
app/build/outputs/apk/debug/app-debug.apk
Grootte: ~15-20 MB
```

### APK Testen

1. **Kopieer APK naar Android telefoon**
2. **Open APK file op telefoon**
3. **Allow "Install from unknown sources"**
4. **Installeer**
5. **Open YessFish app**
6. **Controleer**:
   - App start met splash screen
   - Laadt yessfish.com/fishmap.php
   - Fishmap wordt geladen
   - Locatie werkt (als GPS permission gegeven)

---

## Custom Headers Verificatie

Om te controleren of de app de juiste headers stuurt:

1. **Verbind telefoon via USB**
2. **Enable USB debugging**
3. **Open Chrome op PC**
4. **Ga naar**: `chrome://inspect`
5. **Selecteer je device**
6. **Inspect de YessFish WebView**
7. **Network tab → Refresh**
8. **Click op fishmap.php request**
9. **Headers tab**
10. **Zoek**: `X-YessFish-Android-App: 1.0.0`

Als je deze header ziet → **Perfect!** ✅

---

## Veelvoorkomende Errors

### Error: "SDK not found"

```
File → Project Structure → SDK Location
Download Android SDK (klik Download)
```

### Error: "Java version incompatible"

```
File → Settings → Build Tools → Gradle
Gradle JDK: Selecteer "JDK 17" of "Embedded JDK"
```

### Error: Build hangs

Eerste build kan 10-15 min duren (downloads dependencies).

**Versnellen**:
```
File → Settings → Compiler
☑ Parallel build
Build process heap size: 2048 MB
```

---

## 🎯 Volgende Stappen

Na succesvolle APK build:

1. ✅ Test APK op je Android telefoon
2. ✅ Controleer alle features (map, locatie, posts)
3. ✅ Test op meerdere devices als mogelijk
4. ✅ Maak app icon (512x512px)
5. ✅ Neem screenshots voor Play Store
6. ✅ Upload naar Google Play Console

---

## 📞 Quick Reference

**Build commando**: `gradlew.bat assembleDebug`
**Clean commando**: `gradlew.bat clean`
**APK locatie**: `app\build\outputs\apk\debug\app-debug.apk`
**Gradle versie**: 8.0
**Android Gradle Plugin**: 8.1.4
**Target SDK**: 34 (Android 14)
**Min SDK**: 24 (Android 7.0)

---

**Let op**: Re-download de `anderoid` folder als je de Gradle fix nog niet hebt!

**Good luck!** 🚀
