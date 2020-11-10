# 编码作业

## 代码仓库
[zhongmingwu/java-training-camp](https://github.com/zhongmingwu/java-training-camp)

## FibonacciUtil
```java
public class FibonacciUtil {

    // 0 - 0
    // 1 - 1
    // 2 - 1
    // 3 - 2
    // 4 - 3
    // 5 - 5
    public static long fibonacci(int n) {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] is starting...\n", threadName);
        if (n < 0) {
            throw new IllegalArgumentException("n must greater than or equal to 0");
        }

        int a = 0, b = 1;
        while (n-- > 0) {
            a = (b += a) - a;
        }
        System.out.printf("[%s] is done!\n", threadName);
        return a;
    }
}
```

# 知识梳理