(ns threact.core
  (:require [cljs.spec :as s]
            [threact.element.common :as c]
            [threact.element.camera :as ca]
            [threact.element.renderer :as r]
            [threact.element.models :as m]
            [threact.element.root :as ro]))

;; TODO - add ::models spec

;;
(s/def ::scene
  (s/keys :req-un [::ca/camera
                   ::r/renderer
                   ::m/models]
          ;:opt [::m/models]
          ))

(s/def ::state
  (s/keys :req [::ro/root]))

;; NOTE: The scene must include a :rootDOMElement variable to specify where the WebGL context
;;       should appear in the scene
(defn init!
  "Initializes a scene into the runtime environment.
   scene - Map representation of a 3D scene in 'scene spec' format
   root  - an attachment point into the environment.
           This is a DOM Node in JS.
   Returns: state - all state created by the library.
                    Used with subsequent render! calls to update the scene."
  [scene root]

  )

(defn render!
  [scene state]
  
  )

