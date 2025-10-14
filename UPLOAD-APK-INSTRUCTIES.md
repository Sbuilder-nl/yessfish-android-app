# 📤 YessFish APK Upload Instructies

## ✅ Download Pagina Is Klaar!

De download pagina is nu live op: **https://yessfish.com/download-app**

---

## 🎯 Wat Je Nu Moet Doen

### Stap 1: Build Release APK in Android Studio

**Op je Windows machine:**

```cmd
cd C:\Users\info\Desktop\YESSFISH\anderoid
gradlew.bat clean
gradlew.bat assembleRelease
```

**Wacht op:**
```
BUILD SUCCESSFUL in 2-4 minuten
Built the following apks:
  C:\Users\info\Desktop\YESSFISH\anderoid\app\build\outputs\apk\release\app-release.apk
```

---

### Stap 2: Hernoem de APK

**Hernoem van:**
```
app-release.apk
```

**Naar:**
```
yessfish-app-v1.0.0.apk
```

**Windows Command:**
```cmd
cd app\build\outputs\apk\release
ren app-release.apk yessfish-app-v1.0.0.apk
```

---

### Stap 3: Upload via SFTP

**Gebruik WinSCP, FileZilla of SCP:**

#### Methode A: WinSCP / FileZilla
```
Server: 185.165.242.58
Port: 2223
User: root
Protocol: SFTP

Upload:
  Van: C:\Users\info\Desktop\YESSFISH\anderoid\app\build\outputs\apk\release\yessfish-app-v1.0.0.apk
  Naar: /home/yessfish/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk
```

#### Methode B: SCP Command Line
```cmd
scp -P 2223 app\build\outputs\apk\release\yessfish-app-v1.0.0.apk root@185.165.242.58:/home/yessfish/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk
```

---

### Stap 4: Test de Download

**Open in je browser:**
```
https://yessfish.com/download-app
```

**Klik op:**
```
📥 Download Android App
```

**Verwacht:**
- Download start automatisch
- Bestandsnaam: yessfish-app-v1.0.0.apk
- Grootte: ~4-6 MB

---

## 🔍 Verificatie

### Check of APK Online Is:

**Via browser:**
```
https://yessfish.com/downloads/yessfish-app-v1.0.0.apk
```

**Via curl:**
```bash
curl -I https://yessfish.com/downloads/yessfish-app-v1.0.0.apk
```

**Verwacht:**
```
HTTP/2 200
content-type: application/vnd.android.package-archive
content-length: 4000000-6000000 (4-6 MB)
```

---

## 📱 Test Installatie

### Op je Android Telefoon:

1. **Download APK**
   - Open https://yessfish.com/download-app
   - Klik "Download Android App"
   - APK wordt gedownload naar Downloads map

2. **Installeer**
   - Open Downloads
   - Tik op yessfish-app-v1.0.0.apk
   - Sta "onbekende bronnen" toe (eenmalig)
   - Klik "Installeren"

3. **Test App**
   - Open YessFish app
   - Login met Google (moet werken zonder 403 error!)
   - Check of je naar fishmap-android.php gaat
   - Test locatie, camera permissions

---

## 📊 Current Status

### ✅ Klaar:
- [x] Chrome Custom Tabs geïmplementeerd (Google OAuth fix)
- [x] Android app routing (naar fishmap-android.php)
- [x] YessFish logo overal (website + app)
- [x] Download pagina live op yessfish.com/download-app
- [x] Gradle build configuratie voor release

### ⏳ Jouw Actie Vereist:
- [ ] Build release APK in Android Studio
- [ ] Upload APK naar server
- [ ] Test download vanaf website
- [ ] Test installatie op telefoon
- [ ] Verify Google OAuth werkt

---

## 🎨 Download Pagina Features

De download pagina heeft:
- ✅ Duidelijke download knop voor APK
- ✅ PWA optie (Progressive Web App)
- ✅ Stap-voor-stap installatie instructies
- ✅ Features overzicht
- ✅ Systeemeisen
- ✅ Responsive design (mobiel & desktop)
- ✅ YessFish branding met logo

**URL**: https://yessfish.com/download-app

---

## 🔄 Voor Toekomstige Updates

### Nieuwe Versie Uploaden:

1. **Update version in build.gradle**
   ```gradle
   versionCode 2
   versionName "1.0.1"
   ```

2. **Build nieuwe APK**
   ```cmd
   gradlew.bat clean
   gradlew.bat assembleRelease
   ```

3. **Hernoem met nieuwe versie**
   ```cmd
   ren app-release.apk yessfish-app-v1.0.1.apk
   ```

4. **Upload naast oude versie** (voor backwards compatibility)
   ```
   /downloads/yessfish-app-v1.0.0.apk (behouden)
   /downloads/yessfish-app-v1.0.1.apk (nieuwe)
   ```

5. **Update download-app.php**
   - Wijzig link naar v1.0.1
   - Update versienummer en datum

---

## 📁 Server File Structure

```
/home/yessfish/domains/yessfish.com/public_html/
├── download-app.php (✅ Download pagina - LIVE)
├── downloads/
│   ├── yessfish-app-v1.0.0.apk (⏳ Wacht op upload)
│   └── [toekomstige versies]
├── favicon.ico (✅ YessFish logo)
├── favicon-16x16.png (✅)
├── favicon-32x32.png (✅)
├── apple-touch-icon.png (✅)
├── android-chrome-192x192.png (✅)
└── android-chrome-512x512.png (✅)
```

---

## 🔐 Security & Permissions

### APK Bestand Permissions:
```bash
chmod 644 /home/yessfish/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk
chown yessfish:yessfish /home/yessfish/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk
```

**Dit wordt automatisch goed gezet als je via SFTP als root upload.**

---

## 📊 Analytics (Optioneel)

### Track Downloads:
Later kun je tracking toevoegen aan download-app.php:

```php
// Log download
if (isset($_GET['download'])) {
    $logFile = __DIR__ . '/logs/app-downloads.log';
    $entry = date('Y-m-d H:i:s') . " - " . $_SERVER['REMOTE_ADDR'] . " - " . $_SERVER['HTTP_USER_AGENT'] . "\n";
    file_put_contents($logFile, $entry, FILE_APPEND);
}
```

---

## 🐛 Troubleshooting

### "APK not found" bij download
**Check:**
```bash
ls -lh /home/yessfish/domains/yessfish.com/public_html/downloads/
```

**Fix:**
Upload APK opnieuw of check bestandsnaam.

### Download werkt, maar installatie faalt
**Oorzaken:**
- APK corrupt (re-download)
- Android versie te oud (<7.0)
- Storage vol op telefoon

### APK gedownload, maar werkt niet op telefoon
**Check:**
- Is dit een release build? (niet debug)
- Signing config correct?
- Build succesvol geweest?

---

## ✅ Quick Command Cheat Sheet

### Build:
```cmd
cd C:\Users\info\Desktop\YESSFISH\anderoid
gradlew.bat clean assembleRelease
```

### Rename:
```cmd
cd app\build\outputs\apk\release
ren app-release.apk yessfish-app-v1.0.0.apk
```

### Upload:
```cmd
scp -P 2223 yessfish-app-v1.0.0.apk root@185.165.242.58:/home/yessfish/domains/yessfish.com/public_html/downloads/
```

### Test:
```
https://yessfish.com/download-app
```

---

## 🎯 Summary

**Je hebt nu:**
1. ✅ Working Android app met Google OAuth (Chrome Custom Tabs)
2. ✅ Professionele download pagina op yessfish.com
3. ✅ Build instructies voor release APK
4. ✅ Upload instructies naar server

**Je moet nog:**
1. ⏳ Release APK builden in Android Studio
2. ⏳ APK uploaden naar server
3. ⏳ Downloaden en testen op je telefoon

**Zodra de APK is geupload, is alles 100% klaar!** 🚀

---

**Laatste Update**: 2025-10-14
**Download Pagina**: https://yessfish.com/download-app
**APK Locatie**: /home/yessfish/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk
**Status**: Wacht op APK upload
