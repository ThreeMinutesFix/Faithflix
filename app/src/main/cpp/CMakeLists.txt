cmake_minimum_required(VERSION 3.10.2)
project("myapp")

# Define version information for your C++ library
set(LIBRARY_VERSION_MAJOR 1)
set(LIBRARY_VERSION_MINOR 0)
set(LIBRARY_VERSION_PATCH 0)

add_library(native-lib SHARED native-lib.cpp)

set_target_properties(native-lib PROPERTIES
    VERSION ${LIBRARY_VERSION_MAJOR}.${LIBRARY_VERSION_MINOR}.${LIBRARY_VERSION_PATCH}
    SOVERSION ${LIBRARY_VERSION_MAJOR}
)

# Find libraries
find_library(log-lib log)

# Link libraries
target_link_libraries(native-lib ${log-lib})
