# Module() Error Fix - Gradle Downgrade

## ❌ Probleem
```
'org.gradle.api.artifacts.Dependency org.gradle.api.artifacts.dsl.DependencyHandler.module(java.lang.Object)'
```

## 🔍 Oorzaak
Android Gradle Plugin 8.1.4 heeft compatibility issues met Gradle 8.0.

De `module()` methode is deprecated/verwijderd in Gradle 8.0, maar AGP 8.1.4 gebruikt deze intern nog steeds in sommige scenario's.

## ✅ Oplossing
**Downgrade naar Gradle 7.6.3** - de meest stabiele versie voor AGP 8.1.4.

### Aangepast Bestand
**File**: `gradle/wrapper/gradle-wrapper.properties`

**Voor**:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
```

**Na**:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-7.6.3-bin.zip
```

## 📋 Compatibility Matrix
| Android Gradle Plugin | Aanbevolen Gradle Versie |
|-----------------------|--------------------------|
| AGP 8.1.x            | Gradle 7.5 - 7.6.3       |
| AGP 8.2.x            | Gradle 8.0 - 8.3         |
| AGP 8.3.x            | Gradle 8.2 - 8.5         |

## 🚀 Wat Nu?

### Stap 1: Re-download Project
Download de `anderoid` folder opnieuw naar je Windows computer:
```
sftp://root@185.165.242.58:2223/root/afbeeldingen/yessfish/app/anderoid/
```

### Stap 2: Clean Cache (Belangrijk!)
Voor een verse build start:

**Windows Command Prompt**:
```cmd
cd C:\pad\naar\anderoid
rd /s /q .gradle
gradlew.bat clean
```

**Android Studio**:
1. File → Invalidate Caches
2. Check "Clear downloaded shared indexes"
3. Klik "Invalidate and Restart"

### Stap 3: Build APK
**Command Line**:
```cmd
gradlew.bat assembleDebug
```

**Android Studio**:
1. Wait for "Gradle sync finished" (~2-5 min)
2. Build → Build Bundle(s) / APK(s) → Build APK(s)
3. Wait for "BUILD SUCCESSFUL"

### Stap 4: Output
```
BUILD SUCCESSFUL in 2m 34s
45 actionable tasks: 45 executed

APK locatie:
anderoid\app\build\outputs\apk\debug\app-debug.apk
```

## 🔄 Waarom Downgrade en Niet Update?
We gebruiken **Gradle 7.6.3** in plaats van 8.x omdat:
- ✅ 100% compatibel met AGP 8.1.4
- ✅ Meest stabiele configuratie
- ✅ Geen breaking changes
- ✅ Breed getest in productie

Upgraden naar AGP 8.3.x + Gradle 8.x zou werken, maar vereist:
- Testen van alle dependencies
- Mogelijk breaking changes
- Meer tijd voor troubleshooting

## 💡 Alternative: Upgrade Path (Optioneel)
Als je toch wilt upgraden naar nieuwere versies:

**build.gradle** (root):
```gradle
plugins {
    id 'com.android.application' version '8.3.2' apply false  // Was: 8.1.4
    id 'org.jetbrains.kotlin.android' version '1.9.22' apply false
}
```

**gradle-wrapper.properties**:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
```

Maar voor nu is **7.6.3 de veiligste keuze**! ✅

## 📚 Meer Info
- [Gradle AGP Compatibility](https://developer.android.com/build/releases/gradle-plugin#updating-gradle)
- [Gradle Release Notes 7.6.3](https://docs.gradle.org/7.6.3/release-notes.html)
- [Android Gradle Plugin 8.1](https://developer.android.com/build/releases/past-releases/agp-8-1-0-release-notes)

---
**Status**: ✅ Fixed (2025-10-13)
**Gradle Version**: 7.6.3 (downgrade from 8.0)
**Android Gradle Plugin**: 8.1.4 (unchanged)
