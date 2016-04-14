;; Provides functions to create and manipulate THREE Face3 objects

(ns scene-map.wrappers.face3-3
  (:require [THREE]))

(defn construct
  "Constructs a Face3 with the specified integral face indices"
  ([a b c] (THREE.Face3. a b c))
  ([coll] (THREE.Face3. (nth coll 0) (nth coll 1) (nth coll 2))))