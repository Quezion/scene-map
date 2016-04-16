(ns threact.diff
  "Provides functions to compare two clojure datastructures and return the
difference between them. Alterations will return the elements that were added, removed, or updated."
  (:require [clojure.set :as set]))

(declare alterations-inner)

(defn map-alterations
  "Given two maps, diffs elements in new-state that do not exist in state.
  Returns a map. See alterations for description of map keys."
  [state new-state]
  ; Iterate over keys in new-state. Bind each key to k and store the rest in a sequence.
  ; Define a transient that will act as the return
  (loop [[k & k-rest] (clojure.set/union (keys state) (keys new-state))
         additions    (transient {})
         removals     (transient {})
         updates      (transient {})]

    ; If k is nil, all keys have been iterated over. Return results.
    (if-not k
      (vector (persistent! additions) (persistent! removals) (persistent! updates))

      ; Otherwise compare the old-val and new-val of the current keyword
      ; old-value and new-value are setup the way they are so they compare the entire branch of the map incl sub-colls
      (let [old-value (get state k ::none)
            new-value (get new-state k ::none)]

        (cond
          ; If both values are colls, recursively diff them and propogate results into corresponding transients
          (and (coll? old-value) (coll? new-value))
          (let [new-alterations (alterations-inner old-value new-value)
                new-additions (if-not (empty? (nth new-alterations 0)) (assoc! additions k (nth new-alterations 0)) additions)
                new-removals  (if-not (empty? (nth new-alterations 1)) (assoc! removals  k (nth new-alterations 1)) removals)
                new-updates   (if-not (empty? (nth new-alterations 2)) (assoc! updates   k (nth new-alterations 2)) updates)]
            (recur k-rest new-additions new-removals new-updates))

          ; Handle case where new value = old value (do nothing except recur)
          (= new-value old-value)
          (recur k-rest additions removals updates)

          ; Handle case where key is not present in old value (add to "additions")
          (= old-value ::none)
          (recur k-rest (assoc! additions k new-value) removals updates)

          ; Handle case where key is not present in new value (add to "removals")
          (= new-value ::none)
          (recur k-rest additions (assoc! removals k old-value) updates)

          ; This is implicitly the last possible choice, but we reserve the default for the :else just in case
          (not= new-value old-value)
          (recur k-rest additions removals (assoc! updates k new-value))

          :else
          (do (print "new value = " new-value ", old value = " old-value)
              (throw (js/Error. "Unrecognized condition met in map alterations diff"))))
        ))))

(defn vec-alterations
  "Given two vectors, diffs elements in new-state against old-state. If any elements differ, all elements are added to
  the 'update' key in the return map. See alterations for description of return map keys."
  [state new-state]
  (if (= state new-state)
    [[] [] []]
    [[] [] new-state]))

(defn alterations-inner
  "Performs same functionality as alterations-new except that it returns a vector of three elements.
  The first element contains the additions of the new state, the second contains the removals,
  and the third element contains the updates. This is done for speed."
  [state new-state]
  (cond
    ; map alterations
    (and (map? state) (map? new-state))
    (map-alterations state new-state)

    ; seq alterations
    (and (sequential? state) (sequential? new-state))
    (if (vector? new-state)
      ; vector alterations
      (vec-alterations state new-state)
      ; Non-vector alterations
      (let [vec-result (vec-alterations state new-state)]
        (vector (into (list) (reverse (nth vec-result 0)))
                (into (list) (reverse (nth vec-result 1)))
                (into (list) (reverse (nth vec-result 2))))))

    ; set alterations
    ; TODO: implement this so it returns properly
    (and (set? state) (set? new-state))
    (throw (js/Error. (str "scene-map alterations does not support diff of sets")))
    ;(set/difference new-state state)

    ; Likely to be a base data type (number, bool, etc). Return no updates if they're equal.
    (= state new-state)
    (list [] [] [])

    ; if this point is reached, it means that new state is completely different than old state
    ; it's either an addition or update; either way it will be considered an update due to inability to compare it
    :else
    (list [] [] [new-state])))

(defn alterations
  "Finds elements that vary between state and new-state. Returns a map with following keys:
  :additions - contains elements that exist in new-state but not in state.
  :removals  - contains elements that were in state but are not in new-state.
  :updates   - contains elements that exist in both states but where value has changed. Will never contain map keys that only exist in new-state.
               Contains comparisons of map values. If any part of a non-associative collection is altered, the entire collection is returned."
  [state new-state]
  (let [vec-return (alterations-inner state new-state)]
    {:additions (nth vec-return 0)
     :removals  (nth vec-return 1)
     :updates   (nth vec-return 2)}))
