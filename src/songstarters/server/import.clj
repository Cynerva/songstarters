(ns songstarters.server.import
  (:require
    [clojure.string :refer [replace-first split join]]
    [clojure.java.io :as io]
    [clojure.java.shell :refer [sh]]
  )
)

; Imports samples for the server to use.
; Requires ffmpeg, converts to .ogg

(defn import-file [in root-path]
  (let [
    out (join "." (conj (pop (split
      (replace-first in root-path "public/samples")
      #"\."
    )) "ogg"))
    _ (io/make-parents out)
    result (sh "ffmpeg" "-i" in "-codec:" "libvorbis" "-qscale:a" "3" out)
    _ (if (= (:exit result) 0)
      (println "Imported" in)
      (println "Skipping" in)
    )
  ])
  ;(println (:out (sh "echo" in out)))
)

(defn -main [path]
  (let [
    root (io/file path)
    root-path (.getAbsolutePath root)
    files (filter #(.isFile %) (file-seq root))
    _ (doseq [file files]
      (import-file (.getAbsolutePath file) root-path)
    )
  ])
  (shutdown-agents)
)
