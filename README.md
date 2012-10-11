# Plugin Management System (PMS)

A clojure plugin management system, enabling the user to dynamically load clj and jar files
and extract functions marked as plugins and add them to a list of transformations, which
can then be applied to data.

## Usage

This section provides a short description of how you define a plugin function, the
primary functions in the Plugin Management System and examples of their usage.

### Defining plugin functions

To mark your functions to be loaded by the Plugin Management System, all you have to do is to add
a metadata entry, where the key is the hook keyword you will set when loading the plugins and
the value is the name it will be associated with in the transformation map.

#### Example

    (defn
      #^{:plugin :add-10}
      test-function
      [data]
      (+ data 10))

Defines a function named "test-function", which will be loaded by the plugin
management system, if it gets it hook-keyword set to ":plugin". If so the
function will be associated with the hook ":add-10".

### Functions

#### register-transformation

Takes a hookname and a function and registers the given function to the given hookname in the
transformation map.

#### load-plugin-file

Takes a path to a clj file and loads the functions containing the
defined hook keyword in the given file and registers them on the hooks specified in the metadata.

#### load-plugin-jar

Takes a path to a jar file and scans it for namespaces, it then loads all functions
containing the defined hook keyword in all found namespaces and registers them on the hooks
specified in metadata.

#### load-plugin-files

Takes a path to a directory and scans all clj and jar files in the directory (it doesn't search
in subdirectories). It then loads all functions containing the defined hok keyword and registers
them on the hookname specified in metadata.

#### apply-changes

Takes a hook name and a input object, it then looks up all transformation associated with the
given hok name in the transformation map and applies them to the input object.

#### loaded-plugin-files

Returns a sorted list of all the filenames, from where transformations have been loaded

### Examples

#### Example of registering af transformation

    (register-transformation :add-10 test-function)

Adds the function "test-function" associated with the hook ":add-10" to the transformation map.

#### Examples of loading files

##### Extract from plugin.clj

    (ns test-plugin.core)

    (defn
      #^{:plugin :multiply-by-2}
      test-function
      [data]
      (* data 2))

    (defn test-function2
      [data]
      (* data 4))

##### Extract from plugin2.clj in plugin.jar

    (ns test-plugin2.core)

    (defn
      #^{:plugin :subtract-2}
      test-function2
      [data]
      (- data 2))

##### Load a single clj file

    (load-plugin-file "/home/username/project/plugins/plugin.clj")

Loads the function "test-function" and adds it to the transformation map, associated by
the hook ":multiply-by-2"

##### Load a single jar file

    (load-plugin-jar "/home/username/project/plugins/plugin.jar")

Loads the function "test-function2" and adds it to the transformation map, associated by
the hook ":subtract-2"

##### Load all files in a directory

    (load-plugin-files "/home/username/project/plugins")

Given the directory contains the "plugin.clj" and "plugin.jar", it loads the
functions "test-function" and "test-function2" and adds them to the transformation map,
where "test-function" gets associated with the hook ":multiply-by-2" and "test-function2"
gets associated with the hook ":subtract-2"

#### Example of applying loaded transformation

    (apply-changes :multiply-by-2 10)

In this example the function "test-function" from "plugin.clj" from the example above has been loaded
and associated with the hook ":multiply-by-2". The "apply-changes" call looks up the functions
associated with the hook ":multiply-by-2" and calls the function with "10" as input to the
function. The return value of the call is "20".

#### Example of applying multiple functions

    (defn multiply-by-2
      [data]
      (* data 2))

    (defn multiply-by-10
      [data]
      (* data 10))

    (apply-changes :multiply 1)

It is possible to associate multiple functions to a hook, and all the functions will be
called, in this example the two functions have been associated to the hook ":multiply" and the call
to "apply-changes" then looks up the associated functions and call them in a chain on the input data,
so the result of this call is "20";

#### Example of changing hook keyword

    (def ^:dynamic *hook-keyword* :plugin)

Sets the internal hook keyword to look for when loading trasformations from plugin files, to :plugin

## Acknowledgements

This project is based on the ideas and initial work of [Jacob Emcken](https://github.com/jacobemcken "jacobemcken on GitHub")

## License

Copyright (C) 2012 Claus Petersen

Distributed under the Eclipse Public License, the same as Clojure.