(ns threact.element.root
  (:require [cljs.spec :as s]
            [threact.element.common :as c]))

;; Abstraction that must exist in the scene map
;; specifies host-OS dependent "node" that Threact can attach to

;; Threact only supports JS for now, so we must receive a DOM node
(s/def ::root ::c/dom-node)
