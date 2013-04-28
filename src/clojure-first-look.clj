(defn two
  ([] [])
  ([x & more]
    (lazy-cat [(* x 2)] (apply two more))
    ))

(two)
(two 1 2 3)

(defn multi [x [y z]] 
  (map (partial * x) [y z])
  )

(multi 0 [1 2])
(multi 3 [1 2])
