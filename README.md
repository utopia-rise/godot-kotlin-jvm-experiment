# godot-kotlin-jvm

to get embedding working one has to:  

1. have java >= `9` installed and setup
2. create jre:  
    - navigate to root of sample project (`harness/simple` or `harness/test`) 
    - run `jlink --add-modules java.base,jdk.jdwp.agent,java.logging --output jre` for debug and `jlink --add-modules java.base,java.logging --output jre` for release

3. disable other targets (only on windows and macOS) in `jni-kt/build.gradle.kts` and in `godot-glue/build.gradle.kts`

# Setup Protobuf

1. Download the java protobuf release zip [releases](https://github.com/protocolbuffers/protobuf/releases/tag/v3.13.0).  
2. navigate to the extracted protobuf folder
3. run `./configure --disable-shared CXXFLAGS="-fPIC"`
4. run `make`
5. run `sudo make install`
6. run `sudo ldconfig`

# building and using
After setup of the embedded jvm and installed protobuff one has to:
1. Import the root project using intellij
2. Import the `godot-loader2` using clion
3. Build `godot-loader2` using cmake (make sure the output folder is called `build`! clion per default builds into `cmake-build-debug` which does not work in this poc!)
4. Build the root project using gradle `build` command
5. Build the sample project of choice using the `build` gradle command inside the corresponding project
6. Open the sample project of choice in godot through command line (you can either open the editor or run the game directly. The play scene or play game button inside the editor DO NOT WORK with this PoC!)

# Protobuf

https://github.com/protocolbuffers/protobuf/tree/master/src (install the java version, which already includes cpp)

`./configure --disable-shared CXXFLAGS="-fPIC"`