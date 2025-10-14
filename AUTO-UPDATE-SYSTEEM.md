# 🔄 YessFish Auto-Update Systeem

## Overzicht

De YessFish Android app heeft een **automatisch update systeem** dat:
- ✅ Bij elke app start checkt voor nieuwe versies
- ✅ Gebruikers informeert met een mooie dialog + changelog
- ✅ Automatisch nieuwe APK downloadt via DownloadManager
- ✅ Gebruiker vraagt om installatie te bevestigen
- ✅ Optionele én verplichte updates ondersteunt

## Hoe Werkt Het?

### 1. Server-Side: version.json

**Locatie:** `https://yessfish.com/api/version.json`

```json
{
  "latestVersion": "1.0.1",
  "latestVersionCode": 2,
  "minimumVersion": "1.0.0",
  "minimumVersionCode": 1,
  "downloadUrl": "https://yessfish.com/downloads/yessfish-app-v1.0.1.apk",
  "fileSize": "4.2 MB",
  "releaseDate": "2025-10-14",
  "forceUpdate": false,
  "changelog": {
    "nl": [
      "✅ Nieuwe feature 1",
      "✅ Fix voor bug X"
    ],
    "en": [
      "✅ New feature 1",
      "✅ Fix for bug X"
    ]
  },
  "releaseNotes": "Beschrijving van deze release"
}
```

### 2. Android-Side: UpdateChecker.kt

**Klasse:** `UpdateChecker(context: Context)`

**Belangrijkste functies:**
- `checkForUpdates()`: Fetch version.json van server (async)
- `isUpdateAvailable()`: Vergelijk versies
- `isUpdateRequired()`: Check of update verplicht is
- `showUpdateDialog()`: Toon update prompt met changelog
- `downloadAndInstallUpdate()`: Download APK en trigger installatie

### 3. Integratie in MainActivity

```kotlin
private fun checkForUpdates() {
    lifecycleScope.launch {
        val updateChecker = UpdateChecker(this@MainActivity)
        val versionInfo = updateChecker.checkForUpdates()

        if (versionInfo != null && updateChecker.isUpdateAvailable(versionInfo)) {
            updateChecker.showUpdateDialog(versionInfo) {
                updateChecker.downloadAndInstallUpdate(versionInfo)
            }
        }
    }
}
```

## Nieuwe Versie Uitrollen

### Stap 1: Build nieuwe APK

```bash
cd /root/afbeeldingen/yessfish/app/anderoid

# Update versie in app/build.gradle:
# versionCode 2
# versionName "1.0.1"

./gradlew assembleRelease
```

### Stap 2: Upload APK naar server

```bash
# Copy APK
cp app/build/outputs/apk/release/app-release.apk \
   /home/admin/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.1.apk

# Set permissions
chown admin:admin /home/admin/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.1.apk
chmod 644 /home/admin/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.1.apk
```

### Stap 3: Update version.json

Edit `/home/admin/domains/yessfish.com/public_html/api/version.json`:

```json
{
  "latestVersion": "1.0.1",
  "latestVersionCode": 2,
  "downloadUrl": "https://yessfish.com/downloads/yessfish-app-v1.0.1.apk",
  "releaseDate": "2025-10-14",
  "forceUpdate": false,
  "changelog": {
    "nl": [
      "✅ Wat je hebt toegevoegd/gefixt",
      "🐛 Bug fixes"
    ]
  }
}
```

### Stap 4: Test Update Flow

1. **Open app op telefoon** (oude versie)
2. **App start** → checkt automatisch version.json
3. **Dialog verschijnt** met changelog
4. **Klik "Updaten"** → download start
5. **Installatie prompt** → gebruiker moet bevestigen
6. **App updatet** → nieuwe versie draait

## Verplichte Updates (Force Update)

Als je een **kritieke security fix** hebt:

```json
{
  "forceUpdate": true,
  "minimumVersion": "1.0.1",
  "minimumVersionCode": 2
}
```

Dan:
- ❌ Geen "Later" knop in dialog
- ❌ Dialog kan niet worden gesloten
- ⚠️ App werkt niet zonder update

## Versie Nummering

**Semantic Versioning:**
- `1.0.0` → Major.Minor.Patch
- `versionCode` → Integer die ALTIJD omhoog gaat (1, 2, 3, ...)
- `versionName` → String die gebruiker ziet ("1.0.1")

**Voorbeeld progressie:**
```
v1.0.0 (code 1)  → Initial release
v1.0.1 (code 2)  → Bug fix
v1.0.2 (code 3)  → Another fix
v1.1.0 (code 4)  → New feature
v2.0.0 (code 5)  → Major update
```

## Benodigde Permissions

In `AndroidManifest.xml`:

```xml
<!-- Voor download -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Voor APK installatie (Android 8.0+) -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

<!-- Voor downloads (automatisch toegestaan) -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

## Testen

### Test Update Dialog (zonder echte update)

```kotlin
// In MainActivity, tijdelijk:
private fun testUpdateDialog() {
    val fakeVersion = UpdateChecker.VersionInfo(
        latestVersion = "1.0.1",
        latestVersionCode = 2,
        minimumVersion = "1.0.0",
        minimumVersionCode = 1,
        downloadUrl = "https://yessfish.com/downloads/test.apk",
        fileSize = "4.2 MB",
        releaseDate = "2025-10-14",
        forceUpdate = false,
        changelog = listOf("✅ Test feature", "🐛 Test fix"),
        releaseNotes = "Test release"
    )

    val updateChecker = UpdateChecker(this)
    updateChecker.showUpdateDialog(fakeVersion) {
        // Test download
    }
}
```

### Test version.json endpoint

```bash
curl https://yessfish.com/api/version.json
```

## Troubleshooting

### "Download Failed"
- Check INTERNET permission
- Check file permissions op server (chmod 644)
- Check HTTPS certificate

### "Install Failed"
- Check REQUEST_INSTALL_PACKAGES permission
- Check if APK is signed
- Android 8.0+ moet "Install unknown apps" toestaan voor de app

### "Dialog verschijnt niet"
- Check version.json bereikbaar is
- Check versionCode in app/build.gradle
- Check logs: `adb logcat -s YessFish-UpdateChecker`

## GitHub Releases (Toekomst)

In de toekomst kunnen we overstappen naar **GitHub Releases**:

```json
{
  "downloadUrl": "https://github.com/Sbuilder-nl/yessfish-android-app/releases/download/v1.0.1/app-release.apk"
}
```

Voordelen:
- ✅ Versie geschiedenis automatisch
- ✅ Release notes in GitHub
- ✅ CDN voor snelle downloads
- ✅ Statistieken over downloads

## Best Practices

1. **Test altijd eerst** op test device
2. **Incrementeer altijd versionCode** bij elke build
3. **Schrijf duidelijke changelogs** (Nederlands + Engels)
4. **Gebruik forceUpdate spaarzaam** (alleen voor kritieke fixes)
5. **Keep oude APK's** als backup (voor rollback)
6. **Monitor crash reports** na update

## Server Backup

Backup van huidige production APK:

```bash
cp /home/admin/domains/yessfish.com/public_html/downloads/yessfish-app-v1.0.0.apk \
   /root/backups/apks/yessfish-app-v1.0.0-$(date +%Y%m%d).apk
```

## Changelog Template

```json
{
  "changelog": {
    "nl": [
      "✨ Nieuwe features",
      "  • Feature 1 beschrijving",
      "  • Feature 2 beschrijving",
      "🐛 Bug fixes",
      "  • Fix voor probleem X",
      "  • Fix voor probleem Y",
      "⚡ Performance",
      "  • Snellere laadtijden",
      "🔒 Security",
      "  • Security verbetering"
    ],
    "en": [
      "✨ New features",
      "  • Feature 1 description",
      "🐛 Bug fixes",
      "  • Fixed issue X"
    ]
  }
}
```

---

🐟 **YessFish Auto-Update System**
Versie 1.0 - Oktober 2025
