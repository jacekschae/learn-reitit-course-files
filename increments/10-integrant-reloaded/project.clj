(defproject cheffy "0.1.0-SNAPSHOT"
  :description "Cheffy Backend"
  :url "http://api.learnreitit.com"
  :min-lein-version "2.0.0"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.8.1"]
                 [integrant "0.8.0"]
                 [environ "1.2.0"]
                 [metosin/reitit "0.5.2"]
                 [metosin/ring-http-response "0.9.1"]
                 [seancorfield/next.jdbc "1.0.462"]
                 [org.postgresql/postgresql "42.2.14"]
                 [clj-http "3.10.0"]
                 [ovotech/ring-jwt "1.3.0"]]
  :source-paths ["src"]
  :resource-paths ["resources"]
  :test-paths ["test"]
  :profiles
  {:dev     {:source-paths   ["dev/src"]
             :resource-paths ["dev/resources"]
             :dependencies   [[ring/ring-mock "0.4.0"]
                              [integrant/repl "0.3.1"]]}
   :uberjar {:aot :all}}
  :repl-options {:init-ns cheffy.server}
  :uberjar-name "cheffy.jar")

