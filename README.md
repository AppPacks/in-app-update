# in-app-update
A library for update into own application.

## nstallation
Add this line to the dependency of app

```gradle
repositories {
    maven {
        url  "https://mmoradi.bintray.com/AndroidPack" 
    }
}
```
## Usage
Add this code where that you whant to display upload dialog.

```java
new CheckNewVersion(context).apply();
```

## License
[MIT](https://choosealicense.com/licenses/mit/)
