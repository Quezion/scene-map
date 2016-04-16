(ns threact.elements.collections)

(def keywords
  #{:type
    :data})

(def types
  {:clojure-coll identity
   :js-array     array-seq
   })

(defn realize
  "Given a map representing a coll, realizes it to a seq"
  [{:keys [type data] :as coll}]
  (let [to-seq (get types type)]
    (to-seq data)))
