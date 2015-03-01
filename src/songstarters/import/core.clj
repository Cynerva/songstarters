(ns songstarters.import.core
  (:require
    [clojure.string :refer [replace-first split join]]
    [clojure.java.io :as io]
    [clojure.java.shell :refer [sh]]
    [me.raynes.fs :as fs]
  )
)

; Imports samples for the server to use.
; Requires ffmpeg, converts to .ogg

; This file is a mess, sorry!

(defn import-file [in root-path]
  (let [
    out (join "." (conj (pop (split
      (replace-first in root-path
        (join ["public/samples/" (fs/base-name root-path)])
      )
      #"\."
    )) "ogg"))
    _ (io/make-parents out)
    result (sh "ffmpeg" "-i" in "-codec:" "libvorbis" "-qscale:a" "3" out)
    _ (if (= (:exit result) 0)
      (println "Imported" in)
      (println "Skipping" in)
    )
  ])
)

(defn import-files [path]
  (let [
    root (io/file path)
    root-path (.getAbsolutePath root)
    files (filter #(.isFile %) (file-seq root))
    _ (doseq [file files]
      (import-file (.getAbsolutePath file) root-path)
    )
  ])
)

(defn sample-paths []
  (let [
    root (clojure.java.io/file "public/samples")
    root-path (.getAbsolutePath root)
    files (filter #(.isFile %1) (file-seq root))
    file-paths (for [file files] (.getAbsolutePath file))
    result (for [path file-paths]
      (subs path (+ (count root-path) 1))
    )
  ] result)
)

(defn update-sample-list []
  (with-open [w (io/writer "public/sampleList")]
    (.write w (pr-str (sample-paths)))
  )
)

(defn -main [path]
  (import-files path)
  (update-sample-list)
  (shutdown-agents)
)
