(ns hello-clj)

; 一趟冒泡, 最大元素移动到最后
(declare bubble-up-do)
(defn bubble-up [coll] 
  (do
    '(println ['bubble-up coll]) ; debug
    ; 打散参数, 使能够按元素个数重载
    (apply bubble-up-do coll)
    ))
(defn bubble-up-do
  ([x] (do '(println ['bubble-up-do :one x]) [x]))
  ([x y & more]
    (do 
      '(println ['bubble-up-do :more x y more])
      ; 交换表头
      (let [[a b] (if (< x y) [x y] [y x])]
        (concat [a] (bubble-up (concat [b] more)))
        ))))

; 冒泡排序
(declare bubble-sort-do)
(defn bubble-sort [coll]
  (do
    '(println ['bubble-sort coll])
    ; 打散参数, 按元素个数重载
    (apply bubble-sort-do coll)
    ))
(defn bubble-sort-do
  ([] [])
  ([x] (do '(println ['bubble-sort-do :one x]) [x]))
  ([x & more]
    (do
      '(println ['bubble-sort-do :more x more])
      (->
        (concat [x] more)
        (bubble-up)
        ; 分离最后一个元素，递归排序前面的元素
        (#(concat
            (bubble-sort (drop-last %))
            [(last %)]
            ))))))

; 单元测试
(let [xsort bubble-sort] (doseq
  [x [
    []
    [1]
    [2 1]
    [3 1 2]
    (take 10 (repeatedly #(rand-int 100)))
    ]]
  (println (xsort x))
  (assert (= (xsort x) (sort x)))
  ))
