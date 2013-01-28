# Plugin Management System (PMS)

A clojure plugin management system, enabling the user to dynamically load clj and 
jar files and extract functions marked as plugins and add them to a list of 
transformations, which can then be applied to data.

## The project have moved ##
This project have been moved to a new project called "Hookin"

## Usage

This section provides some short examples of the Plugin Management System its usage.

#### Example 1 - Simple usage

    (defn
      test-function
      [data]
      (+ data 10))

    (register-transformation :add-10 test-function)

    (apply-changes :add-10 15)

Defines a function named "test-function", which are then registered in the plugin
management system under the hook :add-10 and then applied to the number 15 for a result of 25.

#### Example 2 - Autoloading functions

##### Code placed in a file called plugin.clj

    (ns test-plugin.core)

    (defn
      #^{:hook :multiply-by-2}
      test-function
      [data]
      (* data 2))

##### In your project

    (load-plugin-file "/path-to-file/plugin.clj")

    (apply-changes :multiply-by-2 10)

Defines a function named "test-function" in a seperate clj file. A key value pair have been added to the functions meta data defining that it is a plugin functions and which hook it should be associated with.  
In the project the file is loaded and the function registeres it self in the plugin management system under the hook :multiply-by-2 and then applied to the number 10 for a result of 20.

#### Example 3 - Changing hook keyword

    (def ^:dynamic *hook-keyword* :plugin)

Sets the internal hook keyword, which are the key to look for in functions when loading trasformations from files, to :plugin

## Acknowledgements

This project is based on the ideas and initial work of [Jacob Emcken](https://github.com/jacobemcken "jacobemcken on GitHub")

## License

Copyright (C) 2012 Claus Petersen

Distributed under the Eclipse Public License, the same as Clojure.