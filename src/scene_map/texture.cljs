(ns scene-map.texture
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macro
  (:require
    [THREE]  ; WebGL rendering library
    [scene-map.jset :as jset]  ; convenient JS object setters
    [util.coll :refer [contains-every?]]
    ))

(def ^:private texture-formats-three-map
  "A map of texture formats that may be passed to make-three-texture.
  The keys map to the corresponding THREE.js format constants."
  {:RGB (.-RGBFormat js/THREE)
   ; The below are supported by THREE.js but I have been unable to make them work, so they are disabled.
   ;:DXT1  (.-RGB_S3TC_DXT1_Format js/THREE)
   ;:DXT1A (.-RGBA_S3TC_DXT1_Format js/THREE)
   ;:DXT3  (.-RGBA_S3TC_DXT3_Format js/THREE)
   ;:DXT5  (.-RGBA_S3TC_DXT5_Format js/THREE)
  })

(def ^:private texture-formats
  "The possible formats of a texture's data, such as RGB."
  (into #{} (keys texture-formats-three-map)))

(defprotocol ^:export properties-protocol
  "Defines the keywords that map to property functions on the texture."
  (width  [object width] "Whether the material should render as a wireframe")
  (height [object height] "The color that the material should render as in hex")
  (data   [object array-buffer] "The ArrayBuffer or TypedArray containing the image data in the specified format")
  (format [object type] "The format of the texture data. Must be a key from texture-formats."))

(def ^:export texture-types
  "A set of texture types that denotes the purpose of a texture."
  #{:color
    :specular
    :bump
    :normal
    :unknown})

(defn- three-texture
  "Given an array buffer containing texture data, the width and height of the texture, and its format
  Returns a THREE texture that can be used on THREE models."
  [width height format data]
  (if-not (contains? texture-formats-three-map format) (throw (js/Error. "Texture format is unsupported by Scene Map")))
  (-<> (THREE.DataTexture. data width height (get texture-formats-three-map format) (.-UnsignedByteType js/THREE))
       (jset/needs-update <> true)))

(defn- texmap-to-three
  "Given a map representing a texture, creates a matching three-texture and returns it"
  [{:keys [width height format data] :as texmap}]
  {:pre [(contains-every? texmap :width :height :format :data)]}
  (three-texture width height format data))
