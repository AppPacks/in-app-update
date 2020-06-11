# in-app-update
[![](https://jitpack.io/v/AppPacks/in-app-update.svg)](https://jitpack.io/#AppPacks/in-app-update)

A library for update into own application.

## Installation
Add this line to the dependency of app

**Step 1.** Add the JitPack repository to your build file 

Add it in your root build.gradle at the end of repositories:
```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
**Step 2.** Add the dependency
```gradle
    dependencies {
            implementation 'com.github.AppPacks:in-app-update:v1.0.0'
    }
```

## Usage
Add this code where that you whant to display upload dialog.

```java
new CheckNewVersion(context).apply();
```

## License
[MIT](https://choosealicense.com/licenses/mit/)
