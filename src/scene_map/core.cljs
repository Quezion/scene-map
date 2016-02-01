(ns scene-map.core
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; wand threading macros
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.jset :as jset]
    [scene-map.jget :as jget]
    [scene-map.texture   :refer [texmap-to-three]]
    [scene-map.material  :refer [matmap-to-three]]
    [scene-map.geometry  :refer [geomap-to-three]]
    [scene-map.mesh      :refer [three-mesh]]
    [scene-map.renderer  :refer [rendmap-to-three three-renderer] :as renderer]
    [scene-map.camera    :refer [three-camera]]
    [scene-map.object-3d :refer [modelmap-to-three object3d-applicator]]
    [differ.diff :as diff]                                  ; utility functions to diff old scene-map against updated ones
    [util.coll :refer [intersection-keys mapply]]
    ))

(defn ^:export init-scene! [{:keys [meshes camera] :as scene}]
  "Given a collection of one or more THREE.js meshes, initiates the scene using a WebGL canvas on the browser.
  This will eventually support a scene map that contains models with positions, lighting sources, camera settings, etc."
  (let [three-scene    (THREE.Scene.)
        renderer       (three-renderer [:screen-width :screen-height] :clear-color-hex 0xd3d3d3)
        rotation-delta [0.000 0.005 0.000] ; rotation speed per frame in radians x,y,z
        rotate (fn [mesh] (jset/rotation mesh (map + (jget/rotation mesh) rotation-delta)))
        ]
    (dorun (map #(.add three-scene %) meshes))
    (.appendChild (.-body js/document) (.-domElement renderer))

    (letfn [(render []
              (dorun (map rotate meshes))
              (.render renderer three-scene camera))
            (loop []
              (.requestAnimationFrame js/window loop)
              (render))]
      (loop))))

; TODO: all arguments need to be processed for key resource-id and replaced with the corresponding resource if found
;      Textures should be initialized into THREE objects so they are shared among all referencing objects

(defn state-map
  "Given the initialized objects and the passed in scene-map, constructs the state-map of the scene."
  [scene-map three-scene three-camera three-renderer three-objectmap]
  {:scene-map        scene-map
   :three-scene      three-scene
   :three-camera     three-camera
   :three-renderer   three-renderer
   :three-objectmap  three-objectmap})

(defn ^:export init-scene-new!
  "Given a scene map containing various possible keys describing a scene,
  initiates a matching scene using WebGL canvas into the browser. Check advanced documentation for details.
  Returns a map of state about the initialized scene, including the passed in scene under the :scene key."
  [{:keys [camera renderer models] :as scene-map}]
  ; Start with the :meshes key
  (let [three-renderer (rendmap-to-three renderer)
        ; TODO: namespace for THREE.Scene functions
        three-scene     (THREE.Scene.)
        three-objectmaps (into {} (for [[k v] models] [k (modelmap-to-three v)]))
        three-objects   (vals three-objectmaps)
        three-camera    (three-camera (:position camera) (:rotation camera) (:fov camera))
        ]
    (.appendChild (.-body js/document) (.-domElement three-renderer))
    (dorun (map #(.add three-scene %) three-objects))

    ; TODO: refactor to use the render method from the renderer protocol
    (.render three-renderer three-scene three-camera)
    (state-map scene-map three-scene three-camera three-renderer three-objectmaps)))

(defn ^:private model-updates-seq
  "Given map where keys are model-IDs and values are instantiated THREE Object3Ds. And a corresponding map where keys are
  model-IDs and values are keyword properties should be set on the THREE Object3Ds. Returns a seq of pairs, the first element
  being a THREE Object and the second being a map of the keyword properties to be set on it. Ignores IDs that don't exist in both maps."
  [three-objectmap model-updates]
  (-<> (intersection-keys three-objectmap model-updates)
       (map #(list (% three-objectmap) (% model-updates)) <>)))

(defn ^:private new-models-seq
  "Given map where keys are model-IDs and values are instantiated THREE Object3Ds. And a corresponding map where keys are
  model-IDs and values are keyword properties should be set on the THREE Object3Ds. Returns a seq of pairs, the first element
  being an ID keyword and the second being a modelmap representing a THREE Object3D. Ignores IDs that exist in both maps."
  []
  )

; TODO: should this simply take a seq of two element seqs, one with the THREE object and the other with the updated properties to be applied?
(defn ^:private update-models
  "Given map where keys are model-IDs and values are instantiated THREE Object3Ds. And a corresponding map where keys are
  model-IDs and values are keyword properties should be set on the THREE Object3Ds, updates the THREE objects."
  [three-objectmap model-updates]
  (let [updates-seq (model-updates-seq three-objectmap model-updates)
        ; TODO: use new-models-seq to handle new objects being added to the scene
        ]
    (dorun (map #(mapply object3d-applicator (first %) (second %)) updates-seq))))

(defn ^:export update-scene!
  "Given the state of an initialized scene map and an updated scene,
  updates the scene's objects and renders it on the DOM."
  [{:keys [three-objectmap three-camera three-scene three-renderer] :as scene-state} new-scene-map]
  (let [diffed-scene-state (diff/updates (:scene-map scene-state) new-scene-map)
        model-updates      (:models diffed-scene-state)]
    ;(print "diffed-scene-state = " diffed-scene-state)
    ;(print "scene-state = " scene-state)
    ;(print "new-scene-map = " new-scene-map)
    ;(print "three-objectmap = " three-objectmap)
    ;(print "model-updates = " model-updates)

    (if (some? model-updates) (update-models three-objectmap model-updates))
    (.render three-renderer three-scene three-camera)

    ; If it exists, handle the :camera key by updating the camera
    (assoc-in scene-state [:scene-map] new-scene-map)))


