(ns scene-map.mesh
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro, non-updating diamond macro
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.material :refer [matmap-to-three]]
    [scene-map.geometry :refer [geomap-to-three]]
    ))

(defn three-mesh
  "Given key values representing a mesh, returns a corresponding THREE mesh.
  The type argument is a key that must match a value in material-types."
  [three-geometry three-material]
  (THREE.Mesh. three-geometry three-material))

(defn ^:private meshmap-to-three
  "Given a mesh map (stored under a model's :meshes key), instantiates corresponding THREE mesh."
  [{:keys [texture material geometry] :as meshmap}]
  (let [three-material (matmap-to-three material)
        ; TODO: texture invocation should be changed so that it's set on the material after creation if non-nil
        ;three-texture  (texmap-to-three texture)
        three-geometry (geomap-to-three geometry)
        ]
    (three-mesh three-geometry three-material)))