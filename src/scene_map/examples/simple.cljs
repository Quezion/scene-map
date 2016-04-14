(ns scene-map.examples.simple
  (:require [scene-map.examples.sample-data :as data]
            [scene-map.core :as core])
  (:require-macros [swiss.arrows :refer [-<>]]))

(defn- rotate-object
  "Given a scene-map with a model that has the ID :cube, rotates it on the y axis by passed rotation (in radians)"
  [modelmap rotation]
  (update-in modelmap [:rotation 1] #(+ % rotation)))

(defn- random-color-rgb-flat
  "Returns a list of three RGB integers with values randomly 0 or 1"
  [] (list (rand-int 2) (rand-int 2) (rand-int 2)))

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
                                        :data data/cube-vertices}
                             :faces    {:type :clojure-coll
                                        :data data/cube-faces}
                             }
                :material   {:type      :basic
                             :color-rgb color
                             :wireframe wireframe
                             }}
               }
   }
  )

(def scene
  {
   :camera {
            :position [0 0 7]
            :rotation [0 0 0]
            :fov      90}
   :renderer {:size [:screen-width :screen-height]
              :clear-color 0xd3d3d3
              :auto-resize? true}
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

(defn update-callback
  [scene-map]
  (-<> (update-in scene-map [:models :cube1] #(rotate-object % 0.007))
       (update-in <>        [:models :cube2] #(rotate-object % -0.009))
       (update-in <>        [:models :cube3] #(rotate-object % 0.008))
       (update-in <>        [:models :cube6 :meshes :body :material :color-rgb] #(if (= (rand-int 15) 14) (random-color-rgb-flat) %))))

(defn init-scene!
  "Renders the simple scene and rotates some objects."
  []
  (core/init-reductions! scene update-callback))
