# iOS Glass UI for Android 🍎✨

A reusable **iOS-style glassmorphism layout** for Android.
Designed as a clean **Android UI Library** with XML-only usage,
supporting **real blur (Android 12+)**, **Light/Dark mode**, and rounded glass effects.

🔗 GitHub: https://github.com/dinidumaleezha/IosGlassAndroid  
📦 JitPack: https://jitpack.io/#dinidumaleezha/IosGlassAndroid

---

## ✨ Features

- iOS-like **glass blur** effect
- **XML-only** usage (no Java/Kotlin code required)
- **Real blur** using `RenderEffect` (Android 12+)
- Fallback glass look for Android 11 and below
- **Light / Dark mode** auto support
- Rounded corners with proper clipping
- Gradient + gloss overlay (premium iOS look)
- Reusable as an **Android Library (AAR)**

---

## 📦 Installation (via JitPack)

### 1️⃣ Add JitPack repository

**settings.gradle**
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}
```

---

### 2️⃣ Add dependency

**app/build.gradle**
```gradle
dependencies {
    implementation "com.github.dinidumaleezha:IosGlassAndroid:v1.0.0"
}
```

---

## 🚀 Usage (XML Only)

```xml
<com.dinidu.glassui.IosGlassLayout
    android:layout_width="match_parent"
    android:layout_height="180dp"
    android:padding="18dp"
    app:glassBlurRadius="32"
    app:glassVibrancy="true"
    app:glassAutoUpdate="true">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="iOS Glass Card"
        android:textSize="22sp"
        android:textStyle="bold"
        android:textColor="@color/glass_text"/>

</com.profit.glassui.IosGlassLayout>
```

---

## ⚙ XML Attributes

| Attribute | Description |
|---------|-------------|
| glassBlurRadius | Blur radius |
| glassVibrancy | Enable vibrancy |
| glassSaturation | Color saturation |
| glassBrightnessLift | Brightness boost |
| glassAutoUpdate | Auto refresh |

---

## 🌗 Light / Dark Mode

- Uses `values/` and `values-night/`
- Automatically adapts to system theme

---

## 📄 License

MIT License

Copyright (c) 2026 Dinidu Maleezha
