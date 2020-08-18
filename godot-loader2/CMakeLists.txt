cmake_minimum_required(VERSION 3.16)
project(godot_loader)

add_definitions(-D__ANDROID__)
find_package(JNI REQUIRED)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_SKIP_RPATH FALSE)

add_subdirectory(jni_wrapper)

set(SOURCES
        src/bridge.cpp
        src/native_kobject.cpp
        src/jni_utils.cpp
        src/native_class_handle.cpp
        src/godot.cpp
        src/entry.cpp
        src/native_binding_context.cpp)

add_library(godot_loader SHARED ${SOURCES})
target_include_directories(godot_loader PRIVATE src)
target_include_directories(godot_loader PUBLIC ../godot_headers)
target_link_libraries(godot_loader jni_wrapper)