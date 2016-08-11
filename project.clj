(defproject songstarters "0.0.2"
  :aliases {
    "import" ["run", "-m", "songstarters.import.core"]
  }
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [org.clojure/clojurescript "1.9.89"]
    [org.clojure/core.async "0.2.385"]
    [me.raynes/fs "1.4.6"]
    [cljs-http "0.1.41"]
    [reagent "0.6.0-rc"]
  ]
  :plugins [
    [lein-cljsbuild "1.1.3"]
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
