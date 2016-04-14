(ns scene-map.wrappers.geometry3
  (:require-macros [swiss.arrows :refer [-<> -!<>]] ; diamond threading macro, non-updating diamond macro
                   [scene-map.util.wrapper-macros :refer [make-setter make-getter]])
  (:require [THREE]))  ; WebGL rendering library

(defn compute-face-normals
  [geometry3]
  (.computeFaceNormals geometry3)
  geometry3)

(defn compute-vertex-normals
  [geometry3]
  (.computeVertexNormals geometry3)
  geometry3)

(defn set-vertices
  "Sets a JS array of THREE vertices faces on the geometry3"
  [geometry3 js-array]
  (aset geometry3 "vertices" js-array)
  geometry3)

;(comment (generate-getters-setters "vertices" "faces"))
;(def set-vertices (make-setter "vertices"))

(defn set-faces
  "Sets a JS array of THREE faces on the geometry3"
  [geometry3 js-array]
  (aset geometry3 "faces" js-array)
  geometry3)

(defn construct
  "Constructs and returns a THREE geometry. If a JS array containing Vector3 objects is supplied for the
  vertices and faces, they will be set on the Geometry."
  [] (THREE.Geometry.))
