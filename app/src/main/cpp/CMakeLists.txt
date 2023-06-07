cmake_minimum_required(VERSION 3.4.1)

add_library(
    Normalization
    SHARED
    Normalization.cpp)

find_library(
    Normalization
    log)

target_link_libraries(
    Normalization
    ${log_lib})