(ns threact.wrappers.object-3d
  (:require-macros [swiss.arrows :refer [-<> -!<>]]) ; diamond threading macros
  (:require
    [THREE] ; WebGL rendering library
    [threact.jset :as jset]
    [threact.wrappers.mesh :refer [meshmap-to-three]]
    [util.coll :refer [mapply map-dorun map-doall]]
    ))

(defprotocol protocol
  "Wrapping THREE.js Object3D properties with convenience functions"
  (position [object position] "The position of the object in the scene (vec3)")
  (rotation [object rotation] "The rotation of the object in the scene (vec3)")
  (visible  [object bool]     "Whether the object should be rendered in the scene (bool)")
  (scale    [object bool]     "The scale of the object in the scene (vec3)"))

(extend-type THREE.Object3D protocol
  (position [object position] (jset/position object position))
  (rotation [object rotation] (jset/rotation object rotation))
  (visible  [object bool]     (jset/visible  object bool))
  (scale    [object scale]    (jset/scale   object scale)))

(def keyword-setters
  "Maps keywords to the appropriate jset function. Useful for THREE constructors."
  {:position #(position %1 %2)
   :rotation #(rotation %1 %2)
   :visible  #(visible  %1 %2)
   :scale    #(scale    %1 %2)})

(def keyword-properties
  "A set of the possible keywords that can be set on an Object3D in the scene-map"
  (into #{} (keys keyword-setters)))

(defn- constructor
  "Constructs a default THREE Object3d and returns it."
  [] (THREE.Object3D.))

(defn- apply!
  "Given a THREE Object3D and keyword/values as variadic args, sets the kvs on the Object3D.
  See keyword-setters for possible properties."
  [three-object3d & kvs]
  (let [props-kv    (partition 2 kvs)
        apply-props (fn [[k v] props]
                      (if-not (contains? keyword-setters k) (throw (js/Error. (str "Invalid keyword-property :" (name k) " specified in scene model."))))
                      (apply (k keyword-setters) (list three-object3d v)))]
    (map-dorun apply-props props-kv)
    three-object3d))

(defn modelmap-to-three
  "Given a model map (stored under a scene's :models key), instantiates a corresponding THREE Object3D."
  [{:keys [meshes] :as modelmap}]
  (let [object-3d      (constructor)
        three-meshmaps (into {} (for [[k v] meshes] [k (meshmap-to-three v)]))
        three-meshes   (vals three-meshmaps)]

    (map-dorun #(.add object-3d %) three-meshes)
    (mapply apply! object-3d (dissoc modelmap :meshes))
    {:three-object object-3d :meshes three-meshmaps}))

(defn valid-update-keywords
  "Given a map containing keyword properties representing an object3d, returns a new map containing only
  the valid keyword properties that can be set on an initialized object3d"
  [model-updates]
  (select-keys model-updates keyword-properties))