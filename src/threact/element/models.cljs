(ns threact.element.models
  (:require [cljs.spec :as s]
            [threact.element.common :as c]))

(s/def ::position ::c/vec3)
(s/def ::rotation ::c/vec3)

(s/def ::model
  (s/keys :req-un [::position ::rotation]))

(s/def ::models (s/every-kv keyword? ::model))
