;; Provides functions for realization of a geometry map
(ns threact.elements.geometry
  (:require-macros [swiss.arrows :refer [-<> -!<>]])
  (:require
    [threact.elements.collections :as collections]
    [threact.wrappers.geometry3 :as geometry3]
    [threact.wrappers.face3-3 :as face3-3]
    [threact.wrappers.vector3-3 :as vector3-3]
    [util.coll :refer [mapply seq-to-js apply-kv-map]]))

(defn convert-vertices
  "Given a map representing a coll in the scene, returns a JS array of matching THREE vector3 objects"
  [_geometry3 vertices]
  (-<> (collections/realize vertices)
       (partition 3 <>)
       (map vector3-3/construct <>)
       (seq-to-js <>)
       (geometry3/set-vertices _geometry3 <>)))

(defn convert-faces
  "Given a map representing a coll in the scene, returns a JS array of matching THREE vector3 objects"
  [_geometry3 faces]
  (-<> (collections/realize faces)
       (partition 3 <>)
       (map face3-3/construct <>)
       (seq-to-js <>)
       (geometry3/set-faces _geometry3 <>)))

(def applicables
  "Keyword properties that can be set and applied on a geometry3"
  {:vertices convert-vertices
   :faces    convert-faces})

(def apply!
  "Applies one or more kvs to a geometry3. The kvs should be passed as variadic arguments."
  (partial apply-kv-map applicables))

; TODO: handle UV coordinates (not yet supported)
(defn realize
  "Given a map representing a geometry, realizes it into a geometry3"
  [geometry]
  (-<> (geometry3/construct)
       (mapply apply! <> geometry)
       (geometry3/compute-face-normals   <>)
       (geometry3/compute-vertex-normals <>)))
