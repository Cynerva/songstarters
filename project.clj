(defproject songstarters "0.0.0"
  :aliases {
    "import" ["run", "-m", "songstarters.import.core"]
  }
  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [org.clojure/clojurescript "0.0-2850"]
    [org.clojure/core.async "0.1.346.0-17112a-alpha"]
    [me.raynes/fs "1.4.6"]
    [cljs-http "0.1.26"]
    [reagent "0.5.0-alpha3"]
  ]
  :plugins [
    [lein-cljsbuild "1.0.4"]
  ]
  :cljsbuild {
    :builds {
      :debug {
        :source-paths ["src"]
        :compiler {
          :output-dir "public/dist/build"
          :output-to "public/dist/songstarters.js"
          :source-map "public/dist/songstarters.js.map"
        }
      }
      :release {
        :source-paths ["src"]
        :compiler {
          :output-to "public/dist/songstarters.js"
          :optimizations :advanced
          :externs ["conf/webaudio-externs.js"]
        }
      }
    }
  }
)
