# Gradle plugin for Botsing

## Build the plugin

```./gradlew shadowJar```

## Try the plugin

```
cd example/
./gradlew botsing
```

It will generate a test from Fraction.log and Fraction_libraries.jar in the result directory.

## Configure the plugin

The plugin can be configured by modifying the file example/build.gradle. For more information about parameters, check the [Botsing repository](https://github.com/stamp-project/botsing).

#### Required parameters

 - The `logPath` parameter should contain the path to the log.
 - The `libsPath` parameter should contain the path to the classes.
 - The `targetFrame` parameter should target the frame to reproduce. This number should be between 1 and the number of frames in the stack trace.

#### Optional parameters

 - The `output` parameter enables to choose where the output will be generated.
 - The `searchBudget` parameter enables to specify an additional parameter in format.
 - The `population` parameter

