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

## AsyncExecutionTest
```java
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class AsyncExecutionTest {

    private static final int N = 36;
    private static final long FIBONACCI_N = 14930352L;
    private static final long SLEEP_MS = 500L;

    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private String name;
    private long result;
    private volatile boolean isDone;

    @Before
    public void setUp() {
        result = Long.MIN_VALUE;
        stopwatch.start();
    }

    @After
    public void destroy() {
        System.out.printf("invoked_method=[%s], elapsed_ms=%s\n", name, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        assertEquals(FIBONACCI_N, result);
        stopwatch.reset();
    }

    ...

    private String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}
```

### m_1_join
```java
@Test
public void m_1_join() throws InterruptedException {
    name = getMethodName();

    Thread thread = new Thread(() -> result = FibonacciUtil.fibonacci(N), name);
    thread.start();

    System.out.printf("[%s] waiting for [%s]\n", Thread.currentThread().getName(), thread.getName());
    thread.join();
}
```
```
[main] waiting for [m_1_join]
[m_1_join] is starting...
[m_1_join] is done!
invoked_method=[m_1_join], elapsed_ms=55
```

### m_2_notification
```java
@Test
public void m_2_notification() throws InterruptedException {
    name = getMethodName();

    new Thread(() -> {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] try to occupy monitor lock\n", threadName);
        synchronized (this) {
            System.out.printf("[%s] occupies monitor lock successfully\n", threadName);
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_MS * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = FibonacciUtil.fibonacci(N);
            notifyAll();
        }
        System.out.printf("[%s] release monitor lock\n", threadName);
    }, name).start();

    TimeUnit.MILLISECONDS.sleep(SLEEP_MS);
    String threadName = Thread.currentThread().getName();
    System.out.printf("[%s] try to occupy monitor lock\n", threadName);
    synchronized (this) {
        System.out.printf("[%s] occupies monitor lock successfully\n", threadName);
    }
    System.out.printf("[%s] release monitor lock\n", threadName);
}
```
```
[m_2_notification] try to occupy monitor lock
[m_2_notification] occupies monitor lock successfully
[main] try to occupy monitor lock
[m_2_notification] is starting...
[m_2_notification] is done!
[m_2_notification] release monitor lock
[main] occupies monitor lock successfully
[main] release monitor lock
invoked_method=[m_2_notification], elapsed_ms=1049
```

### m_3_interrupt
```java
@Test
public void m_3_interrupt() {
    name = getMethodName();

    Thread thread = Thread.currentThread();
    new Thread(() -> {
        try {
            TimeUnit.MILLISECONDS.sleep(SLEEP_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = FibonacciUtil.fibonacci(N);
        System.out.printf("[%s] interrupt [%s]\n", Thread.currentThread().getName(), thread.getName());
        thread.interrupt();
    }, name).start();

    String threadName = Thread.currentThread().getName();
    System.out.printf("[%s] try to occupy monitor lock\n", threadName);
    synchronized (this) {
        System.out.printf("[%s] occupies monitor lock successfully\n", threadName);
        try {
            System.out.printf("[%s] release monitor lock and wait, isInterrupted=%s\n", threadName,
                    Thread.currentThread().isInterrupted());
            wait(); // throw InterruptedException
        } catch (InterruptedException e) {
            System.out.printf("[%s] throw InterruptedException, isInterrupted=%s\n", threadName,
                    Thread.currentThread().isInterrupted());
        }
    }
}
```
```
[main] try to occupy monitor lock
[main] occupies monitor lock successfully
[main] release monitor lock and wait, isInterrupted=false
[m_3_interrupt] is starting...
[m_3_interrupt] is done!
[m_3_interrupt] interrupt [main]
[main] throw InterruptedException, isInterrupted=false
invoked_method=[m_3_interrupt], elapsed_ms=553
```

# 知识梳理