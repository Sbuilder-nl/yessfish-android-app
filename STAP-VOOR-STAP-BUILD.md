# 🎯 Stap-voor-Stap: YessFish APK Bouwen

## ✅ Voordat We Beginnen

**Check deze dingen:**
- [ ] Android Studio is geïnstalleerd
- [ ] Je hebt de `anderoid` folder op je bureaublad
- [ ] Je bent op je Windows machine

---

## 📋 STAP 1: Open Command Prompt

**Druk op:**
```
Windows toets + R
```

**Type:**
```
cmd
```

**Druk op Enter**

Je ziet nu een zwart scherm (Command Prompt).

---

## 📋 STAP 2: Ga Naar Project Folder

**Type in CMD (en druk Enter):**
```cmd
cd C:\Users\info\Desktop\YESSFISH\anderoid
```

**Check of het werkt:**
```cmd
dir
```

**Je moet zien:**
```
app (folder)
build.gradle
gradlew.bat
settings.gradle
... (andere bestanden)
```

✅ **Als je deze bestanden ziet → Goed!**
❌ **Als je een error ziet → Vertel me welke error**

---

## 📋 STAP 3: Clean Build (Opschonen)

**Type:**
```cmd
gradlew.bat clean
```

**Druk Enter**

**Je ziet:**
```
> Task :clean
BUILD SUCCESSFUL in Xs
```

Dit ruimt oude build bestanden op.

---

## 📋 STAP 4: Build Release APK

**Type:**
```cmd
gradlew.bat assembleRelease
```

**Druk Enter**

**Nu gebeurt dit:**
```
Downloading dependencies... (eerste keer)
> Task :app:preBuild
> Task :app:compileReleaseKotlin
> Task :app:compileReleaseJavaWithJavac
> Task :app:mergeReleaseResources
> Task :app:processReleaseManifest
> Task :app:packageRelease
> Task :app:assembleRelease

BUILD SUCCESSFUL in 2m 45s
68 actionable tasks: 68 executed
```

⏱️ **Dit duurt 2-5 minuten**

---

## ⏸️ TIJDENS DE BUILD

**Wat je ziet:**
- Veel tekst voorbij scrollen
- "Compiling..." berichten
- "Task :app:..." regels
- Mogelijk download berichten (eerste keer)

**Dit is NORMAAL!** ✅

**WACHT tot je ziet:**
```
BUILD SUCCESSFUL in [tijd]
```

---

## ❌ MOGELIJKE ERRORS

### Error 1: "gradlew.bat is not recognized"
**Betekenis:** Je bent niet in de juiste folder
**Fix:**
```cmd
cd C:\Users\info\Desktop\YESSFISH\anderoid
dir
```
Check of je `gradlew.bat` ziet.

### Error 2: "JAVA_HOME is not set"
**Betekenis:** Java niet gevonden
**Fix:**
Open Android Studio eerst, dan probeer opnieuw.

### Error 3: "Execution failed for task..."
**Betekenis:** Build error in code
**Fix:**
Kopieer de HELE error message en stuur naar mij.

---

## ✅ STAP 5: Check of APK Er Is

**Als je "BUILD SUCCESSFUL" ziet, type:**
```cmd
dir app\build\outputs\apk\release
```

**Je moet zien:**
```
app-release.apk    (4.000.000 - 6.000.000 bytes)
```

✅ **Als je app-release.apk ziet → GELUKT!**

---

## 📋 STAP 6: Hernoem de APK

**Type:**
```cmd
cd app\build\outputs\apk\release
ren app-release.apk yessfish-app-v1.0.0.apk
```

**Check:**
```cmd
dir
```

**Je moet nu zien:**
```
yessfish-app-v1.0.0.apk
```

---

## 📋 STAP 7: Open de Folder in Windows Explorer

**Type:**
```cmd
explorer .
```

**Druk Enter**

Windows Explorer opent en je ziet:
```
yessfish-app-v1.0.0.apk (4-6 MB)
```

✅ **Dit is je APK! Je kan hem nu uploaden!**

---

## 📤 VOLGENDE STAP: Upload Naar Server

**Heb je WinSCP of FileZilla geïnstalleerd?**
- JA → Ik help je uploaden
- NEE → We kunnen SCP gebruiken of je installeert WinSCP

---

## 🎯 Quick Reference

### Alle Commando's Achter Elkaar:
```cmd
cd C:\Users\info\Desktop\YESSFISH\anderoid
gradlew.bat clean
gradlew.bat assembleRelease
cd app\build\outputs\apk\release
ren app-release.apk yessfish-app-v1.0.0.apk
explorer .
```

---

## 📞 Hulp Nodig?

**Vertel me:**
1. Bij welke stap je bent
2. Wat je ziet op je scherm
3. Als er een error is: kopieer de HELE error

Ik help je verder! 🚀
