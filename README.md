# Gradle plugin for Evocrash

## EvoCrash github repository: https://github.com/STAMP-project/EvoCrash

## Build the plugin

```./gradlew clean build```

## Try the plugin

```
cd example/
./gradlew evocrash
```

## Issues

It seems that there is a bug in gradle that prevent the execution of evocrash through the gradle plugin.
The issue should be fix in the version 5 of gradle: https://github.com/gradle/gradle/issues/2657.

It is really clear that the issue is the same in the gradle version 2.10. The error exist in version 4.10.2 but the message is different.