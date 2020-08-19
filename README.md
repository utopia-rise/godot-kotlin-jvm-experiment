# godot-kotlin-jvm

to get embedding working one has to:  

1. create jre:  
    - navigate to root of sample project  
    - run `jlink --add-modules java.base,jdk.jdwp.agent --output jre` for debug and `jlink --add-modules java.base --output jre` for release

2. disable other targets (only windows and macOS) in `jni-kt/build.gradle.kts` and in `godot-glue/build.gradle.kts`


# Protobuf

https://github.com/protocolbuffers/protobuf/tree/master/src (install the java version, which already includes cpp)

`./configure --disable-shared CXXFLAGS="-fPIC"`