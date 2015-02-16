(defproject songstarterj "0.0.0"
    :main server.main
    :dependencies [
        [org.clojure/clojure "1.6.0"]
        [org.clojure/clojurescript "0.0-2850"]
        [org.clojure/core.async "0.1.346.0-17112a-alpha"]
        [http-kit "2.1.18"]
        [compojure "1.3.1"]
        [cljs-http "0.1.26"]
        [reagent "0.5.0-alpha3"]
    ]
    :plugins [
        [lein-cljsbuild "1.0.4"]
    ]
    :cljsbuild {
        :builds [{
            :source-paths ["src"]
            :compiler {
                :output-to "public/dist/songstarterj.js"
            }
        }]
    }
)
