# 🚀 YessFish Android App - Release APK Bouwen

## 📱 Release APK voor Distributie

Deze guide laat zien hoe je een **release APK** bouwt voor distributie via yessfish.com (niet via Google Play Store).

---

## 🎯 Wat Is Een Release APK?

### Debug vs Release:
| Type | Gebruik | Optimalisatie | Grootte | Signing |
|------|---------|---------------|---------|---------|
| **Debug** | Development/testing | Geen | ~8-12 MB | Debug keystore |
| **Release** | Productie/distributie | Ja (minify) | ~4-6 MB | Release keystore |

---

## 📋 Vereisten

### Voordat Je Begint:
- [x] Android Studio geïnstalleerd
- [x] YessFish anderoid project geopend
- [x] Gradle sync succesvol
- [x] Debug build werkt (getest op telefoon)

---

## 🔧 Methode 1: Android Studio GUI (Makkelijkst)

### Stap 1: Open Build Menu
```
Android Studio > Build > Generate Signed Bundle / APK
```

### Stap 2: Selecteer APK
- Kies: **APK** (niet AAB/Bundle)
- Klik: **Next**

### Stap 3: Keystore (Voor Nu: Debug Keystore)
**Optie A: Debug Keystore Gebruiken (Snel)**
```
Key store path: (Laat leeg of gebruik debug keystore)
Klik: Cancel (ga terug)
```

We gebruiken de debug keystore die al in build.gradle staat:
```gradle
release {
    signingConfig signingConfigs.debug  // Gebruikt debug keystore
}
```

### Stap 4: Build Release APK
```
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

Android Studio toont:
```
Build > Build Output > locate
```

### Stap 5: Vind APK
Locatie:
```
C:\Users\info\Desktop\YESSFISH\anderoid\app\build\outputs\apk\release\app-release.apk
```

---

## 🔧 Methode 2: Gradle Command Line (Sneller)

### In Android Studio Terminal:
```cmd
gradlew.bat assembleRelease
```

### In Windows CMD:
```cmd
cd C:\Users\info\Desktop\YESSFISH\anderoid
gradlew.bat clean
gradlew.bat assembleRelease
```

### Build Output:
```
> Task :app:assembleRelease

BUILD SUCCESSFUL in 3m 12s
68 actionable tasks: 68 executed

Built the following apks:
  C:\Users\info\Desktop\YESSFISH\anderoid\app\build\outputs\apk\release\app-release.apk
```

---

## 📦 APK Locatie

### Release APK:
```
app\build\outputs\apk\release\app-release.apk
```

### File Properties:
- **Naam**: app-release.apk
- **Grootte**: ~4-6 MB (kleiner dan debug versie!)
- **Geoptimaliseerd**: Ja (minify + shrinkResources)
- **Signed**: Ja (debug keystore voor nu)

---

## ✅ Verificatie

### Check APK Details:
```cmd
cd app\build\outputs\apk\release
dir app-release.apk
```

### Install Test (Optioneel):
```cmd
adb install app-release.apk
```

### App Info Check:
```cmd
aapt dump badging app-release.apk | findstr "package versionCode versionName"
```

**Expected Output:**
```
package: name='com.yessfish.app' versionCode='1' versionName='1.0.0'
```

---

## 📤 Upload Naar Server

### Methode 1: SFTP (WinSCP / FileZilla)
```
Server: 185.165.242.58
Port: 2223
User: root
Protocol: SFTP

Upload van:
  C:\Users\info\Desktop\YESSFISH\anderoid\app\build\outputs\apk\release\app-release.apk

Naar:
  /home/yessfish/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk
```

### Methode 2: SCP Command Line
```cmd
scp -P 2223 app\build\outputs\apk\release\app-release.apk root@185.165.242.58:/home/yessfish/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk
```

---

## 🌐 Download Pagina

### URL:
```
https://yessfish.com/download-app
```

### Features:
- APK download link
- Installatie instructies
- Systeemeisen
- Screenshots
- Changelog

---

## 🔒 Signing Config (Huidige Setup)

### Debug Keystore (Tijdelijk)
**build.gradle configuratie:**
```gradle
release {
    minifyEnabled true
    shrinkResources true
    proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'

    // Uses debug signing for now (until release keystore is created)
    signingConfig signingConfigs.debug
}
```

**Voordelen:**
- ✅ Snel te implementeren
- ✅ Geen keystore management nodig
- ✅ Werkt voor beta distributie

**Nadelen:**
- ⚠️ Niet geschikt voor Google Play Store
- ⚠️ Apps met debug keystore kunnen niet worden ge-update via Play Store

**Voor Nu**: Debug keystore is prima voor directe download distributie!

---

## 🔐 Release Keystore (Toekomst)

### Voor Google Play Store Publicatie:
Je moet later een **release keystore** maken:

```cmd
keytool -genkey -v -keystore yessfish-release.keystore -alias yessfish -keyalg RSA -keysize 2048 -validity 10000
```

**Vragen:**
```
Enter keystore password: [kies sterk wachtwoord]
What is your first and last name?: YessFish BV
What is your organization?: YessFish
What is your City or Locality?: Amsterdam
What is your State or Province?: Noord-Holland
What is your country code?: NL
```

### Update build.gradle:
```gradle
signingConfigs {
    release {
        storeFile file("yessfish-release.keystore")
        storePassword "your-keystore-password"
        keyAlias "yessfish"
        keyPassword "your-key-password"
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release  // Gebruik release keystore
    }
}
```

**⚠️ BELANGRIJK**: Bewaar keystore + passwords veilig! Als je deze verliest, kun je nooit meer updates pushen naar de Play Store!

---

## 🎨 Build Variants

### Beschikbare Builds:
```
Debug:   app-debug.apk   (development)
Release: app-release.apk (production)
```

### Switch in Android Studio:
```
View > Tool Windows > Build Variants
Select: release
Run > Run 'app'
```

---

## 📊 APK Optimalisatie (Ingebouwd)

### Release Build Optimalisaties:
1. **Code Shrinking** (R8/ProGuard)
   - Verwijdert ongebruikte code
   - Obfuscates code (moeilijker te reverse engineeren)

2. **Resource Shrinking**
   - Verwijdert ongebruikte resources (images, layouts)
   - Reduceert APK grootte

3. **Compression**
   - Comprimeert alle assets
   - Kleinere download size

### Result:
```
Debug APK:   ~8-12 MB
Release APK: ~4-6 MB  (50% kleiner!)
```

---

## 🐛 Troubleshooting

### Error: "Cannot resolve symbol BuildConfig"
**Fix:**
```
Build > Clean Project
Build > Rebuild Project
```

### Error: "Signing config not found"
**Fix:** Check build.gradle regel 42:
```gradle
signingConfig signingConfigs.debug
```

### Release APK Crashed Bij Start
**Oorzaak:** ProGuard heeft te veel code verwijderd
**Fix:** Update proguard-rules.pro:
```
-keep class com.yessfish.app.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
```

### APK Te Groot (>10 MB)
**Check:**
- Ongebruikte libraries verwijderen
- Image assets optimaliseren (gebruik WebP)
- Check build.gradle: `shrinkResources true`

---

## 📝 Checklist Voor Release

### Pre-Build:
- [ ] Alle features getest in debug build
- [ ] Google OAuth werkt (Chrome Custom Tabs)
- [ ] App routing werkt (fishmap-android.php)
- [ ] Logo's zichtbaar (alle densities)
- [ ] Geen debug logs in code

### Build:
- [ ] `gradlew.bat clean` uitgevoerd
- [ ] `gradlew.bat assembleRelease` succesvol
- [ ] APK grootte check (~4-6 MB)

### Post-Build:
- [ ] APK geïnstalleerd op test device
- [ ] App opent zonder crashes
- [ ] Google login werkt
- [ ] Map functionaliteit werkt
- [ ] Permissions (locatie, camera) werken

### Upload:
- [ ] APK hernoemen: `yessfish-app-v1.0.0.apk`
- [ ] Upload naar server `/downloads/` folder
- [ ] Permissions: `chmod 644`
- [ ] Test download link in browser

---

## 🚀 Distributie Plan

### Fase 1: Beta Testing (Nu)
- ✅ Directe APK download via yessfish.com
- ✅ Debug signing (tijdelijk)
- ✅ Kleine groep beta testers

### Fase 2: Public Release
- Release keystore maken
- Rebuild met release keystore
- Bredere distributie via website

### Fase 3: Google Play Store
- Google Play Developer account ($25 eenmalig)
- Release keystore signing
- App listing + screenshots
- Review process (~3-7 dagen)

---

## 🎯 Huidige Status

| Component | Status | Notes |
|-----------|--------|-------|
| Gradle build configuratie | ✅ | Release variant klaar |
| Code optimalisatie | ✅ | minifyEnabled true |
| Resource shrinking | ✅ | shrinkResources true |
| Signing | ✅ | Debug keystore (tijdelijk) |
| ProGuard rules | ✅ | Basic rules aanwezig |

**Status**: Klaar om release APK te bouwen!

---

## 📞 Build Commando's Samenvatting

### Clean Build:
```cmd
cd C:\Users\info\Desktop\YESSFISH\anderoid
gradlew.bat clean
```

### Build Release APK:
```cmd
gradlew.bat assembleRelease
```

### Locatie Check:
```cmd
dir app\build\outputs\apk\release\app-release.apk
```

### Upload (Windows):
```cmd
scp -P 2223 app\build\outputs\apk\release\app-release.apk root@185.165.242.58:/home/yessfish/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk
```

---

**Laatste Update**: 2025-10-14
**Versie**: 1.0.0
**Build Type**: Release (debug signing)
**Distributie**: yessfish.com/download-app
**Play Store**: Later (na release keystore)
