;; http://www.4clojure.com/problem/125
;; 设计一个函数, 返回值为这个函数自身的源码.
;
;; 建议在 eclipse 下阅读本文, 这样你可以通过切换代码注释一边阅读一边运行查看测试代码, 就像我写作本文时一样.
;
; 从理论(http://en.wikipedia.org/wiki/Quine_%28computing%29)角度看,
; 实际为要求设计一个满足下列闭环变换的函数.
; fn --invoke-> str --read-string-> list --> eval --> fn
;
; 题目要求的测试用例
; (= (str '__) (__))
; 实际为从上述闭环抽取出两个变换.
; fn --invoke-> str <-----str------ list
;
; 为方便修改、执行测试, 可将 list 绑定到 symbol 避免重复手写.
; 测试用例重写为
; (= (str x) ((eval x)))
; 对应变换
; list --> eval --> fn --invoke-> str <-----str------ list
;
; 判断字符串相等需要精确匹配(多少一个空格都不行), 需要小心翼翼的处理输出格式.
; 不过我们可以先肉眼观察结果，等基本结构已经一致后，再调整输出格式.

; 根据以上分析, 先写出测试代码框架
(let [x
      ; 函数定义     
      ; 定义一个函数，返回结果是一个字符串，最简形式如下
      '(fn [] "")
      ]
  ; print肉眼比较
  (println (str x))
  (println ((eval x)))
  ; 测试
  (println "str:" (= (str x) ((eval x))))
  ; 测试 list 结构是否相等.
  ; 只有函数输出为合法源码时才能执行， 否则 read-string 将抛出异常.
  ;(println "list:" (= x (read-string ((eval x)))))
  )

; 详细解题思路
(let [x
      ; 定义一个函数，返回结果是一个字符串，最简形式如下
;      '(fn [] "")
      ; 返回字符串的内容应该是函数源码自身，copy函数源码填充字符串的内容
;      '(fn [] "(fn [] ")
      ; "(fn [] "是函数定义前缀，接下来写函数体，而函数体就是我们正在定义的字符串自身.
      ; 如果继续copy自己，字符串越来越长，永远copy不完。
      ; 想到应该把前面所有内容定义成变量prefix，这样只要引用这个变量的源码，就可以完成copy自己。
      ; 在python里，使用 "repr(x)"可以得到一个平凡数据结构的源码。
      ; 函数源码前缀定义prefix变量，代码如下
;      '(fn [] (let [prefix "(fn [] (let [prefix "] prefix))
      ; copy到这里，同样的问题又出现了，让 prefix继续copy自己，将无穷无尽没有尽头, 
      ; 所以只能copy到prefix自身定义前为止，
      ; 在函数体里，使用(str)函数将 prefix 自身的源码表示拼接到 prefix后面.
;      '(fn [] (let [prefix "(fn [] (let [prefix "] (str prefix (repr prefix))))
      ; 悲剧的是, clojure不支持(repr), shit!
      ; 现在有两个思路：
      ; (1) clojure 的 str 用在 vector等其他数据结构上时，效果跟 python 的 repr 相似, 
      ; 可以想办法构造其他数据结构，然后把 str 当 repr 用.
      ; (2) 简单情况下，字符串不涉及转义处理，那么 (repr x) 只要在两端加上双引号就可以了.
      ; 为了不影响我们的思路，先定义一个最简单的 (repr)，当做内置函数使用.
      ; (defn repr [x] (str \" x \"))
      ; 继续用 print 大法观察效果.
      ; so good, 前缀已经相等了. 还是简单的想法，把源码后缀再copy串接到 str 后面.
;      '(fn [] (let [prefix "(fn [] (let [prefix "] (str prefix (repr prefix) "] (str prefix (repr prefix ")))
      ; too simple, too naive, 后缀定义再次面临copy自己的窘境.
      ; 根据经验, 把后缀也定义成变量. 代码膨胀, 整理下代码排版.
;      '(fn [] (let [
;                    prefix "(fn [] (let [prefix "
;                    suffix "] (str prefix (repr prefix) suffix)))"
;                    ]
;                (str prefix (repr prefix) suffix)
;                ))
      ; 函数体的拼接源码输出时, 添加 suffix 定义.
;      '(fn [] (let [
;                    prefix "(fn [] (let [prefix "
;                    suffix "] (str prefix (repr prefix) 'suffix (repr suffix) suffix)))"
;                    ]
;                (str prefix (repr prefix) 'suffix (repr suffix) suffix)
;                ))
      ; 比较了一下输出差异, 源码 let binding 的元素之间需要添加空格.
;      '(fn [] (let [
;                    prefix "(fn [] (let [prefix "
;                    suffix "] (str prefix (repr prefix) \" \" 'suffix \" \" (repr suffix) suffix)))"
;                    ]
;                (str prefix (repr prefix) " " 'suffix " " (repr suffix) suffix)
;                ))
      ; 新的问题出现了, suffix 包含特殊字符, 双引号, 
      ; 我们编写的简单 (repr) 不能正确产生 suffix 的源码.
      ; 这时有两个思路
      ; (1) 想办法让 clojure 支持真正的 repr, 借助java三方库比如
      ; org.apache.commons.lang.StringEscapeUtils.escapeJava(String) 也许可以实现.
      ; 可是 4clojure 怎么可能让你随便使用java三方库呢。
      ; (2) 来自 @xumingming 的提示, 可以使用 (char)替换一些字符常量, 避免特殊字符.
      ; 查一下空格符的 codePoint.
      ; (.codePointAt " " 0)
      ; 得 32, 用 (char 32)替换 " ", 修正 suffix.
;      '(fn [] (let [
;                    prefix "(fn [] (let [prefix "
;                    suffix "] (str prefix (repr prefix) (char 32) 'suffix (char 32) (repr suffix) suffix)))"
;                    ]
;                (str prefix (repr prefix) (char 32) 'suffix (char 32) (repr suffix) suffix)
;                ))
      ; 最后一个问题, (str x) 的结果中 ```'suffix```变成了 ```(quote suffix)```.
      ; 不用简写, 直接写 (quote suffix) 好了.
;      '(fn [] (let [
;                    prefix "(fn [] (let [prefix "
;                    suffix "] (str prefix (repr prefix) (char 32) (quote suffix) (char 32) (repr suffix) suffix)))"
;                    ]
;                (str prefix (repr prefix) (char 32) (quote suffix) (char 32) (repr suffix) suffix)
;                ))
      ; done! 本机测试通过. 
      ; 可是不要忘了，我们本机私自定义了 (repr)函数, 执行 ```(ns-unmap *ns* 'repr)```清掉自定义的 repr, 立马报错.
      ; 为了能够在 4clojure 上测试通过, 借助 (char 34), 把 (repr x)全部替换成
      ; ```(str (char 34) x (char 34))``` 
;      '(fn [] (let [
;                    prefix "(fn [] (let [prefix "
;                    suffix "] (str prefix (str (char 34) prefix (char 34)) (char 32) (quote suffix) (char 32) (str (char 34) suffix (char 34)) suffix)))"
;                    ]
;                (str prefix (str (char 34) prefix (char 34)) (char 32) (quote suffix) (char 32) (str (char 34) suffix (char 34)) suffix)
;                ))
      ; done! 4clojure测试通过. 可是，是不是又臭又长？
      ; 我一开始犯了一个错误, 尝试用 (str) 来拼接整个输出. 
      ; 如果改用 (format), 整个函数体用一个变量定义就够了.
      ;
      ; 现在我们已经知道了函数的整体结构，用 format 来重写它吧.
      ; 还是从简单的开始，第一版会是这个样子.
;      '(fn [] (let [x ""] (format x)))
      ; copy源码前缀和后缀填充变量 x
;      '(fn [] (let [x "(fn [] (let [x ] (format x)))"] (format x)))
      ; 中间还缺少什么呢？还缺少  x 自己的源码表示.
;      '(fn [] (let [x "(fn [] (let [x %s] (format x (repr x))))"] (format x (repr x))))
      ; 抱歉! (repr x) 要换成 ```(str (char 34) x (char 34))```.
;      '(fn [] (let [x "(fn [] (let [x %s] (format x (str (char 34) x (char 34)))))"] (format x (str (char 34) x (char 34)))))
      ; 从 @xumingming 代码片段的提示, 可以压缩几个字符.
;      '(fn [] (let [x "(fn [] (let [x %c%s%c] (format x (char 34) x (char 34))))"] (format x (char 34) x (char 34))))
      ;
      ; 回顾上文提到的另一种思路, (str)函数应用到 vector 等数据结构上时, 具有与 repr 一样的效果.
      ; 而 let 语句的 binding 子句刚好是一个 vector, 可以尝试用一个 vector来格式化.
      ; 同样, 先写出函数模板:
;      '(fn [] (let [x ""] (format x [])))
      ; 填充 let binding 对应的 vector, copy函数源码填充格式化字符串.
;      '(fn [] (let [x "(fn [] (let %s (format x ['x x])))"] (format x ['x x])))
      ; 额，'x 简写又带来问题. 究其本质, vector中的 'x 被展开了, 字符串中的 'x 没有展开(当然不可能展开).
      ; 还是两个思路:
      ; (1) 使 'x 始终作为语法元素, 让 clojure 统一处理. 函数体也作为参数, 从字符串中抽取出来.
;      '(fn [] (let [x "(fn [] (let %s %s))" y '(format x ['x x 'y (list 'quote y)] y)] (format x ['x x 'y (list 'quote y)] y)))
      ; (2) 不要使用 'x 简写, 直接用 (quote x). 
      ; 实际上, 只在字符串内部使用 (quote x) 就可以了, 函数体用 'x 会被 clojure 替换，从而跟字符串内的表示一致.
      ; 为了代码观感一致, 我们把所有位置一并替换了.
;      '(fn [] (let [x "(fn [] (let %s (format x [(quote x) x])))"] (format x [(quote x) x])))
      ;
      ; @khotyn 提供了另外一种标准解法: https://github.com/khotyn/4clojure-answer/blob/master/125-gus-guinundrum.clj。
      ;
      ;
      ; 按下文的思路, 我们还可以先构造表示函数定义的list, 再转换成str, 而不用其他字符串拼接或处理.
      '(fn [] (let [x '(str (list 'fn [] (list 'let ['x (list 'quote x)] x)))] 
                (str (list 'fn [] (list 'let ['x (list 'quote x)] x)))
                ))
      ; 还可以通过引入全局变量消重.
;      '(fn [] (do (def x '(list 'fn [] (list 'do (list 'def 'x (list 'quote x)) '(str (eval x))))) (str (eval x))))
      ; 注: 这样污染了全局空间，现实代码不应该这样做.
      ]
  ; print肉眼比较
  (println (str x))
  (println ((eval x)))
  ; 测试
  (println "str:" (= (str x) ((eval x))))
  ; 测试 list 结构是否相等.
  ; 只有函数输出为合法源码时才能执行， 否则 read-string 将抛出异常.
  ;(println "list:" (= x (read-string ((eval x)))))
  )


; 自己推导出的第一个版本.
(def eval-self
  '(fn []
     (let
       [
        prefix "(fn [] (let "
        suffix "))"
        body '(str prefix ['prefix prefix 'suffix suffix 'body (list 'quote body)] " " body suffix)
        ]
       ; (eval body) ; CompilerException java.lang.RuntimeException: Unable to resolve symbol: prefix in this context, WHY?
       (str prefix ['prefix prefix 'suffix suffix 'body (list 'quote body)] " " body suffix)
       ))
  )


; 假设 clj 内置支持 repr, 不需要这一行.
; 从 @xumingming 得到启发, 可使用 (char 34)消除引号.
; 使用 (str (char 34) x (char 34)) 替换 (repr x), 可消除依赖 (repr).
(defn repr [x] (str \" x \")) 
(let [x "(let [x %s] (print (format x (repr x))))"] (print (format x (repr x))))

; 代码片段: 撤销自定义的 repr.
(ns-unmap *ns* 'repr)


; 既然 clojure 看起来像是在操作语法树，有没有可能实现一个不通过字符串源码转换的闭环。
; 转换规则如下:
; list --> eval --> fn --invoke-> list
; 满足如下测试
; (= x ((eval x)))

; 测试代码框架
(let [x
      ; 函数定义
      ; 定义一个函数, 返回结果是一个 list, 最简单的想法如下
;      '(fn [] '(fn []))
      ; 根据之前的经验, 考虑使用let定义变量
;      '(fn [] (let [x '(fn [] (let [x x] x))] x))
      ; 如果希望能用 x 表示整个函数的 list 表达式, 则 x 必须引用自己. 
      ; 格式化字符串可以用 %s 做占位符, 再用自身替换占位符.
      ; list则不可能实现预留占位符和用自身去替换.
      ; 因此考虑到退回用 (concat) 拼接的方案.
      ; 还没想好 x 要表示什么, 而拼 list 时发现需要一个变量表示 let 的 body 部分.
      ; 额，那就用 x 表示 let 的 body 部分吧.
      ; 写完 let-body 前, 我们暂时把 x 的值留空, 后续再替换过去.
;      '(fn [] (let [x '()] (concat '(fn []) [(list 'let ['x x] x)])))
      ; 注意一个问题, 输入 list let x 是绑定到 "(quote ())", 而输出时 "quote" 没了.
      ; 输出时应该也要有 quote, 单不能直接 (quote x), 应该是 x 被求值后，再添加 quote.
      ; 经过思考，使用 (list 'quote x) 构造 quote 表达式, x 也正常求值.
;      '(fn [] (let [x '()] (concat '(fn []) [(list 'let ['x (list 'quote x)] x)])))
      ; let-binding 没问题了, 再把整个 let-body 赋值给 x.
;      '(fn [] (let [x '(concat '(fn []) [(list 'let ['x (list 'quote x)] x)])]
;                (concat '(fn []) [(list 'let ['x (list 'quote x)] x)])
;                ))
      ; 测试了下 concat 竟然是 lazy 的？使用 list 替换 concat，代码似乎更简洁了.
;      '(fn [] '(fn [] BODY))
      ; x 表示 let-body
;      '(fn [] (let [x '()] '(fn [] (let [x x] x)) ))
      ; 为了替换 x, 需要对 let-body 中的 x 求值, let-body 使用 ```(list)``` 替代 ```'()```.
;      '(fn [] (let [x '()] (list 'fn [] (list 'let ['x (list 'quote x)] x)) ))
      ; copy let-body为 x 的值.
      '(fn [] (let [x '(list 'fn [] (list 'let ['x (list 'quote x)] x))] 
                (list 'fn [] (list 'let ['x (list 'quote x)] x)) 
                ))
      ; 测ok. 
      ; let binding 和 let body的 list 表达式是重复的，有没有办法消除重复?
      ; 尝试修改 let body 为直接调用 (eval x)
;      '(fn [] (let [x '(list 'fn [] (list 'let ['x (list 'quote x)] '(eval x)))] 
;                (eval x) 
;                ))
      ; 运行报错 "CompilerException java.lang.RuntimeException: Unable to resolve symbol: x in this context, compiling"
      ; eval 无法访问到局部变量.
      ; 尝试使用 (def)定义全局变量, 改写代码.
;      '(fn [] (do (def x '(list 'fn [] (list 'do (list 'def 'x (list 'quote x)) '(eval x)))) (eval x)))
      ; 测试ok. 
      ; 不过这样运行之后引入的全局变量将污染影响名字空间，现实代码不应该这样做.
      ]
  ; print肉眼比较
  (println x)
  (println ((eval x)))
  ; 测试
  (= x ((eval x)))
  )

(ns-unmap *ns* 'x)