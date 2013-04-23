(ns hello-clj)

; 归并排序
(declare merge-coll)
(defn merge-sort [coll]
  (if (< (count coll) 2)
    coll
	  (let 
	    [; 计算 coll 元素个数
	     coll-count (count coll)
	     half-count (quot coll-count 2)
	     ; 将 coll 拆为 [a b] 两段
	     a (take half-count coll)
	     b (drop half-count coll)
	     ]
	    ; 递归排序 [a b]，然后合并结果
	    (merge-coll (merge-sort a) (merge-sort b))
	    )))

; 将两个有序列表合并为一个有序列表
(defn merge-coll [a b] (do (println 'merge-coll a b) (flush)
  (cond
    (empty? a) b
    (empty? b) a
    :else (let
            [a1 (first a)
             b1 (first b)
             ]
            (if (< a1 b1)
              (lazy-cat [a1] (merge-coll (rest a) b))
              (lazy-cat [b1] (merge-coll a (rest b)))
              )))))
