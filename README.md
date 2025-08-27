# mount-configuration

A flexible and lightweight configuration data management library for the Clojure
[mount](https://github.com/tolitius/mount) ecosystem. 

It supports consistent loading and a unified view of the common sources of such data:
- environment variables 
- resources folder
- file

A variety of runtime arguments provide a high degree of flexibility and accessor functions
for each source of configuration data through a single namespace.

Additionally the `missinterpret.mount-configuration.file` namespace has primitive editing 
functions with file save capabilities and the `missinterpret.mount-configuration.env` namespace 
supports safe auto-edn parsing.

## Using missinterpret.mount-configuration 

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

    [io.github.missinterpret/mount-configuration "0.1.3"]

[Deps](https://clojure.org/guides/deps_and_cli) dependency information:

    io.github.missinterpret/mount-configuration {:mvn/version "0.1.3"}

## Usage 

To use the configuration component in a project add this to the namespace 
which loads earliest:

```clojure
[missinterpret.mount-configuration.core :refer [config]]
```

## missinterpret.mount-configuration.core

The [missinterpret.mount-configuration.core](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/core.clj#L1) namespace provides a unified view of 
all those sources that is consistent across runtimes. Accessor functions to access that view as well as 
each source's runtime data independently via a single interface is also provided.

### Unified View and Loading Order

The unified data view provides consistency between runtimes by imposing a loading order 
on the sources and merging loaded data in that order to create the unified view. 

Order:
```
edn -> resource -> file
```

### Bootstrapping 

The [start](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/core.clj#L39) functions will attempt to bootstrap off of the loaded data if 
no runtime argument is provided.

- The [resource](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/resource.clj#L1) 
namespace checks the [environment variables](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/env.clj#L1) as a source 
for its data.  
- The [file](https://github.com/MissInterpret/mount-configuration/blob/ddfbf1c05da0b2883bee90a18d4492cfba5c56f1/src/missinterpret/mount_configuration/file.clj#L1) namespace first checks the env and then the resource namespace 


## missinterpret.mount-configuration.env 

The source of environment configuration data.

### Auto edn parsing 

Variables are parsed into edn if possible. If they can't be parsed they are converted to strings.

### Runtime arguments

The namespace's start function takes the following `mount/start` runtime variables as
keys under the `:mount-configuration.env` namespace:

- `vars` - A set of the names of the variables as a keyword: `#{:PATH}`
- `skip-missing` - Skip any variables in the set which aren't available at runtime instead of throwing an exception.
- `throw-parse-failed` -Throw an exception if the edn parsing fails.

## missinterpret.mount-configuration.resource

A file in the resources folder as the source of configuration data. It does not by default
attempt to load a resource. 

### Runtime arguments

The namespace's start function takes the following `mount/start` runtime variables as
keys under the `:mount-configuration.resource` namespace:

- `path` - The path in the resources folder
- `throw-if-missing` - Throw an exception if there is no `path` argument at runtime. 


#### missinterpret.mount-configuration.file

A file on the file system as a source of configuration data.  It does not by default
attempt to load a file unless a path is provided. 

The namespace provides basic editing operations and an accessor to edited state. 
If any of the editing functions are used changes will be saved on `stop` by default.

### Runtime arguments

The namespace's start function takes the following `mount/start` runtime variables as
keys under the `:mount-configuration.file` namespace:

- `path` - the file path 
- `throw-if-missing` - throw an exception if the file cant be loaded or parsed to edn
- `dont-bootstrap` - Skip bootstrapping and only use `mount/args` to load
- `dont-save-on-stop` - If editing has happened, don't save on stop


# Developer Information

Lackadaisically maintained, please submit feedback via GitHub
[issues](https://github.com/MissInterpret/mount-configuration/issues).

# Copyright and License

[Creative Commons BY 4.0 Deed](https://creativecommons.org/licenses/by/4.0/)



