(ns scene-map.examples.sample-data)

(def cube-vertices
  [-1 -1 -1  ; face vertices
   -1  1 -1
   1  1 -1
   1 -1 -1
   -1 -1  1
   -1  1  1
   1  1  1
   1 -1  1
   -1 -1 -1  ; edge vertices
   -1  1 -1
   1  1 -1
   1 -1 -1
   -1 -1  1
   -1  1  1
   1  1  1
   1 -1  1])

(def cube-faces
  [0 1 2       ; front face
   0 2 3
   4 6 5       ; back face
   4 7 6
   4 5 1       ; left face
   4 1 0
   3 2 6       ; right face
   3 6 7
   1 5 6       ; top face
   1 6 2
   4 0 3       ; bottom face
   4 3 7
   0 1 2 3 0   ; front edges
   4 5 6 7 4   ; back edges
   4 5 1 0 4   ; left edges
   3 2 6 7 3   ; right edges
   1 5 6 2 1   ; top edges
   4 0 3 7 4 ])
