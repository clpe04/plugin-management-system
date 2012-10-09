(ns plugin-manager.core
  (:require [bultitude.core]
            [plugin-manager.classloader]))

(def
  #^{:dynamic true
     :doc
     "A keyword specifying which meta data key to check for plugin information.
  Default value: :hook"}
  *hook-keyword* :hook)

(defonce
  #^{:doc
     "A map used to hold all the loaded transformation functions
  associated to the specified hooks"}
  plugin-functions (atom {}))

(defn register-transformation
  "Registers a transformation function to a given key in the transformations map"
  [hook-name function]
  (swap! plugin-functions assoc hook-name
         (conj (hook-name @plugin-functions) function)))

(defn apply-changes
  "Applies all transformation functions associated with the given hook to the input"
  [hook-name input]
  (reduce #(%2 %1) input (hook-name @plugin-functions)))

(defn- list-plugin-functions
  "Gives a list of all functions, containing a meta
  data entry with the given hook keyword, in a given namespace"
  [ns]
  (filter #(*hook-keyword* (meta %)) (vals (ns-publics ns))))

(defn register-transformations
  "Adds a collection of transformation functions to the plugin manager"
  [functions]
  (doseq [function functions]
    (register-transformation (*hook-keyword* (meta function)) function)))

(defn load-plugin-file
  "Loads the specific clj file, and adds all plugin functions in the
  files namespace to the plugin manager"
  [file]
  (load-file file)
  (register-transformations
   (list-plugin-functions
    (first (#'bultitude.core/namespaces-in-dir file)))))

(defn load-plugin-jar
  "Adds the specific jar file to the classpath and requires the namespaces
  in the jar file and adds the plugin functions to the plugin manager"
  [jar-file]
  (plugin-manager.classloader/add-classpath jar-file)
  (doseq [namespace (#'bultitude.core/namespaces-in-jar jar-file)]
    (require namespace)
    (register-transformations
     (list-plugin-functions namespace))))

(defn load-plugin-files
  "Loads all plugin functions from the clj and jar files from the
  specified directory"
  [dir]
  (doseq [file (filter #'bultitude.core/clj? (file-seq (clojure.java.io/file dir)))]
    (load-plugin-file (.getAbsolutePath file)))
  (doseq [jar-file (filter #'bultitude.core/jar? (file-seq (clojure.java.io/file dir)))]
    (load-plugin-jar (.getAbsolutePath jar-file))))

(defn- get-plugin-filename
  "Tries to find the filename associated with a given function"
  [function]
  (let [file (:file (meta function))]
    (if file
      (. (clojure.java.io/file file) getName))))

(defn loaded-plugin-files
  "Finds all filenames, from which functions have been loaded into the transformation map"
  []
  (sort (filter #(not (nil? %))
                (map get-plugin-filename (flatten (vals @plugin-functions))))))