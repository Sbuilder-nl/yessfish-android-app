# YessFish Android App - Windows Build Guide

## ✅ Je hebt nu gedownload: Complete Android Studio project

**Locatie op server**: `sftp://root@185.165.242.58:2223/root/afbeeldingen/yessfish/app/anderoid`

---

## 🚀 STAPPEN OM APK TE BOUWEN

### Stap 1: Open Project in Android Studio

1. **Start Android Studio**
2. **File → Open** (of "Open an Existing Project" op startscherm)
3. **Navigeer naar** de gedownloade `anderoid` folder
4. **Selecteer de anderoid folder** (niet een bestand erin!)
5. **Klik OK**

Android Studio gaat nu:
- Project inladen
- Gradle sync uitvoeren (eerste keer kan 5-10 min duren)
- Dependencies downloaden

**⏳ Wacht tot je onderaan ziet: "Gradle sync finished"**

---

### Stap 2: Gradle Wrapper Toevoegen (Belangrijk!)

Het project mist de Gradle wrapper files. Voeg deze toe:

**In Android Studio Terminal** (onderaan):

```bash
gradle wrapper --gradle-version 8.1.4
```

Of **handmatig**:

1. Download Gradle wrapper van: https://services.gradle.org/distributions/gradle-8.1.4-bin.zip
2. Pak uit naar: `C:\gradle` (of andere locatie)
3. Voeg toe aan Windows PATH:
   - Zoek "Environment Variables" in Windows
   - Voeg toe: `C:\gradle\gradle-8.1.4\bin`

---

### Stap 3: Build Project

**Optie A: Via Menu (Makkelijkst)**
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

**Optie B: Via Terminal**
```bash
cd anderoid
gradlew.bat assembleDebug
```

**⏳ Wacht ~2-5 minuten**

Je ziet onderaan de progress bar.

---

### Stap 4: Vind je APK

Als build succesvol is, zie je:

```
BUILD SUCCESSFUL in 2m 34s
```

**APK locatie:**
```
anderoid/app/build/outputs/apk/debug/app-debug.apk
```

**In Android Studio:**
- Klik op link in build output: "locate"
- Of: `Build → Analyze APK` → Selecteer de APK

---

### Stap 5: Test op Telefoon

**Optie A: USB Debugging**

1. **Op je Android telefoon:**
   - Ga naar `Instellingen → Over telefoon`
   - Tap 7x op "Build nummer"
   - Developer opties zijn nu enabled
   - Ga naar `Instellingen → Developer Options`
   - Zet `USB Debugging` AAN

2. **Verbind telefoon met USB kabel**

3. **In Android Studio:**
   - Selecteer je device bovenaan (naast groene play knop)
   - Klik groene play knop ▶️
   - Of: `Run → Run 'app'`

4. **Op telefoon:** Accept USB debugging prompt

5. **App installeert en start automatisch!**

**Optie B: APK delen**

1. **Kopieer** `app-debug.apk` naar je telefoon (via USB/Bluetooth/email)
2. **Op telefoon:** Open de APK
3. **Allow** "Install from unknown sources"
4. **Installeer**
5. **Open app**

---

## 🐛 TROUBLESHOOTING

### Error: "Gradle sync failed"

**Oplossing 1**: Controleer internet verbinding
```
File → Settings → Appearance & Behavior → System Settings → HTTP Proxy
Zet op "No proxy" of configureer juiste proxy
```

**Oplossing 2**: Clear cache
```
File → Invalidate Caches / Restart
```

**Oplossing 3**: Update Android Studio
```
Help → Check for Updates
```

### Error: "SDK not found"

```
File → Project Structure → SDK Location
Download Android SDK (klik op "Download Android SDK")
Selecteer SDK locatie: C:\Users\[JouwNaam]\AppData\Local\Android\Sdk
```

### Error: "Gradle version mismatch"

Open `build.gradle` (root) en zorg dat versie klopt:
```gradle
classpath 'com.android.tools.build:gradle:8.1.4'
```

### Error: "Java version incompatible"

Android Studio gebruikt verkeerde Java versie.

```
File → Settings → Build, Execution, Deployment → Build Tools → Gradle
Gradle JDK: Selecteer "JDK 17" of "Embedded JDK"
```

### Error: "Device not found"

Telefoon niet herkend:

1. **Windows drivers:**
   - Google "USB drivers" + je telefoon merk
   - Download en installeer manufacturer drivers

2. **Controleer USB debugging:**
   - Disconnect en reconnect USB kabel
   - Accepteer USB debugging popup op telefoon

3. **Verifieer in CMD:**
   ```cmd
   cd C:\Users\[JouwNaam]\AppData\Local\Android\Sdk\platform-tools
   adb devices
   ```
   Zou je telefoon moeten tonen.

### Build duurt lang / hangt

**Eerste keer:** Kan 10-15 min duren (downloads dependencies)

**Versnellen:**
```
File → Settings → Build, Execution, Deployment → Compiler
Parallel build: Check
Build process heap size: 2048 MB
```

---

## ✅ SUCCESS CHECKLIST

Na succesvolle build heb je:

- [x] `app-debug.apk` file (±15-20 MB)
- [x] App installeert op telefoon zonder errors
- [x] App start met YessFish splash screen
- [x] WebView laadt yessfish.com
- [x] Custom headers worden verstuurd (check in Chrome DevTools)

---

## 🎯 RELEASE BUILD (Voor Play Store)

De debug APK is voor testen. Voor Play Store heb je Release build nodig:

```bash
gradlew.bat bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

**Let op:** Release build moet signed zijn met keystore!

Zie `COMPLETE-APP-GUIDE.md` voor signing instructies.

---

## 🔍 VERIFICATIE

Test of custom headers werken:

1. **Open app op telefoon**
2. **Open Chrome DevTools op PC:**
   ```
   chrome://inspect
   ```
3. **Selecteer je device**
4. **Inspect WebView**
5. **Network tab → Refresh**
6. **Click op fishmap.php request**
7. **Headers tab**
8. **Zoek:** `X-YessFish-Android-App: 1.0.0`

Als je deze header ziet → Perfect! ✅

---

## 📞 HULP NODIG?

**Build errors:**
- Check Android Studio "Build" output tab
- Copy error message en google
- Check Stack Overflow

**App crashes:**
- Check Logcat in Android Studio (onderaan)
- Filter op "YessFish"

**Vragen:**
- Check README.md in project folder
- Check COMPLETE-APP-GUIDE.md voor store deployment

---

## 🎉 NEXT STEPS

Na succesvolle APK:

1. **Test grondig** op meerdere devices
2. **Maak app icon** (512x512px)
3. **Neem screenshots** voor Play Store
4. **Upload naar Play Console**
5. **Submit voor review**

---

**Good luck! 🚀**
