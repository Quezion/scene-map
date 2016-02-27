(ns scene-map.mesh
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro, non-updating diamond macro
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.material :refer [matmap-to-three]]
    [scene-map.geometry :refer [geomap-to-three]]
    ))

(defprotocol Mesh
  "Wrapping THREE.js material properties with convenience functions"
  (material [three-mesh three-material] "Sets the material on the mesh")
  (get-material [object] "Returns the material that is set on the mesh")
  (geometry [three-mesh three-geometry] "Sets the geometry on the mesh")
  (get-geometry [object] "Returns the geometry that is set on the mesh"))

(extend-type THREE.Mesh Mesh
  (material [three-mesh three-material] (aset three-mesh "material" three-material))
  (get-material [three-mesh] (aget three-mesh "material"))
  (geometry [three-mesh three-geometry] (aset three-mesh "geometry" three-geometry))
  (get-geometry [three-mesh] (aget three-mesh "geometry")))

(defn three-mesh
  "Given key values representing a mesh, returns a corresponding THREE mesh."
  [three-geometry three-material]
  (THREE.Mesh. three-geometry three-material))

(defn- meshmap-to-three
  "Given a mesh map (stored under a model's :meshes key), instantiates corresponding THREE mesh."
  [{:keys [material geometry] :as meshmap}]
  {:pre [(some? material) (some? geometry)]}
  (let [three-material (matmap-to-three material)
        three-geometry (geomap-to-three geometry)]
    (three-mesh three-geometry three-material)))

