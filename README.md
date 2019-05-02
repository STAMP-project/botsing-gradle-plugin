# Gradle plugin for Botsing

## Build the plugin

```./gradlew build```

## Build the plugin for local usage

```./gradlew install```

## Try the plugin

### Example using local jar

```
$ ./gradlew install
$ cd example/localJar/
$ ./gradlew botsing -Plocal --info
```

It will generate a test from Fraction.log and Fraction_libraries.jar. The generated test can be found in the directory `crash-reproduction-tests/`.

### Example using Maven jar

```
$ ./gradlew install
$ cd example/mavenJar/
$ ./gradlew botsing -Plocal --info
```

It will generate a test from SpaceNotFound.log and Scheduling project libraries uploaded on maven. The generated test can be found in the directory `crash-reproduction-tests/`.

## Configure the plugin

The plugin can be configured by modifying the file example/build.gradle. For more information about parameters, check the [Botsing repository](https://github.com/stamp-project/botsing).

#### Required parameters

 - The `logPath` parameter should contain the path to the log.
 - The `targetFrame` parameter should target the frame to reproduce. This number should be between 1 and the number of frames in the stack trace.
 - The `localArtifacts` or `mavenArtifacts` should provide the libraries that will used for the generation (i.e ["/home/user/myPath/myLibrary.jar"]. `localArtifacts` provide a list of path to jar files. `mavenArtifacts` provide a list of maven artifacts(i.e ["com.google.truth:truth:0.27"]). Using both may lead to conflict between libraries. 

#### Optional parameters

 - The `output` parameter enables to choose where the output will be generated.
 - The `botsingVersion` parameter enables to modify the Botsing version used. The default version is `1.0.4`.
 - The `searchBudget` parameter enables to specify an additional parameter in format.
 - The `population` parameter from Botsing


