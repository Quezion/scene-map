(ns threact.textures)

(def texture-scene
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
