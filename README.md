# YessFish Android App

Complete Android app voor YessFish - De sociale netwerk app voor vissers.

## 📱 App Features

- **WebView gebaseerde app** met native integratie
- **Custom headers** (`X-YessFish-Android-App`) voor platform detectie
- **GPS/Locatie** support voor visplekken
- **Camera** integratie voor vangst foto's
- **Offline caching** voor betere performance
- **Material Design 3** UI

## 🚀 Build Instructies

### Vereisten

- Android Studio Arctic Fox (2020.3.1) of nieuwer
- JDK 11 of nieuwer
- Android SDK 34 (Target SDK)
- Min SDK 24 (Android 7.0+)

### Stappen

1. **Open project in Android Studio**
   ```bash
   File → Open → Select /root/afbeeldingen/yessfish/app/android
   ```

2. **Gradle Sync**
   - Android Studio zal automatisch dependencies downloaden
   - Dit kan enkele minuten duren bij eerste keer

3. **Build de app**
   ```bash
   Build → Make Project (Ctrl+F9)
   ```

4. **Run op emulator of fysiek device**
   ```bash
   Run → Run 'app' (Shift+F10)
   ```

## 📦 Release Build (voor Play Store)

### 1. Maak signing key

```bash
keytool -genkey -v -keystore yessfish-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias yessfish
```

**Wachtwoorden** (zie gradle.properties):
- Store password: `YessFish2024!SecureKey`
- Key password: `YessFish2024!SecureKey`
- Alias: `yessfish`

### 2. Build Release APK

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### 3. Build App Bundle (voor Play Store)

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## 🏪 Google Play Store Submissie

### App Informatie

- **Package Name**: `com.yessfish.app`
- **App Name**: YessFish
- **Version**: 1.0.0 (versionCode 1)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Benodigde Assets

#### App Icons
- **512x512px** - High resolution icon (PNG)
- **192x192px** - Standard icon (PNG)

#### Screenshots (minimaal 2, maximaal 8)
- **Phone**: 1080x1920px of 1080x2340px
- **Tablet 7"**: 1200x1920px
- **Tablet 10"**: 1920x1200px

#### Feature Graphic
- **1024x500px** - Voor Play Store listing

#### Promo Video (optioneel)
- YouTube URL

### Privacy Policy

**Vereist!** Upload naar: https://yessfish.com/privacy-policy

Moet bevatten:
- Data collection (locatie, foto's)
- Data usage (app functionaliteit)
- Data sharing (none)
- User rights (GDPR compliance)

### Store Listing

**Titel**: YessFish - Social Network voor Vissers

**Korte beschrijving** (max 80 tekens):
```
Ontdek visplekken, deel vangsten en connect met vissers
```

**Volledige beschrijving** (max 4000 tekens):
```
🎣 YessFish - Jouw Digitale Vismaat!

Ontdek de ultieme app voor vissers in Nederland en België! YessFish is het sociale netwerk
waar vissers elkaar ontmoeten, ervaringen delen en samen de beste visplekken ontdekken.

✨ Features:

📍 Interactieve Kaart
• Ontdek duizenden visplekken
• Realtime drukte-indicatoren
• Check-in bij jouw favoriete wateren
• GPS navigatie naar spots

🎣 Deel je Vangsten
• Upload foto's van je vangsten
• Tag de locatie
• Ontvang likes en reacties
• Build je profiel

👥 Sociale Features
• Volg andere vissers
• Bekijk wat er bij jou in de buurt gebeurt
• Privé berichten
• Groepen en communities

🌊 Water Informatie
• SBuilder Water API integratie
• Diepte, oppervlakte, vissoorten
• Vergunningen en dagkaarten
• Faciliteiten per locatie

📊 Persoonlijke Statistieken
• Houd je vangsten bij
• Bekijk je progressie
• Verdien badges
• Vergelijk met vrienden

💯 Waarom YessFish?

• Gratis te gebruiken
• Privacy-first: jouw data blijft van jou
• Nederlandse app, gemaakt door vissers voor vissers
• Actieve community
• Regelmatige updates

Download nu en join de YessFish community! 🐟
```

**Categorie**: Sports

**Tags**:
- Fishing
- Social Network
- Outdoors
- Sports
- Netherlands
- Community

### Content Rating

- **Target Audience**: Everyone
- **Ads**: No
- **In-app purchases**: No (tenzij premium features)

### App Content

- **Contains ads**: No
- **In-app products**: No
- **Target age**: Everyone
- **Contains user generated content**: Yes

## 🧪 Testing

### Internal Testing
1. Upload naar Play Console
2. Add internal testers (via email)
3. Share test link

### Closed Testing (Beta)
1. Create closed test track
2. Add testers
3. Collect feedback

### Open Testing
1. Create open test track
2. Anyone can join
3. Final pre-release testing

### Production
1. Promote from testing
2. Staged rollout (5% → 20% → 50% → 100%)
3. Monitor crash reports

## 🔧 Configuratie

### Base URL
Gedefinieerd in `app/build.gradle`:
```gradle
buildConfigField "String", "BASE_URL", '"https://yessfish.com"'
```

### Custom Header
```gradle
buildConfigField "String", "APP_VERSION_HEADER", '"X-YessFish-Android-App"'
```

### Permissions

Required:
- `INTERNET` - WebView content
- `ACCESS_FINE_LOCATION` - GPS for fishing spots
- `ACCESS_COARSE_LOCATION` - Approximate location
- `CAMERA` - Photo uploads

Optional:
- `READ_MEDIA_IMAGES` - Gallery access
- `POST_NOTIFICATIONS` - Push notifications

## 📝 Version Management

Update versie in `app/build.gradle`:

```gradle
versionCode 1      // Increment for each release
versionName "1.0.0" // Semantic versioning
```

## 🐛 Debugging

### Enable WebView Debugging

In `YessFishApplication.kt`:
```kotlin
if (BuildConfig.DEBUG) {
    WebView.setWebContentsDebuggingEnabled(true)
}
```

Access via Chrome: `chrome://inspect`

### View Logs

```bash
adb logcat | grep YessFish
```

## 📚 Resources

- [Android Developer Guide](https://developer.android.com)
- [Play Console Help](https://support.google.com/googleplay/android-developer)
- [Material Design](https://material.io/design)
- [WebView Best Practices](https://developer.android.com/guide/webapps/webview)

## 🆘 Support

Voor vragen of problemen:
- Email: dev@yessfish.com
- Website: https://yessfish.com/contact

## 📄 License

Proprietary - © 2024 YessFish. All rights reserved.
