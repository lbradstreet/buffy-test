(ns buffy-test.core
  (:require [clojurewerkz.buffy.core :as buffy]
            [clojurewerkz.buffy.frames :as frames]) )

(def dynamic-string
  (frames/frame-type
    (frames/frame-encoder [value]
                   length (buffy/short-type) (count value)
                   string (buffy/string-type (count value))
                   value)
    (frames/frame-decoder [buffer offset]
                   length (buffy/short-type)
                   string (buffy/string-type (read length buffer offset)))
    second))

(def key-value-pair
  (frames/composite-frame dynamic-string dynamic-string))

(def dynamic-map
  (frames/frame-type
    (frames/frame-encoder [value]
                   length (buffy/short-type) (count value)
                   map    (frames/repeated-frame key-value-pair (count value)) value)
    (frames/frame-decoder [buffer offset]
                   length (buffy/short-type)
                   map    (frames/repeated-frame key-value-pair (read length buffer offset)))
    second))

(let [dynamic-type (buffy/dynamic-buffer dynamic-map)]
  (buffy/compose dynamic-type [[["key1" "value1"] ["key1" "value1"] ["key1" "value1"]]]) ;; Returns a constructred buffer
  (-> dynamic-type
      (buffy/compose [[["key1" "value1"] ["key1" "value1"] ["key1" "value1"]]])
      buffy/decompose) ;; Decomposes it back to the key-value pairs
)

