# mount-configuration

A flexible and lightweight configuration data management library for the Clojure
[mount](https://github.com/tolitius/mount) ecosystem. 

It supports loading of the common sources of such data:
- environment variables 
- resources 
- a file source

## missinterpret.mount-configuration.core

The `missinterpret.mount-configuration.core` namespace provides a unified view of 
all those sources in a consistent way by imposing a loading order. 

