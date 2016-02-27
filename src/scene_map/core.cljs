(ns scene-map.core
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; wand threading macros
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.texture   :refer [texmap-to-three]]
    [scene-map.material  :refer [matmap-to-three] :as material]
    [scene-map.geometry  :refer [geomap-to-three]]
    [scene-map.renderer  :refer [rendmap-to-three] :as renderer]
    [scene-map.camera    :refer [three-camera]]
    [scene-map.jset      :as    jset]
    [scene-map.jget      :as    jget]
    [scene-map.mesh      :as    mesh]
    [scene-map.object-3d :as    object3d]
    [scene-map.diff      :as    diff] ; utility functions to diff old scene-map against updated ones
    [util.coll :refer [intersection-keys mapply]]
    ))

(defn init-scene-old! [{:keys [meshes camera] :as scene}]
  "Given a collection of one or more THREE.js meshes, initiates the scene using a WebGL canvas on the browser.
  This will eventually support a scene map that contains models with positions, lighting sources, camera settings, etc."
  (let [three-scene    (THREE.Scene.)
        renderer       (rendmap-to-three {:size [:screen-width :screen-height] :clear-color-hex 0xd3d3d3})
        rotation-delta [0.000 0.005 0.000] ; rotation speed per frame in radians x,y,z
        rotate (fn [mesh] (jset/rotation mesh (map + (jget/rotation mesh) rotation-delta)))
        ]
    (dorun (map #(.add three-scene %) meshes))
    (.appendChild (.-body js/document) (.-domElement renderer))

    (letfn [(render []
              (dorun (map rotate meshes))
              (.render (:three renderer) three-scene camera))
            (loop []
              (.requestAnimationFrame js/window loop)
              (render))]
      (loop))))

; TODO: all arguments need to be processed for key resource-id and replaced with the corresponding resource if found
;       Textures should be initialized into THREE objects so they are shared among all referencing objects
(defn state-map
  "Given the initialized objects and the passed in scene-map, constructs the state-map of the scene."
  [scene-map three-scene three-camera renderer-state three-objectmap]
  {:scene-map        scene-map
   :three-scene      three-scene
   :three-camera     three-camera
   :renderer-state   renderer-state
   :three-objectmap  three-objectmap})

(defn three-object3ds-from-objectmaps
  [three-objectmaps]
  (doall (map #(:three-object %) (vals three-objectmaps))))

(defn init-scene!
  "Given a scene map containing various possible keys describing a scene,
  initiates a matching scene using WebGL canvas into the browser. Check advanced documentation for details.
  Returns a map of state about the initialized scene, including the passed in scene under the :scene key."
  [{:keys [camera renderer models] :as scene-map}]
  ; Start with the :meshes key
  (let [renderer-state   (rendmap-to-three renderer)
        ; TODO: namespace for THREE.Scene functions
        three-scene      (THREE.Scene.)
        three-objectmaps (into {} (for [[k v] models] [k (object3d/modelmap-to-three v)]))
        three-objects3ds (three-object3ds-from-objectmaps three-objectmaps)
        three-camera     (three-camera (:position camera) (:rotation camera) (:fov camera))]
    (.appendChild (.-body js/document) (.-domElement (:three renderer-state)))
    (dorun (map #(.add three-scene %) three-objects3ds))

    (renderer/render (:three renderer-state) three-scene three-camera)
    (state-map scene-map three-scene three-camera renderer-state three-objectmaps)))

(def model-updates {:cube3 {:meshes {:body {:material {:color-rgb '(1 15 164)}}}, :rotation [0 2.336000000000001 0]},
                    :cube2 {:rotation [0 -0.002999999999995071 0]},
                    :cube1 {:meshes {:body {:material {:color-rgb '(209 20 206)}}}, :rotation [0 3.1690000000000196 0]}})
(def three-objects {:cube1 {:three-object "JS Object", :meshes {:body "JS Object"}},
                    :cube2 {:three-object "JS Object", :meshes {:body "JS Object"}},
                    :cube3 {:three-object "JS Object", :meshes {:body "JS Object"}},
                    :cube4 {:three-object "JS Object", :meshes {:body "JS Object"}},
                    :cube5 {:three-object "JS Object", :meshes {:body "JS Object"}}})

(defn- update-three-from-meshmap
  [mesh-updates three-mesh]
  ; There are no dynamically updateable properties on a mesh supported right now. Update material.
  (let [three-objects (map #() three-mesh)]) ; use keys to get both three material object and meshmap

  ;(print "mesh-updates = " mesh-updates)
  ;(print "three-mesh-map = " three-mesh)
  ;(print (mesh/get-material three-mesh))

  ; TODO: check there's actually a material key for this mesh?
  (mapply material/applicator (mesh/get-material three-mesh) (material/valid-update-keywords (:material mesh-updates)))
  )

(defn- update-meshes
  [mesh-updates three-mesh-map]
  (-<> (map #(update-three-from-meshmap (% mesh-updates) (% three-mesh-map)) (keys mesh-updates))
       (dorun <>)))

(defn- update-three-from-modelmap
  "Given a single modelmap of updates and the corresponding THREE Object3D, updates all parts of the Object3D"
  [model-updates three-object3d-map]
  (mapply object3d/applicator (:three-object three-object3d-map) (object3d/valid-update-keywords model-updates))
  (update-meshes (:meshes model-updates) (:meshes three-object3d-map)))

(defn- update-models
  "Given the map of initialized three-objects and a diffed-scene, updates the THREE scene to match"
  [three-objectmaps model-updates]
  (-<> (map #(update-three-from-modelmap (% model-updates) (% three-objectmaps)) (keys model-updates))
       (dorun <>)))

(defn- update-three
  "Updates the THREE scene. Takes a map of three-objects and a map of updates to the scene-map"
  [three-objectmaps updates]
  (update-models three-objectmaps (:models updates))
  ;update-camera
  )

(defn- add-three
  "Adds objects to the THREE scene. Takes a map of three-objects and scene-map of objects to be added."
  [three-objectmaps additions]
  )

(defn- remove-three
  "Removes objects from the THREE scene. Takes a map of three-objects and scene-map of objects to be removed."
  [three-objectmaps removals]
  )

(defn update-scene!
  "Given the state of an initialized scene map and an updated scene,
  updates the scene's objects and renders it on the DOM."
  [{:keys [three-objectmap three-camera three-scene renderer-state] :as scene-state} new-scene-map]
  (let [diffed-scene (diff/alterations (:scene-map scene-state) new-scene-map)
        updates      (:updates diffed-scene)

        ]
    
    ;(add-three three-objectmap (:additions diffed-scene-state))
    ;(remove-three three-objectmap (:removals diffed-scene-state)

    (if (some? updates) (update-three three-objectmap updates))
    (renderer/render (:three renderer-state) three-scene three-camera)
    (assoc-in scene-state [:scene-map] new-scene-map)))
