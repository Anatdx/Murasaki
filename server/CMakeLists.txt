cmake_minimum_required(VERSION 3.22.1)
project("hymofs_jni")

add_library(hymofs_jni SHARED
    src/main/cpp/hymofs_jni.c
)
target_include_directories(hymofs_jni PRIVATE
    src/main/cpp
)
# No C++ for now, C only
set_target_properties(hymofs_jni PROPERTIES
    C_STANDARD 11
)
