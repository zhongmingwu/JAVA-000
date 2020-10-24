# 周四

## 第1题

### 描述

使用GCLogAnalysis.java自己演练一遍串行/并行/CMS/G1的案例。

### 解答

```
$ sysctl -a |egrep 'machdep.*cpu.*count'
machdep.cpu.core_count: 4
machdep.cpu.thread_count: 8

$ java -version
openjdk version "1.8.0_265"
OpenJDK Runtime Environment (Zulu 8.48.0.53-CA-macosx) (build 1.8.0_265-b11)
OpenJDK 64-Bit Server VM (Zulu 8.48.0.53-CA-macosx) (build 25.265-b11, mixed mode)

$ javac GCLogAnalysis.java
```

#### 并行

##### 4096m --default

###### 命令

```
$ java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc_log/parallel_4096.log GCLogAnalysis
正在执行...
执行结束!共生成对象次数:11547
```

###### 日志

```
OpenJDK 64-Bit Server VM (25.265-b11) for bsd-amd64 JRE (Zulu 8.48.0.53-CA-macosx) (1.8.0_265-b11), built on Jul 28 2020 03:07:54 by "zulu_re" with gcc 4.2.1 (Based on Apple Inc. build 5658) (LLVM build 2336.11.00)
Memory: 4k page, physical 16777216k(1653692k free)

/proc/meminfo:

CommandLine flags: -XX:InitialHeapSize=268435456 -XX:MaxHeapSize=4294967296 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
2020-10-24T15:51:37.787-0800: 0.125: [GC (Allocation Failure) [PSYoungGen: 65536K->10733K(76288K)] 65536K->21164K(251392K), 0.0072218 secs] [Times: user=0.01 sys=0.03, real=0.01 secs]
2020-10-24T15:51:37.805-0800: 0.143: [GC (Allocation Failure) [PSYoungGen: 76116K->10743K(141824K)] 86547K->43177K(316928K), 0.0125974 secs] [Times: user=0.02 sys=0.07, real=0.01 secs]
2020-10-24T15:51:37.853-0800: 0.190: [GC (Allocation Failure) [PSYoungGen: 141815K->10748K(141824K)] 174249K->88850K(316928K), 0.0194465 secs] [Times: user=0.02 sys=0.09, real=0.02 secs]
2020-10-24T15:51:37.889-0800: 0.226: [GC (Allocation Failure) [PSYoungGen: 141820K->10744K(272896K)] 219922K->131910K(448000K), 0.0198885 secs] [Times: user=0.02 sys=0.11, real=0.02 secs]
2020-10-24T15:51:37.909-0800: 0.246: [Full GC (Ergonomics) [PSYoungGen: 10744K->0K(272896K)] [ParOldGen: 121165K->118697K(250368K)] 131910K->118697K(523264K), [Metaspace: 2892K->2892K(1056768K)], 0.0113840 secs] [Times: user=0.07 sys=0.00, real=0.02 secs]
2020-10-24T15:51:37.995-0800: 0.333: [GC (Allocation Failure) [PSYoungGen: 262144K->10748K(272896K)] 380841K->207562K(523264K), 0.0321293 secs] [Times: user=0.04 sys=0.15, real=0.03 secs]
2020-10-24T15:51:38.027-0800: 0.365: [Full GC (Ergonomics) [PSYoungGen: 10748K->0K(272896K)] [ParOldGen: 196813K->183894K(358912K)] 207562K->183894K(631808K), [Metaspace: 2892K->2892K(1056768K)], 0.0195594 secs] [Times: user=0.11 sys=0.01, real=0.02 secs]
2020-10-24T15:51:38.089-0800: 0.427: [GC (Allocation Failure) [PSYoungGen: 262144K->86459K(560640K)] 446038K->270353K(919552K), 0.0318742 secs] [Times: user=0.03 sys=0.15, real=0.04 secs]
2020-10-24T15:51:38.250-0800: 0.588: [GC (Allocation Failure) [PSYoungGen: 552891K->109559K(576000K)] 736785K->370242K(934912K), 0.0630093 secs] [Times: user=0.08 sys=0.32, real=0.06 secs]
2020-10-24T15:51:38.367-0800: 0.704: [GC (Allocation Failure) [PSYoungGen: 575991K->167419K(953344K)] 836674K->458476K(1312256K), 0.0719556 secs] [Times: user=0.08 sys=0.37, real=0.07 secs]
2020-10-24T15:51:38.439-0800: 0.776: [Full GC (Ergonomics) [PSYoungGen: 167419K->0K(953344K)] [ParOldGen: 291057K->307144K(496640K)] 458476K->307144K(1449984K), [Metaspace: 2892K->2892K(1056768K)], 0.0361146 secs] [Times: user=0.18 sys=0.02, real=0.04 secs]
2020-10-24T15:51:38.624-0800: 0.962: [GC (Allocation Failure) [PSYoungGen: 785920K->205574K(995840K)] 1093064K->512719K(1492480K), 0.0742238 secs] [Times: user=0.08 sys=0.39, real=0.07 secs]
Heap
 PSYoungGen      total 995840K, used 623776K [0x000000076ab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 785920K, 53% used [0x000000076ab00000,0x00000007843668c0,0x000000079aa80000)
  from space 209920K, 97% used [0x000000079aa80000,0x00000007a7341ac0,0x00000007a7780000)
  to   space 243712K, 0% used [0x00000007b1200000,0x00000007b1200000,0x00000007c0000000)
 ParOldGen       total 496640K, used 307144K [0x00000006c0000000, 0x00000006de500000, 0x000000076ab00000)
  object space 496640K, 61% used [0x00000006c0000000,0x00000006d2bf21a0,0x00000006de500000)
 Metaspace       used 2899K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 309K, capacity 386K, committed 512K, reserved 1048576K
```

###### 分析

1. Memory: 4k page, physical 16777216k(1653692k free)

   - 内存页大小为4K
   - 物理内存大小为16777216k（16GB）

2. -XX:InitialHeapSize=268435456 -XX:MaxHeapSize=4294967296

   - 初始Heap大小为268435456 Bytes（256MB），默认为物理内存的1/64
   - 最大Heap大小为4294967296 Bytes（4GB），默认为物理内存的/1/4

3. -XX:+UseCompressedClassPointers -XX:+UseCompressedOops

   - klass pointer从8 Bytes压缩到4 Bytes，提升内存利用率

   - UseCompressedOops

     - 普通对象指针压缩

   - UseCompressedClassPointers

     - 类指针压缩

     - 依赖UseCompressedOops

       - > UseCompressedOops must be on for UseCompressedClassPointers to be on.

4. -XX:+UseParallelGC

   - 默认采用并行GC

5. 总共发生了9次Young GC，3次Full GC，下面仅分析第一次Young GC和第一次Full GC

6. Young GC

   - 2020-10-24T15:51:37.787-0800: 0.125
     - GC发生的时间戳

   - GC (Allocation Failure)
     - GC类型为Young GC
     - 触发GC的原因为Allocation Failure
   - PSYoungGen: 65536K->10733K(76288K)
     - Young区容量为76288K（74.5MB）
     - Young区经过Young GC后，从65536K（64MB）降到10733K（10.5MB），回收了83.6%的对象
       - 由于并行GC会导致STW，而且Young = Eden + From = Eden + To，从而可以推算出To区为10.5MB，Eden区为64MB
   - 65536K->21164K(251392K)
     - Heap容量为251392K（245.5MB = InitialHeapSize - To = Young + Old = 256MB - 10.5MB）
       - 从而可以简单推算出Old区为171MB
     - Heap经过Young GC后，从65536K（64MB，其实就是Eden区对象）降到21164K（20.7MB），回收了67.7%的对象
     - 对比Young区的回收情况，有10431K（10.19MB ≈ 21164K - 10733K）晋升到了Old区
   - Times: user=0.01 sys=0.03, real=0.01 secs
     - GC在用户态消耗的CPU时间为0.01秒
     - GC在内核态消耗的CPU时间为0.03秒
     - GC实际消耗的时间为0.01秒（精确值为0.0072218秒）
     - user + sys > real
       - 多核CPU

7. Full GC 

   - 2020-10-24T15:51:37.909-0800: 0.246
     - GC发生的时间戳
   - Full GC (Ergonomics)
     - GC类型为Full GC (Ergonomics)
     - Ergonomics（automatic tuning，相关资料参照下文）
   - PSYoungGen: 10744K->0K(272896K)
     - Young区容量为272896K（266.5MB）
     - Young区经过Full GC后，从10744K（10.5MB）降到0K（0MB），回收了100%的对象
   - ParOldGen: 121165K->118697K(250368K)
     - Old区容量为250368K（244.5MB）
     - Old区经过Full GC后，从121165K（118.3MB）降到118697K（115.9MB），回收了2%的对象
   - 131910K->118697K(523264K)
     - Heap容量为523264K（511MB = Young + Old = 266.5MB + 244.5MB）
     - Heap经过Full GC后，从131910K（128.8MB = 10.5MB + 118.3MB）降到118697K（115.9MB = 0MB + 115.9MB），回收了67.7%的对象
   - Metaspace: 2892K->2892K(1056768K)
     - Meta区容量为1056768K（1032MB）
     - Meta区经过Full GC后，空间没有变化
       - 因为Meta区只占用了2892K，远低于MetaspaceSize的默认值（21807104K ≈ 20.8MB）
   - Times: user=0.07 sys=0.00, real=0.02 secs
     - GC在用户态消耗的CPU时间为0.07秒
     - GC在内核态消耗的CPU时间为0.00秒
     - GC实际消耗的时间为0.02秒（精确值为0.0113840秒）
     - user + sys > real
       - 多核CPU

8. JVM进程退出前的内存布局

   - Young区
     - PSYoungGen      total 995840K, used 623776K
       - Young区容量995840K（972.5MB），使用了623776K（608.2MB，62.7%）
     - eden space 785920K, 53% used
       - Eden区容量785920K（767.5MB），使用了53%（416538K ≈ 406.8MB）
     - from space 209920K, 97% used
       - From区容量为209920K（205MB），使用了97%（203622K ≈ 198.9MB）
       - Young = Eden + From
         - 995840K = 785920K +  209920K
         - 623776K ≈ 785920K × 0.53 + 209920K × 0.97 = 620160K
     - to   space 243712K, 0% used
       - To区容量为243712K（238MB），使用了0%
       - From ≈ To
   - Old区
     - ParOldGen       total 496640K, used 307144K（object space 496640K, 61% used）
       - Old区容量为496640K（485MB），使用了307144K（299.9MB，61.8%）
       - Old区没有再继续细分，所有显示为object space
   - Meta区
     -  Metaspace       used 2899K, capacity 4486K, committed 4864K, reserved 1056768K
       - Meta区已经申请的虚拟地址空间为1056768K（1032MB），操作系统实际分配的物理内存为4864K（4.75MB）
       - 容量为4486K（4.38MB），使用了2899K（2.83MB，64.6%）
     - class space    used 309K, capacity 386K, committed 512K, reserved 1048576K
       - CCS区已经申请的虚拟地址空间为1048576K（1GB），操作系统实际分配的物理内存为512K
       - 容量为386K，使用了309K（80%）

**user sys real**

[What do 'real', 'user' and 'sys' mean in the output of time(1)?](https://stackoverflow.com/questions/556405/what-do-real-user-and-sys-mean-in-the-output-of-time1)

User

> **User** is the amount of CPU time spent in user-mode code (outside the kernel) within the process. This is only actual CPU time used in executing the process. Other processes and time the process spends blocked do not count towards this figure.

Sys

> **Sys** is the amount of CPU time spent in the kernel within the process. This means executing CPU time spent in system calls within the kernel, as opposed to library code, which is still running in user-space. Like 'user', this is only CPU time used by the process. 

Real

> **Real** is wall clock time - time from start to finish of the call. This is all elapsed time including time slices used by other processes and time the process spends blocked (for example if it is waiting for I/O to complete).

**Ergonomics**

[6 The Parallel Collector](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/parallel.html)

> The parallel collector is selected by default on server-class machines. In addition, the parallel collector uses a method of automatic tuning that allows you to specify specific behaviors instead of generation sizes and other low-level tuning details. You can specify maximum garbage collection pause time, throughput, and footprint (heap size).

Maximum Garbage Collection Pause Time

> The maximum pause time goal is specified with the command-line option `-XX:MaxGCPauseMillis=<N>`. This is interpreted as a hint that pause times of `<N>` milliseconds or less are desired; by default, there is no maximum pause time goal. If a pause time goal is specified, the heap size and other parameters related to garbage collection are adjusted in an attempt to keep garbage collection pauses shorter than the specified value. These adjustments may cause the garbage collector to reduce the overall throughput of the application, and the desired pause time goal cannot always be met.

Throughput

> The throughput goal is measured in terms of the time spent doing garbage collection versus the time spent outside of garbage collection (referred to as application time). The goal is specified by the command-line option `-XX:GCTimeRatio=<N>`, which sets the ratio of garbage collection time to application time to `1 / (1 +<N>)`.
>
> For example, `-XX:GCTimeRatio=19` sets a goal of 1/20 or 5% of the total time in garbage collection. The default value is 99, resulting in a goal of 1% of the time in garbage collection.

Footprint

> Maximum heap footprint is specified using the option `-Xmx<N>`. In addition, the collector has an implicit goal of minimizing the size of the heap as long as the other goals are being met.

###### 2048m

###### 1024m

###### 512m

#### 串行

#### CMS

#### G1

## 第2题

### 描述

使用压测工具(wrk或sb)，演练gateway-server-0.0.1-SNAPSHOT.jar 示例。

### 解答

