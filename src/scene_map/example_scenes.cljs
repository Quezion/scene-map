(ns scene-map.example-scenes
  (:require-macros [swiss.arrows :refer [-<>]])
  (:require
    [scene-map.core :as core]))

(defn- rotate-object
  "Given a scene-map with a model that has the ID :cube, rotates it on the y axis by passed rotation (in radians)"
  [modelmap rotation]
  (update-in modelmap [:rotation 1] #(+ % rotation)))

(defn- random-color-rgb-flat
  "Returns a list of three RGB integers with values randomly 0 or 1"
  [] (list (rand-int 2) (rand-int 2) (rand-int 2)))

(def cube-vertices
  [-1 -1 -1  ; face vertices
   -1  1 -1
   1  1 -1
   1 -1 -1
   -1 -1  1
   -1  1  1
   1  1  1
   1 -1  1
   -1 -1 -1  ; edge vertices
   -1  1 -1
   1  1 -1
   1 -1 -1
   -1 -1  1
   -1  1  1
   1  1  1
   1 -1  1])

(def cube-faces
  [0 1 2       ; front face
   0 2 3
   4 6 5       ; back face
   4 7 6
   4 5 1       ; left face
   4 1 0
   3 2 6       ; right face
   3 6 7
   1 5 6       ; top face
   1 6 2
   4 0 3       ; bottom face
   4 3 7
   0 1 2 3 0   ; front edges
   4 5 6 7 4   ; back edges
   4 5 1 0 4   ; left edges
   3 2 6 7 3   ; right edges
   1 5 6 2 1   ; top edges
   4 0 3 7 4 ])

(defn- cube-modelmap
  [position rotation color scale wireframe]
  {
   :scale     scale
   :position  position
   :rotation  rotation
   :meshes
              {
               :body
               {
                :geometry   {:vertices {:type :clojure-coll
                                        :data cube-vertices}
                             :faces    {:type :clojure-coll
                                        :data cube-faces}
                             }
                :material   {:type      :basic
                             :color-rgb color
                             :wireframe wireframe
                             }}
               }
   }
  )

(def simple-scene
  {
   :camera {
            :position [0 0 7]
            :rotation [0 0 0]
            :fov      90}
   :renderer {
              :size [:screen-width :screen-height]
              ;:auto-resize true
              :clear-color 0xd3d3d3}
   :models
   {
    :cube1 (cube-modelmap [0 2.5 0] [0 2.0 0] [255 0 0] [1 0.7 4] true)
    :cube2 (cube-modelmap [0 0 0] [0 1.5 0] [0 255 0] [1 0.7 4] true)
    :cube3 (cube-modelmap [0 -2.5 0] [0 1.0 0] [0 0 255] [1 0.7 4] true)
    :cube4 (cube-modelmap [-7 0 0] [0 0 0] [255 0 255] [1 5 1] true)
    :cube5 (cube-modelmap [7 0 0]  [0 0 0] [255 0 255] [1 5 1] true)
    :cube6 (cube-modelmap [0 -5.2 0] [0 0 0] [255 255 255] [8 0.2 1] false)
    :cube7 (cube-modelmap [0 -1.125 0] [0 0 0]  [255 255 0] [0.05 5.95 0.05] false)
    :cube8 (cube-modelmap [0 0 0] [0 0 0]  [255 255 0] [6 0.05 0.05] false)
    }
   })

(defn simple-scene-test
  "Renders the simple scene and rotates three of the cubes."
  []
  (let [scene-state   (core/init-scene! simple-scene)
        scene-state-atom (atom scene-state) ]
    (letfn [(render []
              (let [old-state (deref scene-state-atom)
                    old-scene (:scene-map old-state)
                    new-scene (-<> (update-in old-scene [:models :cube1] #(rotate-object % 0.007))
                                   (update-in <>        [:models :cube2] #(rotate-object % -0.009))
                                   (update-in <>        [:models :cube3] #(rotate-object % 0.008))
                                   (update-in <>        [:models :cube6 :meshes :body :material :color-rgb] random-color-rgb-flat))
                    new-state (core/update-scene! old-state new-scene)]
                (reset! scene-state-atom new-state)))
            (loop []
              (.requestAnimationFrame js/window loop)
              (render))]
      (loop))))


(def medium-scene
  {
   ; NOTE: every model map in the :models coll must have a unique ID as its key
   ;       if this ID is changed then scene-map assumes that it has been removed from the scene
   ;       and the generated THREE objects for that key will be deleted
   :models
   {
    :mario
    {
     :position   [1 0 0]
     :rotation   [1.7 0 0]
     :meshes
                 {
                  :body
                  {
                   :geometry   {:vertices {:type :clojure-coll
                                           :data "This is a flat list of vec3 coordinates in the model."}
                                :faces    {:type :clojure-coll
                                           :data "This is a flat list of vec3 triangle faces (indices into the vertices and uvs.)"}
                                :uv-faces {:type :clojure-coll
                                           :data "This is a flat list of vec2 uv coordinates."}}
                   :material   {:type      :basic
                                :color-rgb [255 0 0]
                                :texture    {:width  2048
                                             :height 2048
                                             :format :RGB
                                             :data   "This should be an RGB-UINT8 buffer"}}
                   }
                  }
     }
    :luigi
    {
     :position   [1 0 0]
     :rotation   [1.7 0 0]
     :meshes
                 {
                  :body
                  {
                   :geometry   {:vertices {:resource-id :luigi-vertices}
                                :faces    {:type :clojure-coll
                                           :data "This is a flat list of vec3 triangle faces (indices into the vertices and uvs.)"}
                                :uv-faces {:type :clojure-coll
                                           :data "This is a flat list of vec2 uv coordinates."}}
                   :material   {:type      :basic
                                :color-rgb [255 0 0]
                                :texture    {:resource-id :luigi-texture}}
                   }
                  }
     }
    }
   :camera
   {:position [0 0 0]
    :rotation [0 0 0]
    :fov      75}
   :renderer
   {
    :size [:screen-width :screen-height]
    :clear-color 0xd3d3d3
    }
   :resources
   {
    :luigi-texture {:width  2048
                    :height 2048
                    :format :RGB
                    :data   "This should be an RGB-UINT8 buffer"}
    :luigi-vertices {:type :clojure-coll
                     :data "This is a flat list of vec3 coordinates in the model."}
    }
   })
