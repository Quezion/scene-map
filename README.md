# Threact
CLJS Library to render WebGL scenes from hash-maps à la [React.](https://github.com/facebook/react) 

**Warning:** This is library is pre-alpha. It's not recommended for usage and the API will change. Due to dependencies on THREE.js, it's difficult to get this running in your own project. A lein template will be available closer to release.

## Premise

3D rendering involves a lot of mutable state. If you're using Clojure, this is a painful situation to deal with.

Functional design says "push imperative code to the edges of your application." That's where Threact comes in.

You're responsible for outputting a structured hash-map to represent the visual state of your application. You keep passing that to Threact and it keeps your WebGL canvas up to date.

## Usage

A scene-map is a Clojure hash-map in a format recognized by the library. It's composed of vanilla Clojure data-structures. Here's an example:

```
{:camera {
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
                   :wireframe true}}}}}}
```

This describes a simple green cube in the scene. Note that cube-vertices and cube-faces are vectors containing the vertices/indices corresponding to a 3D cube.

You can pass this scene-map to the library with the following call:

```
(init-reduce! simple-scene)
```

init-scene! returns a map containing the initialized state of the 3D scene, or the **scene-state**. Whenever you want to update the scene, you can alter the scene-map and pass it back in via **update-scene**:

```
(let [old-scene (:scene-map scene-state)
      new-scene (update-in [:models :cube :rotation] #(+ 0.005 %))]
      (reduce! scene-state new-scene))
```

The WebGL scene would then be re-rendered with the updated cube. How many times to update per second is up to you, but it's recommended to update on the [window.requestAnimationFrame](https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame) of the browser. 

Update: You can now use core/init-reductions! and pass it a callback that will return your current scene-map. Threact handles updating the canvas on the browser's requestAnimationframe.

```
(init-reductions! #(deref scene-map))
```

## Examples

See **simple-scene** in [src/threact/examples/simple.cljs](src/threact/simple.cljs)

## Roadmap

This alpha is in alpha right now. You may initialize and update scenes, but adding or removing objects from the scene post initialization is not supported. Updating properties is supported on several types of objects.

The API will change, and much of the internal code is being refactored. Features are being  
