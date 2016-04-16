(ns threact.wrappers.renderer
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macros
  (:require
    [THREE] ; WebGL rendering library
    [threact.jset :as jset]
    ))

(def ^:private renderer-size-keyword-handlers
  "Maps special renderer size keywords to functions that retrieve their static value."
  {:screen-width  #(.-innerWidth js/window)
   :screen-height #(.-innerHeight js/window)})

(def renderer-size-keywords
  (into #{} (keys renderer-size-keyword-handlers)))

(defprotocol Renderer
  "Wrapping THREE.js material properties with convenience functions"
  (fill-color-hex [object color] "The fill color of the renderer in hex")
  (size           [object size]  "A two element seq containing the width and height of the rendering window in px.
                                  If being specified in scene-map, you may use keywords from special-sizes.")
  (render         [object three-scene three-camera] "Causes the renderer to render onto the scene. Not for use as a keyword property."))

(extend-type THREE.WebGLRenderer. Renderer
  (fill-color-hex [object color]   (jset/clear-color object color))
  (size           [object size]    (jset/size object size))
  (render         [object three-scene three-camera]
    (.render object three-scene three-camera)
    object))

(def keyword-setters
  "Maps keywords to the appropriate jset function. Useful for THREE constructors."
  {:clear-color-hex #(fill-color-hex %1 %2)
   :size            #(size %1 %2)})

(defn- process-size-keyword
  "Given a keyword width or height, processes keyword into a callback that always returns the width or height."
  [keyword]
  {:pre [(contains? renderer-size-keyword-handlers keyword)]}
  (keyword renderer-size-keyword-handlers))

(defn- process-size
  "Given the size of a renderer, processes out special keywords and replaces them with corresponding contextual values.
  Returns the numerical size as a two element sequence."
  ([width height] (process-size (list width height)))
  ([size] (map #(if (keyword? %)
                 ((process-size-keyword %)) %)                 ; note the double invocation here. this is to process a size callback into a static value
                 size)))

(defn- costruct
  "Constructs a default THREE WebGLRenderer and returns it."
  [size-wh]
  (-<> (THREE.WebGLRenderer.)
       (size <> (process-size size-wh))))

(defn- apply!
  "Given a THREE WebGLRenderer and keyword/values as variadic args, sets the kvs on the renderer.
  See keyword-setters for possible properties."
  [renderer3 & kvs]
  (let [props-kv    (partition 2 kvs)
        apply-props (fn [[k v] props]
                      (if-not (contains? keyword-setters k) (throw (js/Error. (str "Invalid keyword-property :" (name k) " specified in scene renderer."))))
                      (apply (k keyword-setters) (list renderer3 v)))]
    (dorun (cljs.core/map apply-props props-kv))
    renderer3))

(defn rendmap-to-three
  "Given a map representing a renderer, constructrs the corresponding renderer and returns it."
  [rendmap]
  {:pre [(contains? rendmap :size)]}
  (-<> (costruct (:size rendmap))
       (apply! <> (dissoc rendmap :size :auto-resize?))))