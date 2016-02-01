# scene-map
CLJS Library that renders 3D scenes from Clojure maps using THREE.js

**Warning:** This is an early alpha version of the library. It is not recommended for usage, the API will change, and it's currently a proof of concept. Due to dependencies, you will not yet be able to get this project running in your browser, but this will change in the coming weeks.

# Premise

Instead of writing the step by step code to create a WebGL rendering in the browser, scene-map allows you to declare what your scene looks like using a Clojure map and takes care of the rest.

To update the scene each frame, you can simply update the scene-map and pass it back in to the library. Adding, removing, and updating 3D objects is handled automatically by the library.

This library aims to be declarative over imperative, even at the expense of performance. This way, you describe "what the scene looks like" instead of "how to render it."

An example use case would be a web application that generates a scene-map of its state every frame and leaves the rendering to the library.

# Usage

A scene-map is a Clojure hash-map in a specific format that is recognized by the library. It is composed of vanilla Clojure data-structures. Here's a simple example:

```
{
 :camera {
          :position [0 0 7]
          :rotation [0 0 0]
          :fov      90}
 :renderer {
            :size [:screen-width :screen-height]
            :clear-color 0xd3d3d3}
 :models
 {
  :cube
  {
   :scale     [1 1 1]
   :position  [0 0 0]
   :rotation  [0 0 0]
   :meshes
    {
     :body
     {
      :geometry   {:vertices {:type :clojure-coll
                              :data cube-vertices}
                   :faces    {:type :clojure-coll
                              :data cube-faces}}
      :material   {:type      :basic
                   :color-rgb [0 255 0]
                   :wireframe true
                   }}
     }
   }
  }
 }
```

This describes a simple green cube in the scene. Note that cube-vertices and cube-faces are vectors containing the vertices/indices corresponding to a 3D cube.

You can pass this scene-map to the library with the following call:

```
(init-scene! simple-scene)
```

init-scene! returns a map containing the initialized state of the 3D scene, or the **scene-state**. Whenever you want to update the scene, you can alter the scene-map and pass it back in via **update-scene**:

```
(let [old-scene (:scene-map scene-state)
      new-scene (update-in [:models :cube :rotation] #(+ 0.005 %))]
      (update-scene! scene-state new-scene))
```

The WebGL scene would then be re-rendered with the updated cube. How many times to update per second is up to you, but it's recommended to update on the [window.requestAnimationFrame](https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame) of the browser. 

# Examples

See "simple_scene_example.html"

# Project roadmap

This alpha is in early alpha right now. You may initialize and update scenes, but adding or removing objects from the scene post initialization is not supported. The only operations that are supported are changing the properties of the models (position, rotation, scale, and visibility.)

The API will change, and much of the internal code will be polished and refactored for simplicity.

Support for changing the camera and scene parts of the map are next to be updated. Following that, support for textures on objects and sharing resources (vertices/indices/textures) on multiple objects in the scene.

A few weeks down the line you should see support for adding/removing models from the scene dynamically. Support for user-interface on the scene is an eventual plan, but it likely months away. 
