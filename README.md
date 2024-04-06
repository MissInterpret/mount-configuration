# mount-configuration

A flexible and lightweight configuration data management library for the Clojure
[mount](https://github.com/tolitius/mount) ecosystem. 

It supports consistent loading and a unified view of the common sources of such data:
- environment variables 
- resources folder
- file

It is restricted to one source of each type but provides a variety of options which provides a 
high degree of flexibility.

Additionally the `missinterpret.mount-configuration.file` namespace has primitive editing 
functions with file save capabilities and the `missinterpret.mount-configuration.env` namespace 
supports safe auto-edn parsing.

## missinterpret.mount-configuration.core

The [missinterpret.mount-configuration.core](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/core.clj#L1) namespace provides a unified view of 
all those sources in a consistent way,

### Unified View and Loading Order

The unified data view provides consistency between runtimes by imposing a loading order 
on the sources. 

```
edn -> resource -> file
```

### Bootstrapping 

The [start](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/core.clj#L39) namespace bootstraps 
by having each source in the chain using the updated context. 

- The [resource](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/resource.clj#L1) 
namespace checks the [environment variables](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/env.clj#L1) as a source 
for its data.  
- The [file](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/file.clj#L1) namespace first checks the env and then the resource namespace 


## missinterpret.mount-configuration.env 

The source of environment configuration data.

### Auto edn parsing 

Variables are parsed into edn if possible. If they can't be parsed they are converted to strings.

### Runtime arguments

The namespace's start function takes the following `mount/start` runtime variables:

#### :mount-configuration.env/vars

A set of the names of the variables as a keyword: `#{:PATH}`

#### :mount-configuration.env/skip-missing

Skip any variables in the set which aren't available at runtime instead of throwing an exception.

#### :mount-configuration.env/throw-parse-failed

Throw an exception if the edn parsing fails. 


## missinterpret.mount-configuration.resource

A file in the resources folder as the source of configuration data. It does not, by default
attempt to load a resource but will load one by using the `path` runtime argument. 

### Runtime arguments

The namespace's start function takes the following `mount/start` runtime variables:

### :mount-configuration.resource/path

The path in the resources directory. 

### :mount-configuration.resource/throw-if-missing

Throw an exception if there is no `path` argument is passed at runtime. 


## missinterpret.mount-configuration.file



The value returned by the [config](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/core.clj#L67) function
is created at start by merging source data into an atom as they are loaded. 
