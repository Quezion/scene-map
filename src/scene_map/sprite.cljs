(ns scene-map.sprite
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.jset :as jset]
    ))

(defn ^:private sprite
  "Given a THREE sprite material object, constructs a sprite object."
  [sprite-material]
  (-<> (THREE.Sprite. sprite-material)
       (jset/position <> 0 4 0)
       (jset/scale <> 12 12 12)))
