(ns scene-map.camera
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro, non-updating diamond macro
  (:require
    [THREE]  ; WebGL rendering library
  ))

(defn set-camera-position
  "Sets a THREE.js camera's position given a seq of three elements x/y/z. Returns the camera object."
  [camera [x y z :as position]]
  (-<> (.-position camera)
       (-!<> .-x (set! <> x))
       (-!<> .-y (set! <> y))
       (-!<> .-z (set! <> z))) camera)

(defn set-camera-rotation
  "Sets a THREE.js camera's rotation given a seq of three elements x/y/z in radians. Returns the camera object."
  [camera [x y z :as rotation]]
  (-<> (.-rotation camera)
       (-!<> .-x (set! <> x))
       (-!<> .-y (set! <> y))
       (-!<> .-z (set! <> z))) camera)

(defn three-camera
  "Creates a THREE.js camera from a seq of x,y,z for position and rotation (in radians) and the FOV, creates a THREE.js camera"
  [position rotation fov]
  (-<> (THREE.PerspectiveCamera. fov (/ (.-innerWidth js/window)
                                        (.-innerHeight js/window)) 1 10000)
       (set-camera-position <> position)
       (set-camera-rotation <> rotation)))

(defn camera-from-meshes
  "Given a collection of THREE.js meshes, creates a camera that is guaranteed to view it (uses bounding sphere)."
  [meshes]
  (let [calculate-bounding-sphere #(.computeBoundingSphere (.-geometry %))
        bounding-sphere-radius    #(.-radius (.-boundingSphere (.-geometry %)))
        ___                        (doall (map calculate-bounding-sphere meshes))
        radius                     (-<> (map bounding-sphere-radius meshes)
                                        (apply max <>))]
    (three-camera (list 0 (* radius 0.7) (* radius 2.0)) '(0 0 0) 75)))
