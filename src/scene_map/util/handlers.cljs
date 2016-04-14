;; Namespace for functions that handle synchronizing the 3D scene with the scene-map every update
(ns scene-map.util.handlers
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; wand threading macros
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.wrappers.texture   :refer [texmap-to-three]]
    [scene-map.wrappers.material  :refer [matmap-to-three] :as material]
    [scene-map.wrappers.camera    :refer [three-camera]]
    [scene-map.wrappers.mesh      :as    mesh]
    [scene-map.wrappers.object-3d :as    object3d]
    [util.coll :refer [intersection-keys mapply map-dorun map-doall]]))

(defn three-object3ds-from-objectmaps
  [three-objectmaps]
  (map-doall #(:three-object %) (vals three-objectmaps)))

(defn id-to-objectmap
  "Given a keyword resource-id that exists in the scene map's :resources, returns the matching resource map.
  Throws an exception if no matching resource was found."
  [id resourcemap]
  {:pre [(contains? resourcemap id)]}
  (get resourcemap id))

; Issue: How to process keywords into objectmaps (for the :resources functionality)
;        without making obtuse code?
; IDEAS: 1) Generate a namespace that contains constructor/applicator functions but modified
;           to take the resource-map as the first argument. Would auto-replace the objectmap
;           with the corresponding resource if it was a keyword.
;     CONS: Namespace generation bunches technically unrelated THREE functions together
;
;        2) Have constructors/applicators take the resourcemap as an optional argument and perform the replacement
;     CONS: Separation of concerns is violated.
;

(defn id-to-objectmap-executor
  [id resourcemap constructor]
  (constructor (id-to-objectmap id resourcemap)))

(defn- add-three
  "Adds objects to the THREE scene. Takes a map of three-objects and scene-map of objects to be added."
  [three-objectmaps additions]
  )

(defn- remove-three
  "Removes objects from the THREE scene. Takes a map of three-objects and scene-map of objects to be removed."
  [three-objectmaps removals]
  )

(defn- update-three-from-meshmap
  [mesh-updates three-mesh]
  ; There are no dynamically updateable properties on a mesh supported right now. Update material and ignores meshes.
  ;(let [three-objects (map #() three-mesh)]) ; use keys to get both three material object and meshmap

  ; TODO: check there's actually a material key for this mesh?
  (mapply material/apply! (mesh/get-material three-mesh) (material/valid-update-keywords (:material mesh-updates))))

(defn- update-meshes
  [mesh-updates three-mesh-map]
  (map-dorun #(update-three-from-meshmap (% mesh-updates) (% three-mesh-map)) (keys mesh-updates)))

(defn- update-three-from-modelmap
  "Given a single modelmap of updates and the corresponding THREE Object3D, updates all parts of the Object3D"
  [model-updates three-object3d-map]
  (mapply object3d/apply! (:three-object three-object3d-map) (object3d/valid-update-keywords model-updates))
  (update-meshes (:meshes model-updates) (:meshes three-object3d-map)))

(defn- update-models
  "Given the map of initialized three-objects and a diffed-scene, updates the THREE scene to match"
  [three-objectmaps model-updates]
  (map-dorun #(update-three-from-modelmap (% model-updates) (% three-objectmaps)) (keys model-updates)))

(defn- update-three
  "Updates the THREE scene. Takes a map of three-objects and a map of updates to the scene-map"
  [three-objectmaps updates]
  (update-models three-objectmaps (:models updates))
  ;update-camera
  )


