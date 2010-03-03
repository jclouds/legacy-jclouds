(defproject org.jclouds/clj-blobstore "0.1-SNAPSHOT"
  :description "clojure binding for jclouds blobstore library"
  :source-path "src/main/clojure"
  :test-path "src/test/clojure"
  :compile-path "target/classes"
  :library-path "target"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]
                 [org.jclouds/jclouds-blobstore "1.0-beta-4"]
                 [org.jclouds/jclouds-log4j "1.0-beta-4"]
                 [log4j/log4j "1.2.14"]]
  :dev-dependencies [[org.clojure/swank-clojure "1.0"]]
  :repositories [["jclouds" "http://jclouds.googlecode.com/svn/repo"]
                 ["jclouds-snapshot" "http://jclouds.rimuhosting.com/maven2/snapshots"]])
