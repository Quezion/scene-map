;; Provides functions to create and manipulate THREE Vector3 objects
(ns scene-map.wrappers.vector3-3
  (:require [THREE]))

(defn construct
  "Constructs a Vector3 with the specified float coordinates"
  ([x y z] (THREE.Vector3. x y z))
  ([coll] (THREE.Vector3. (nth coll 0) (nth coll 1) (nth coll 2))))
