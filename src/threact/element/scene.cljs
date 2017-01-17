(ns threact.element.scene
  (:require [cljs.spec :as s]
            [threact.element.common :as c]
            [threact.element.camera :as ca]
            [threact.element.renderer :as r]))

;(spec/def ::models 

(s/def ::scene (s/keys :req-un [::ca/camera ::r/renderer ;::models
                                ]))
