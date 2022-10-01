# HopLoadingButton

Cool loading effect on Material Button.

[![GitHub license](https://img.shields.io/badge/License-Apache-green.svg)](https://github.com/NieKam/HopLoadingButton/blob/master/LICENSE)

[![](https://jitpack.io/v/NieKam/HopLoadingButton.svg)](https://jitpack.io/#NieKam/HopLoadingButton)

# Gradle Dependency

### Repository

The Gradle dependency is available via JitPack.

The minimum API level supported by this library is API 17.

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
```gradle
dependencies {
  implementation 'com.github.NieKam:HopLoadingButton:X.X.X'
}
```

# Info

| Attribute name    | Type |
| -------------   | ------------- |
| primary_color  |  color  |
| secondary_color  | color  |
| line_width  | dimmension  |
| loading_text | string |
| progress_duration  | integer  |
| disable_on_loading  | boolean  |

All attributes are optional. 

<img src="https://github.com/NieKam/HopLoadingButton/blob/master/promo/promo.gif" width="25%" height="25%">

# Usage

### Sample XML layout

```xml
 <pl.com.hop.components.HopLoadingButton
        android:id="@+id/btn1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="12dp"
        android:text="Hello World"
        app:backgroundTint="@color/md_purple_600"
        app:disable_on_loading="false"
        app:line_width="4dp"
        app:loading_text="Loading"
        app:primary_color="@color/md_green_100"
        app:secondary_color="@color/md_green_100" />
```

```kotlin
  val button =  findViewById<HopLoadingButton>(R.id.button)
  button.isLoading = true
```

## PS
Feel free to send your suggestions.
