# Gradle Versie Fix

## ❌ Probleem
Je kreeg deze error:
```
'org.gradle.api.artifacts.Dependency org.gradle.api.artifacts.dsl.DependencyHandler.module(java.lang.Object)'
BUILD FAILED
```

**Oorzaak**: Gradle 9.0-milestone-1 (beta) werd gedownload, maar Android Gradle Plugin 8.1.4 is daar niet mee compatibel.

## ✅ Oplossing

Ik heb de juiste bestanden toegevoegd. Download de `anderoid` folder opnieuw!

**Nieuwe bestanden:**
- `gradle/wrapper/gradle-wrapper.properties` ← Specificeert Gradle 8.0
- `gradlew.bat` ← Windows build script

## 🚀 Wat je nu moet doen:

### Optie 1: In Android Studio (MAKKELIJKST)

1. **Sluit het project** als het open is
2. **Download de anderoid folder opnieuw** (met de nieuwe bestanden)
3. **Open het project opnieuw** in Android Studio
4. Android Studio zal nu Gradle 8.0 downloaden (automatisch, ~2 min)
5. **Wacht op "Gradle sync finished"**
6. **Build → Build APK**

### Optie 2: Via Command Line

Open Command Prompt in de `anderoid` folder:

```cmd
gradlew.bat clean
gradlew.bat assembleDebug
```

Dit zal:
1. Gradle 8.0 downloaden (automatisch)
2. Project cleanen
3. APK bouwen

Output: `app\build\outputs\apk\debug\app-debug.apk`

## 📋 Verificatie

Check of de nieuwe bestanden er zijn:

```
anderoid/
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties  ← NIEUW!
├── gradlew.bat                        ← NIEUW!
├── build.gradle
└── ...
```

Als deze bestanden er NIET zijn, download de folder opnieuw!

## 🔧 Alternatief: Handmatig Gradle Versie Forceren

Als het nog steeds niet werkt, open in Android Studio:

1. **File → Settings**
2. **Build, Execution, Deployment → Build Tools → Gradle**
3. **Gradle JDK**: Selecteer JDK 17 (of Embedded JDK)
4. **Use Gradle from**: Selecteer 'gradle-wrapper.properties file'
5. **Click OK**
6. **File → Sync Project with Gradle Files**

## 💡 Waarom gebeurde dit?

Gradle had geen wrapper bestanden, dus pakte het de laatste (unstable) versie. Nu forceren we Gradle 8.0 die wel compatibel is met Android Gradle Plugin 8.1.4.

## ✅ Success Indicator

Als het werkt zie je:
```
> Configure project :app
BUILD SUCCESSFUL in 2m 34s
```

En geen versie errors meer!
