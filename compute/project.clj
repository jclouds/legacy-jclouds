(defproject org.jclouds/clj-compute "0.1-SNAPSHOT"
  :description "clojure binding for jclouds compute library"
  :source-path "src/main/clojure"
  :test-path "src/test/clojure"
  :compile-path "target/classes"
  :library-path "target"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]
                 [org.jclouds/jclouds-compute "1.0-beta-4"]
                 [org.jclouds/jclouds-jsch "1.0-beta-4"]
                 [org.jclouds/jclouds-log4j "1.0-beta-4"]
                 [org.jclouds/jclouds-enterprise "1.0-beta-4"]
                 [log4j/log4j "1.2.14"]
                 [com.jcraft/jsch "0.1.42"]]
  :dev-dependencies [[org.clojure/swank-clojure "1.0"]]
  :repositories [["jclouds" "http://jclouds.googlecode.com/svn/repo"]
                 ["jclouds-snapshot" "http://jclouds.rimuhosting.com/maven2/snapshots"]])
