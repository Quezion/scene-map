(ns threact.examples.resources
  (:require-macros [swiss.arrows :refer [-<>]])
  (:require [threact.examples.sample-data :as data]
            [threact.core :as core]))

(def resources-scene
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
    :cube1 {
            :position  [0 2.5 0]
            :rotation  [0 2.0 0]
            :scale     [1 0.7 4]
            :meshes
                       {
                        :body
                        {
                         :geometry   :cube-geometry
                         :material   {:type      :basic
                                      :color-rgb [255 0 0]
                                      :wireframe true
                                      }}
                        }
            }
    }
   :resources
   {
    :cube-geometry {:vertices {:type :clojure-coll
                               :data data/cube-vertices}
                    :faces    {:type :clojure-coll
                               :data data/cube-faces}}
    }
   })
