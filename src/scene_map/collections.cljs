; Provides keywords that allow you to set the type of collections in the scene-map.
; You may lay them out as clojurescript collections or as JavaScript array buffers
; Note that the collection is assumed to be flat (no sub-colls or buffers)
(ns scene-map.collections)

(defn partition-clojure-coll
  [coll n]
  (partition n coll))

; ! IMPORTANT NOTE: not sure if this actually works. needs to be tested.
(defn partition-js-array-buffer
  [coll n]
  (array-seq coll n))

; Maps the type of the collection to a handler function
; this function takes the coll and a number of elements N. It returns a seq where each element is a list of N elements from the collection.
(def type-handlers
  {:clojure-coll              partition-clojure-coll
   :partition-js-array-buffer partition-js-array-buffer})

(def types
  (into #{} (keys type-handlers)))