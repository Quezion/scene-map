(ns scene-map.elements.collections
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro, non-updating diamond macro
  (:require
    [THREE]  ; WebGL rendering library
    [util.coll :refer [seq-to-js map-dorun map-doall]]))

(def keywords
  #{:type
    :data})

(def type-converters
  {:clojure-coll identity
   :js-array     array-seq
   })

(defn realize
  "Given a map representing a coll, realizes it to a JavaScript array and returns it."
  [{:keys [type data] :as coll}]
  (let [to-seq (get type-converters type)]
    (to-seq data)))
