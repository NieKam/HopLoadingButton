# EdgeProgressBar

[![GitHub license](https://img.shields.io/badge/License-Apache-green.svg)](https://github.com/NieKam/EdgeProgressBar/blob/master/LICENSE)

[![](https://jitpack.io/v/NieKam/EdgeProgressBar.svg)](https://jitpack.io/#NieKam/EdgeProgressBar)

<a href='https://play.google.com/store/apps/details?id=com.niekam.sample&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width="50%" height="50%"></a>

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
  implementation 'com.github.NieKam:EdgeProgressBar:XXX'
}
```
# Info

EdgeProgressBar can work in two modes:
- #### Normal progress bar

This mode is default one. Progress will be drawn around the screen edges. You use following attributes:

| Attribute name    | Type |
| -------------   | ------------- |
| first_color  |  color  |
| second_color  | color  |
| line_width  | dimmension  |
| corner_radius  | float  |
| max  | integer  |
| start_progress  | float  |
| progress_anim_duration  | integer  |


<img src="https://github.com/NieKam/EdgeProgressBar/blob/master/screenshots/screenshot.png" width="25%" height="25%">


`progress_color` is orange
`tint_color` is yellow

- #### Indeterminate

To use this mode you need to add attribute

`indeterminate="true"`

In indeterminate mode you can choose between following effects:

1. ZizZag

<img src="https://github.com/NieKam/EdgeProgressBar/blob/master/screenshots/zigzag.gif" width="25%" height="25%">

2. Snake

<img src="https://github.com/NieKam/EdgeProgressBar/blob/master/screenshots/snake.gif" width="25%" height="25%">

3. Glow

<img src="https://github.com/NieKam/EdgeProgressBar/blob/master/screenshots/glow.gif" width="25%" height="25%">

Indeterminate progress could use following attributes:

| Attribute name    | Type |
| -------------   | ------------- |
| first_color  |  color  |
| second_color  | color  |
| line_width  | dimmension  |
| indeterminate  | boolean  |
| indeterminate_type  | enum  |

View allows you to display line as progress

<img src="https://github.com/NieKam/EdgeProgressBar/blob/master/screenshots/progress.gif" width="25%" height="25%">

# Usage

### XML layout

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
  ...

  <com.niekam.edgeprogressbar.EdgeProgress
      android:id="@+id/edgeProgress"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      />

  </android.support.constraint.ConstraintLayout>
```

## PS
Feel free to send your suggestions. Next step is to make better indeterminate effects.
