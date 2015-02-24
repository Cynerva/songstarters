(defproject songstarters "0.0.0"
    :main songstarters.server.core
    :aliases {
      "import" ["run", "-m", "songstarters.server.import"]
    }
    :dependencies [
        [org.clojure/clojure "1.6.0"]
        [org.clojure/clojurescript "0.0-2850"]
        [org.clojure/core.async "0.1.346.0-17112a-alpha"]
        [http-kit "2.1.18"]
        [compojure "1.3.1"]
        [me.raynes/fs "1.4.6"]
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
              :output-dir "public/dist/build"
              :output-to "public/dist/songstarters.js"
              :source-map "public/dist/songstarters.js.map"
            }
        }]
    }
)
