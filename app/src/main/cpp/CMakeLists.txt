cmake_minimum_required(VERSION 3.10.2)
project("graph_editor")
add_library(tools SHARED "")
target_sources(tools PRIVATE tools.cpp planarity.cpp triangulate.cpp)