cmake_minimum_required(VERSION 3.16)
project(jni_wrapper)

set(CMAKE_CXX_STANDARD 11)

set(SOURCES
        src/types.cpp
        src/env.cpp
        src/jvm.cpp
        src/init_args.cpp
        src/jvm_loader.cpp)

add_library(jni_wrapper STATIC ${SOURCES})

target_compile_options(jni_wrapper PRIVATE -fPIC)

target_include_directories(jni_wrapper
        PUBLIC src/include
        PRIVATE src)

target_include_directories(jni_wrapper PUBLIC ${JNI_INCLUDE_DIRS})
target_link_directories(jni_wrapper PUBLIC $ENV{JAVA_HOME}/lib/server)

INSTALL(TARGETS jni_wrapper
        LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR})