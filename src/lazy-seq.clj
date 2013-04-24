(defn pow2-seq [x]
  (lazy-cat [x] (pow2-seq (* x 2)))
  )

(let [pow2 (pow2-seq 1)]
  (println (take 10 pow2))
  )

(defn fibo-seq [x y]
  (lazy-cat [x] (fibo-seq y (+ x y)))
  )

(let [fibo (fibo-seq 1 1)]
  (doseq [x [1 5 20 45]] 
    (println (take x fibo))
    ))

(defn prime-seq [prime-coll x]
  (if (not-any? #(= (rem x %) 0) prime-coll)
    (lazy-cat [x] (prime-seq (lazy-cat prime-coll [x]) (+ x 2)))
    (prime-seq prime-coll (+ x 2))
    ))

(let [prime (lazy-cat [1] (prime-seq [2] 3))]
  (println (take 50 prime))
  )