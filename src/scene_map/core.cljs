(ns scene-map.core
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; wand threading macros
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.util.handlers :as handlers]
    [scene-map.wrappers.texture   :refer [texmap-to-three]]
    [scene-map.wrappers.material  :refer [matmap-to-three]]
    [scene-map.wrappers.renderer  :refer [rendmap-to-three] :as renderer]
    [scene-map.wrappers.camera    :refer [three-camera]]
    [scene-map.wrappers.object-3d :as    object3d]
    [scene-map.canvas-resizer :as canvas-resizer]
    [scene-map.diff      :as    diff] ; utility functions to diff old scene-map against updated ones
    [util.coll :refer [intersection-keys mapply map-dorun map-doall]]
    ))

(defn- assoc-state-map
  "Given the previous state map and objects describing the state of the scene, updates the old map in place"
  [state-map scene-map scene3 camera3 renderer3 three-objects resize-callback]
  (assoc state-map
    :scene-map       scene-map
    :scene3          scene3
    :camera3         camera3
    :renderer3       renderer3
    :three-objects   three-objects
    :resize-callback resize-callback))

; TODO: all arguments need to be processed for key resource-id and replaced with the corresponding resource if found
;       Textures should be initialized into THREE objects so they are shared among all referencing objects
(defn- init-state-map
  "Given the initialized objects and the passed in scene-map, constructs the state-map of the scene."
  [scene-map scene3 camera3 renderer3 three-objects resize-callback]
  (assoc-state-map {} scene-map scene3 camera3 renderer3 three-objects resize-callback))

(defn init-reduce!
  "Given a scene map containing various possible keys describing a scene,
  initiates a matching scene using WebGL canvas into the browser. Check advanced documentation for details.
  Returns a map of state about the initialized scene, including the passed in scene under the :scene key."
  [{:keys [camera renderer models] :as scene-map}]
  ; Start with the :meshes key
  (let [three-renderer   (rendmap-to-three renderer)
        ; TODO: namespace for THREE.Scene functions
        three-scene      (THREE.Scene.)
        three-objectmaps (into {} (for [[k v] models] [k (object3d/modelmap-to-three v)]))
        three-objects3ds (handlers/three-object3ds-from-objectmaps three-objectmaps)
        three-camera     (three-camera (:position camera) (:rotation camera) (:fov camera))
        resize-callback  (canvas-resizer/process-autoresize! [0 0] (:size renderer) false (:auto-resize? renderer) nil three-renderer three-camera)
        ]
    (.appendChild (.-body js/document) (.-domElement three-renderer))
    (map-dorun #(.add three-scene %) three-objects3ds)

    (renderer/render three-renderer three-scene three-camera)
    (init-state-map scene-map three-scene three-camera three-renderer three-objectmaps resize-callback)))

(defn reduce!
  "Given the state of an initialized scene map and an updated scene,
  updates the scene's objects and renders it on the DOM."
  [new-scene-map {:keys [scene-map three-objects camera3 scene3 renderer3 resize-callback] :as scene-state}]
  (let [diffed-scene    (diff/alterations scene-map new-scene-map)
        updates         (:updates diffed-scene)
        new-resize-callback (canvas-resizer/process-autoresize! (:size (:renderer scene-map)) (:size (:renderer new-scene-map))
                                                            (:auto-resize? (:renderer scene-map)) (:auto-resize? (:renderer new-scene-map))
                                                            resize-callback renderer3 camera3)]
    ;(add-three three-objectmap (:additions diffed-scene-state));
    ;(remove-three three-objectmap (:removals diffed-scene-state)

    (if (some? updates) (handlers/update-three three-objects updates))
    (renderer/render renderer3 scene3 camera3)

    ; TODO: should actually be taken three-scene, three-renderer, etc objects from the new maps returned by the update/add/remove functions
    (assoc-state-map scene-state new-scene-map scene3 camera3 renderer3 three-objects new-resize-callback)))

(defn reductions!
  "Similar to update-scene, but repeatedly updates the scene using the supplied callback.
   The callback should take the old scene-map and return a new scene-map.
   Binds to the window requestAnimationFrame event and invokes callback directly prior to rendering."
  [scene-state update-callback]
  {:pre [(contains? scene-state :scene-map)]} ; sanity check to ensure user is passing in scene-state and not scene-map
  (let [scene-state-atom (atom scene-state)]
    (letfn [(render []
              (let [old-state (deref scene-state-atom)
                    old-scene (:scene-map old-state)
                    new-scene (update-callback old-scene)
                    new-state (reduce! new-scene old-state)]
                (reset! scene-state-atom new-state)))
            (loop []
              (.requestAnimationFrame js/window loop)
              (render))]
      (loop))))

(defn init-reductions!
  [scene-map update-callback]
  (-<> (init-reduce! scene-map)
       (reductions! <> update-callback)))
