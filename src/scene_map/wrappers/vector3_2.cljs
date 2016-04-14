;; Provides functions to create and manipulate THREE Vector2 objects
(ns scene-map.wrappers.vector3-2
  (:require [THREE]))

(defn construct
  "Constructs a Vector2 with the specified float coordinates"
  ([x y] (THREE.Vector2. x y))
  ([coll] (THREE.Vector2. (nth coll 0) (nth coll 1))))
