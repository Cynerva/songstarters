(defproject songstarterj "0.0.0"
    :dependencies [
        [org.clojure/clojure "1.6.0"]
        [org.clojure/clojurescript "0.0-2850"]
        [reagent "0.5.0-alpha3"]
    ]
    :plugins [
        [lein-cljsbuild "1.0.4"]
    ]
    :cljsbuild {
        :builds [{
            :source-paths ["src"]
            :compiler {
                :output-to "public/main.js"
            }
        }]
    }
)
