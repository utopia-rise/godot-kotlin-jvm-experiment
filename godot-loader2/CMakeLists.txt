cmake_minimum_required(VERSION 3.16)
project(godot_loader)

add_definitions(-D__ANDROID__)
find_package(JNI REQUIRED)
set(CMAKE_CXX_STANDARD 11)

set(Protobuf_VERSION 3.13.0)
set(Protobuf_USE_STATIC_LIBS TRUE)
find_package(Protobuf REQUIRED)

add_subdirectory(jni_wrapper)

set(SOURCES
        src/bridge.cpp
        src/native_kobject.cpp
        src/native_kfunction.cpp
        src/icall_args.cpp
        src/native_transfer_context.cpp
        src/method_bind_cache.cpp
        src/jni_utils.cpp
        src/native_class_handle.cpp
        src/native_tvalue.cpp
        src/godot.cpp
        src/entry.cpp
        src/native_binding_context.cpp)

protobuf_generate_cpp(PROTO_SRCS PROTO_HDRS ../protos/wire.proto)
add_library(godot_loader SHARED ${SOURCES} ${PROTO_SRCS} ${PROTO_HDRS})
include_directories(godot_loader PRIVATE ${CMAKE_CURRENT_BINARY_DIR})
include_directories(godot_loader PRIVATE ${Protobuf_INCLUDEDIRS})
target_include_directories(godot_loader PRIVATE src)
target_include_directories(godot_loader PRIVATE ../godot_headers)
target_link_libraries(godot_loader jni_wrapper)
target_link_libraries(godot_loader ${Protobuf_LIBRARIES})