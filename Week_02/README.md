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

##### default

###### 执行

```
$ java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc_log/parallel_default.log GCLogAnalysis
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

5. 总共发生了9次Young GC和3次Full GC，并且没有连续的Full GC，下面仅分析第1次Young GC和第1次Full GC

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
     - 对比Young区的回收情况，有10431K（10.2MB ≈ 21164K - 10733K）晋升到了Old区
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
     - Heap经过Full GC后，从131910K（128.8MB = 10.5MB + 118.3MB）降到118697K（115.9MB = 0MB + 115.9MB），回收了10%的对象
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

**GC Statistics**

![image-20201024212236473](image/image-20201024212236473.png)

**Object Stats**

![image-20201024212348481](image/image-20201024212348481.png)

**GC Causes**

![image-20201024212438269](image/image-20201024212438269.png)

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

##### 256m

###### 执行

```
$ java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc_log/parallel_256.log -Xms256m -Xmx256m GCLogAnalysis
正在执行...
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at GCLogAnalysis.generateGarbage(GCLogAnalysis.java:50)
	at GCLogAnalysis.main(GCLogAnalysis.java:27)
```

###### 日志

```
OpenJDK 64-Bit Server VM (25.265-b11) for bsd-amd64 JRE (Zulu 8.48.0.53-CA-macosx) (1.8.0_265-b11), built on Jul 28 2020 03:07:54 by "zulu_re" with gcc 4.2.1 (Based on Apple Inc. build 5658) (LLVM build 2336.11.00)
Memory: 4k page, physical 16777216k(1641796k free)

/proc/meminfo:

CommandLine flags: -XX:InitialHeapSize=268435456 -XX:MaxHeapSize=268435456 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
2020-10-24T21:06:51.065-0800: 0.134: [GC (Allocation Failure) [PSYoungGen: 65536K->10743K(76288K)] 65536K->23377K(251392K), 0.0085607 secs] [Times: user=0.01 sys=0.04, real=0.01 secs]
2020-10-24T21:06:51.085-0800: 0.155: [GC (Allocation Failure) [PSYoungGen: 76279K->10739K(76288K)] 88913K->47189K(251392K), 0.0129632 secs] [Times: user=0.02 sys=0.07, real=0.01 secs]
2020-10-24T21:06:51.106-0800: 0.176: [GC (Allocation Failure) [PSYoungGen: 76173K->10743K(76288K)] 112624K->72793K(251392K), 0.0104206 secs] [Times: user=0.01 sys=0.04, real=0.01 secs]
2020-10-24T21:06:51.124-0800: 0.194: [GC (Allocation Failure) [PSYoungGen: 76279K->10741K(76288K)] 138329K->92704K(251392K), 0.0088805 secs] [Times: user=0.02 sys=0.04, real=0.01 secs]
2020-10-24T21:06:51.142-0800: 0.212: [GC (Allocation Failure) [PSYoungGen: 76163K->10747K(76288K)] 158125K->115392K(251392K), 0.0102026 secs] [Times: user=0.01 sys=0.05, real=0.01 secs]
2020-10-24T21:06:51.160-0800: 0.229: [GC (Allocation Failure) [PSYoungGen: 76221K->10749K(40448K)] 180865K->140109K(215552K), 0.0098795 secs] [Times: user=0.02 sys=0.04, real=0.01 secs]
2020-10-24T21:06:51.173-0800: 0.243: [GC (Allocation Failure) [PSYoungGen: 40138K->17927K(58368K)] 169499K->151877K(233472K), 0.0021099 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-24T21:06:51.179-0800: 0.249: [GC (Allocation Failure) [PSYoungGen: 47567K->26158K(58368K)] 181517K->163833K(233472K), 0.0029516 secs] [Times: user=0.02 sys=0.01, real=0.01 secs]
2020-10-24T21:06:51.185-0800: 0.255: [GC (Allocation Failure) [PSYoungGen: 55826K->28651K(58368K)] 193502K->174056K(233472K), 0.0043213 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
2020-10-24T21:06:51.190-0800: 0.260: [Full GC (Ergonomics) [PSYoungGen: 28651K->0K(58368K)] [ParOldGen: 145405K->155602K(175104K)] 174056K->155602K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0176453 secs] [Times: user=0.09 sys=0.02, real=0.01 secs]
2020-10-24T21:06:51.212-0800: 0.281: [Full GC (Ergonomics) [PSYoungGen: 29294K->0K(58368K)] [ParOldGen: 155602K->162384K(175104K)] 184896K->162384K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0165632 secs] [Times: user=0.08 sys=0.01, real=0.01 secs]
2020-10-24T21:06:51.234-0800: 0.304: [Full GC (Ergonomics) [PSYoungGen: 29696K->0K(58368K)] [ParOldGen: 162384K->166761K(175104K)] 192080K->166761K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0172991 secs] [Times: user=0.10 sys=0.01, real=0.02 secs]
2020-10-24T21:06:51.256-0800: 0.326: [Full GC (Ergonomics) [PSYoungGen: 29696K->0K(58368K)] [ParOldGen: 166761K->173804K(175104K)] 196457K->173804K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0178451 secs] [Times: user=0.10 sys=0.02, real=0.02 secs]
2020-10-24T21:06:51.279-0800: 0.349: [Full GC (Ergonomics) [PSYoungGen: 29689K->6484K(58368K)] [ParOldGen: 173804K->174924K(175104K)] 203493K->181409K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0192671 secs] [Times: user=0.11 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.303-0800: 0.373: [Full GC (Ergonomics) [PSYoungGen: 29696K->10148K(58368K)] [ParOldGen: 174924K->174464K(175104K)] 204620K->184612K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0189544 secs] [Times: user=0.11 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.326-0800: 0.396: [Full GC (Ergonomics) [PSYoungGen: 29692K->14001K(58368K)] [ParOldGen: 174464K->174977K(175104K)] 204156K->188978K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0197305 secs] [Times: user=0.13 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.349-0800: 0.418: [Full GC (Ergonomics) [PSYoungGen: 29451K->17911K(58368K)] [ParOldGen: 174977K->174508K(175104K)] 204428K->192419K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0207601 secs] [Times: user=0.12 sys=0.01, real=0.03 secs]
2020-10-24T21:06:51.371-0800: 0.441: [Full GC (Ergonomics) [PSYoungGen: 29696K->19583K(58368K)] [ParOldGen: 174508K->174521K(175104K)] 204204K->194104K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0184822 secs] [Times: user=0.11 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.392-0800: 0.461: [Full GC (Ergonomics) [PSYoungGen: 29696K->18754K(58368K)] [ParOldGen: 174521K->174810K(175104K)] 204217K->193564K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0188716 secs] [Times: user=0.12 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.412-0800: 0.482: [Full GC (Ergonomics) [PSYoungGen: 29695K->22727K(58368K)] [ParOldGen: 174810K->174521K(175104K)] 204505K->197248K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0242051 secs] [Times: user=0.15 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.438-0800: 0.507: [Full GC (Ergonomics) [PSYoungGen: 29693K->22947K(58368K)] [ParOldGen: 174521K->174898K(175104K)] 204215K->197845K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0236721 secs] [Times: user=0.16 sys=0.00, real=0.03 secs]
2020-10-24T21:06:51.463-0800: 0.532: [Full GC (Ergonomics) [PSYoungGen: 29685K->23036K(58368K)] [ParOldGen: 174898K->174953K(175104K)] 204583K->197990K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0195883 secs] [Times: user=0.12 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.484-0800: 0.553: [Full GC (Ergonomics) [PSYoungGen: 29681K->22246K(58368K)] [ParOldGen: 174953K->175036K(175104K)] 204635K->197283K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0195309 secs] [Times: user=0.12 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.504-0800: 0.574: [Full GC (Ergonomics) [PSYoungGen: 29661K->24284K(58368K)] [ParOldGen: 175036K->175029K(175104K)] 204697K->199314K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0186212 secs] [Times: user=0.12 sys=0.01, real=0.02 secs]
2020-10-24T21:06:51.524-0800: 0.594: [Full GC (Ergonomics) [PSYoungGen: 29627K->25639K(58368K)] [ParOldGen: 175029K->175018K(175104K)] 204657K->200658K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0122496 secs] [Times: user=0.08 sys=0.00, real=0.01 secs]
2020-10-24T21:06:51.537-0800: 0.607: [Full GC (Ergonomics) [PSYoungGen: 29696K->25735K(58368K)] [ParOldGen: 175018K->174989K(175104K)] 204714K->200725K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0128341 secs] [Times: user=0.08 sys=0.00, real=0.02 secs]
2020-10-24T21:06:51.551-0800: 0.621: [Full GC (Ergonomics) [PSYoungGen: 29643K->26366K(58368K)] [ParOldGen: 174989K->175096K(175104K)] 204632K->201462K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0062705 secs] [Times: user=0.04 sys=0.00, real=0.00 secs]
2020-10-24T21:06:51.558-0800: 0.627: [Full GC (Ergonomics) [PSYoungGen: 29542K->27738K(58368K)] [ParOldGen: 175096K->174997K(175104K)] 204639K->202735K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0238922 secs] [Times: user=0.13 sys=0.00, real=0.03 secs]
2020-10-24T21:06:51.582-0800: 0.652: [Full GC (Ergonomics) [PSYoungGen: 29573K->28115K(58368K)] [ParOldGen: 174997K->174997K(175104K)] 204570K->203112K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0034332 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-24T21:06:51.586-0800: 0.656: [Full GC (Ergonomics) [PSYoungGen: 29525K->28449K(58368K)] [ParOldGen: 174997K->174628K(175104K)] 204522K->203077K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0051581 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
2020-10-24T21:06:51.591-0800: 0.661: [Full GC (Ergonomics) [PSYoungGen: 29421K->28792K(58368K)] [ParOldGen: 174628K->174628K(175104K)] 204049K->203420K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0022134 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-24T21:06:51.594-0800: 0.663: [Full GC (Ergonomics) [PSYoungGen: 29559K->28936K(58368K)] [ParOldGen: 174628K->174628K(175104K)] 204187K->203564K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0019199 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T21:06:51.596-0800: 0.666: [Full GC (Ergonomics) [PSYoungGen: 29456K->28988K(58368K)] [ParOldGen: 174628K->174628K(175104K)] 204084K->203616K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0019255 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-24T21:06:51.598-0800: 0.668: [Full GC (Allocation Failure) [PSYoungGen: 28988K->28988K(58368K)] [ParOldGen: 174628K->174609K(175104K)] 203616K->203597K(233472K), [Metaspace: 2892K->2892K(1056768K)], 0.0294956 secs] [Times: user=0.18 sys=0.00, real=0.03 secs]
Heap
 PSYoungGen      total 58368K, used 29609K [0x00000007bab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 29696K, 99% used [0x00000007bab00000,0x00000007bc7ea640,0x00000007bc800000)
  from space 28672K, 0% used [0x00000007bc800000,0x00000007bc800000,0x00000007be400000)
  to   space 28672K, 0% used [0x00000007be400000,0x00000007be400000,0x00000007c0000000)
 ParOldGen       total 175104K, used 174609K [0x00000007b0000000, 0x00000007bab00000, 0x00000007bab00000)
  object space 175104K, 99% used [0x00000007b0000000,0x00000007baa84498,0x00000007bab00000)
 Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 311K, capacity 386K, committed 512K, reserved 1048576K
```

###### 分析

1. 执行了9次Young GC和25次Full GC（连续25次，且最后一次Full GC失败的原因是Allocation Failure，直接OOM）
2. 第1次Full GC到第4次Full GC不但没有回收Old区，反而从Young区晋升了更多的对象
3. 第5次Full GC到第25次Full GC基本无法回收Old区，由于无法晋升到Old区，Young区只能不断增长，最终导致了OOM
4. 从JVM进程退出前的内存布局可知，Eden区使用了99%，Old区也使用了99%
   - Old区无法回收，且To区无法容纳Eden区所有的存活对象，最终导致了Eden区无法分配新对象，最终导致了OOM

**Heap After GC**

Full GC基本没有回收多少空间

![image-20201024214518643](image/image-20201024214518643.png)

**GC Statistics**

![image-20201024214209252](image/image-20201024214209252.png)

**Object Stats**

![image-20201024214258824](image/image-20201024214258824.png)

**GC Causes**

![image-20201024214335398](image/image-20201024214335398.png)

##### 对比

|                                        | default     | 4096m      | 2048m       | 1024m       | 512m        | 256m - OOM |
| -------------------------------------- | ----------- | ---------- | ----------- | ----------- | ----------- | ---------- |
| 生成对象次数                           | 11547       | 11819      | 12610       | 12483       | 7659        | -          |
| Minor GC count                         | 9           | 3          | 7           | 21          | 19          | 9          |
| Minor GC min/max time (ms)             | 10.0 / 70.0 | 80.0 / 110 | 10.0 / 70.0 | 0 / 40.0    | 0 / 20.0    | 0 / 10.0   |
| Full GC Count                          | 3           | 0          | 0           | 1           | 12          | 25         |
| Full GC min/max time (ms)              | 20.0 / 40.0 | -          | -           | 50.0 / 50.0 | 30.0 / 40.0 | 0 / 30.0   |
| Pause Count                            | 12          | 3          | 7           | 22          | 31          | 34         |
| Pause total time (ms)                  | 410         | 270        | 360         | 430         | 620         | 480        |
| Avg creation rate (gb/sec)             | 3           | 6.59       | 4.55        | 3.71        | 2.23        | 1.33       |
| Avg promotion rate (mb/sec)            | 357.54      | 709.38     | 903.16      | 987.73      | 467.18      | 266.42     |
| (1- pause) * log(creation / promotion) | 0.5450      | 0.7066     | 0.4494      | 0.3275      | 0.2579      | -          |
| 备注                                   |             | 综合最佳   |             |             |             |            |

#### 串行

##### default

###### 命令

```
$ java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC -Xloggc:gc_log/serial_default.log GCLogAnalysis
正在执行...
执行结束!共生成对象次数:9105
```

###### 日志

```
OpenJDK 64-Bit Server VM (25.265-b11) for bsd-amd64 JRE (Zulu 8.48.0.53-CA-macosx) (1.8.0_265-b11), built on Jul 28 2020 03:07:54 by "zulu_re" with gcc 4.2.1 (Based on Apple Inc. build 5658) (LLVM build 2336.11.00)
Memory: 4k page, physical 16777216k(27900k free)

/proc/meminfo:

CommandLine flags: -XX:InitialHeapSize=268435456 -XX:MaxHeapSize=4294967296 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
2020-10-24T22:50:00.209-0800: 0.171: [GC (Allocation Failure) 2020-10-24T22:50:00.209-0800: 0.171: [DefNew: 69770K->8704K(78656K), 0.0167116 secs] 69770K->23561K(253440K), 0.0168579 secs] [Times: user=0.01 sys=0.00, real=0.02 secs]
2020-10-24T22:50:00.239-0800: 0.201: [GC (Allocation Failure) 2020-10-24T22:50:00.239-0800: 0.201: [DefNew: 78568K->8696K(78656K), 0.0203654 secs] 93425K->45195K(253440K), 0.0204603 secs] [Times: user=0.01 sys=0.00, real=0.03 secs]
2020-10-24T22:50:00.268-0800: 0.229: [GC (Allocation Failure) 2020-10-24T22:50:00.268-0800: 0.229: [DefNew: 78648K->8702K(78656K), 0.0177235 secs] 115147K->69057K(253440K), 0.0178036 secs] [Times: user=0.01 sys=0.01, real=0.02 secs]
2020-10-24T22:50:00.295-0800: 0.257: [GC (Allocation Failure) 2020-10-24T22:50:00.295-0800: 0.257: [DefNew: 78550K->8701K(78656K), 0.0145468 secs] 138904K->89206K(253440K), 0.0146303 secs] [Times: user=0.00 sys=0.01, real=0.02 secs]
2020-10-24T22:50:00.320-0800: 0.281: [GC (Allocation Failure) 2020-10-24T22:50:00.320-0800: 0.281: [DefNew: 78653K->8699K(78656K), 0.0165465 secs] 159158K->114132K(253440K), 0.0166275 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-24T22:50:00.346-0800: 0.308: [GC (Allocation Failure) 2020-10-24T22:50:00.346-0800: 0.308: [DefNew: 78651K->8703K(78656K), 0.0155677 secs] 184084K->136176K(253440K), 0.0156476 secs] [Times: user=0.01 sys=0.01, real=0.02 secs]
2020-10-24T22:50:00.371-0800: 0.333: [GC (Allocation Failure) 2020-10-24T22:50:00.371-0800: 0.333: [DefNew: 78571K->8701K(78656K), 0.0149883 secs] 206043K->159091K(253440K), 0.0150656 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
2020-10-24T22:50:00.395-0800: 0.357: [GC (Allocation Failure) 2020-10-24T22:50:00.395-0800: 0.357: [DefNew: 78653K->8699K(78656K), 0.0185561 secs]2020-10-24T22:50:00.414-0800: 0.375: [Tenured: 175155K->161023K(175168K), 0.0296649 secs] 229043K->161023K(253824K), [Metaspace: 2892K->2892K(1056768K)], 0.0485336 secs] [Times: user=0.04 sys=0.00, real=0.05 secs]
2020-10-24T22:50:00.470-0800: 0.432: [GC (Allocation Failure) 2020-10-24T22:50:00.470-0800: 0.432: [DefNew: 107456K->13375K(120832K), 0.0186722 secs] 268479K->199318K(389208K), 0.0187701 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
2020-10-24T22:50:00.505-0800: 0.466: [GC (Allocation Failure) 2020-10-24T22:50:00.505-0800: 0.466: [DefNew: 120755K->13375K(120832K), 0.0283379 secs] 306699K->233747K(389208K), 0.0284681 secs] [Times: user=0.02 sys=0.01, real=0.03 secs]
2020-10-24T22:50:00.546-0800: 0.507: [GC (Allocation Failure) 2020-10-24T22:50:00.546-0800: 0.507: [DefNew: 120831K->13370K(120832K), 0.0237774 secs] 341203K->264907K(389208K), 0.0238661 secs] [Times: user=0.01 sys=0.01, real=0.03 secs]
2020-10-24T22:50:00.583-0800: 0.544: [GC (Allocation Failure) 2020-10-24T22:50:00.583-0800: 0.544: [DefNew: 120826K->13375K(120832K), 0.0243821 secs]2020-10-24T22:50:00.607-0800: 0.569: [Tenured: 287283K->251154K(287360K), 0.0388825 secs] 372363K->251154K(408192K), [Metaspace: 2892K->2892K(1056768K)], 0.0637057 secs] [Times: user=0.06 sys=0.01, real=0.06 secs]
2020-10-24T22:50:00.683-0800: 0.644: [GC (Allocation Failure) 2020-10-24T22:50:00.683-0800: 0.644: [DefNew: 167488K->20928K(188416K), 0.0196327 secs] 418642K->300181K(607008K), 0.0197226 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2020-10-24T22:50:00.725-0800: 0.686: [GC (Allocation Failure) 2020-10-24T22:50:00.725-0800: 0.686: [DefNew: 188416K->20926K(188416K), 0.0408684 secs] 467669K->351314K(607008K), 0.0409512 secs] [Times: user=0.02 sys=0.01, real=0.04 secs]
2020-10-24T22:50:00.786-0800: 0.748: [GC (Allocation Failure) 2020-10-24T22:50:00.786-0800: 0.748: [DefNew: 188414K->20927K(188416K), 0.0336036 secs] 518802K->400071K(607008K), 0.0337492 secs] [Times: user=0.02 sys=0.02, real=0.04 secs]
2020-10-24T22:50:00.838-0800: 0.800: [GC (Allocation Failure) 2020-10-24T22:50:00.838-0800: 0.800: [DefNew: 188415K->20927K(188416K), 0.0394888 secs]2020-10-24T22:50:00.878-0800: 0.839: [Tenured: 433937K->324101K(434004K), 0.0648031 secs] 567559K->324101K(622420K), [Metaspace: 2892K->2892K(1056768K)], 0.1046629 secs] [Times: user=0.08 sys=0.01, real=0.11 secs]
2020-10-24T22:50:00.975-0800: 0.936: [GC (Allocation Failure) 2020-10-24T22:50:00.975-0800: 0.936: [DefNew: 216128K->27006K(243136K), 0.0256124 secs] 540229K->401319K(783308K), 0.0256951 secs] [Times: user=0.02 sys=0.01, real=0.03 secs]
2020-10-24T22:50:01.031-0800: 0.993: [GC (Allocation Failure) 2020-10-24T22:50:01.031-0800: 0.993: [DefNew: 243134K->27005K(243136K), 0.0292236 secs] 617447K->464337K(783308K), 0.0293151 secs] [Times: user=0.02 sys=0.01, real=0.03 secs]
2020-10-24T22:50:01.086-0800: 1.048: [GC (Allocation Failure) 2020-10-24T22:50:01.086-0800: 1.048: [DefNew: 243133K->27007K(243136K), 0.0459375 secs] 680465K->526657K(783308K), 0.0460166 secs] [Times: user=0.03 sys=0.02, real=0.05 secs]
Heap
 def new generation   total 243136K, used 154331K [0x00000006c0000000, 0x00000006d07d0000, 0x0000000715550000)
  eden space 216128K,  58% used [0x00000006c0000000, 0x00000006c7c56fe8, 0x00000006cd310000)
  from space 27008K,  99% used [0x00000006ced70000, 0x00000006d07cffe8, 0x00000006d07d0000)
  to   space 27008K,   0% used [0x00000006cd310000, 0x00000006cd310000, 0x00000006ced70000)
 tenured generation   total 540172K, used 499649K [0x0000000715550000, 0x00000007364d3000, 0x00000007c0000000)
   the space 540172K,  92% used [0x0000000715550000, 0x0000000733d40760, 0x0000000733d40800, 0x00000007364d3000)
 Metaspace       used 2899K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 309K, capacity 386K, committed 512K, reserved 1048576K
```

###### 分析

1. 执行了16次Young GC和3次Full GC，仅分析第1次Young GC和第1次Full GC

2. Young GC

   - Allocation Failure
     - 触发GC的原因
   - 2020-10-24T22:50:00.209-0800: 0.171
     - GC发生的时间

   - DefNew: 69770K->8704K(78656K)
     - Young区容量为78656K（76.8MB）
     - Young区经过Young GC后，从69770K（68.1MB）降到8704K（8.5MB），回收了87.5%的对象
       - 由于串行GC会导致STW，而且Young = Eden + From = Eden + To，从而可以推算出To区为8.5MB，Eden区为68.3MB
   - 69770K->23561K(253440K)
     - Heap容量为253440K（247.5MB = InitialHeapSize - To = Young + Old = 256MB - 8.5MB）
       - 从而可以简单推算出Old区为170.7MB
     - Heap经过Young GC后，从69770K（68.1MB < Eden = 68.3MB）降到23561K（23MB），回收了66.2%的对象
     - 对比Young区的回收情况，有14857K（14.5MB ≈ 23561K - 8704K）晋升到了Old区
   - 0.0168579 secs
     - Young GC耗时为0.0168579秒

3. Full GC

   - Allocation Failure
     - 触发GC的原因
   - 2020-10-24T22:50:00.395-0800: 0.357
     - 回收Young区的时间戳
   - DefNew: 78653K->8699K(78656K), 0.0185561 secs
     - Young区容量为78656K（76.8MB）
     - Young区经过回收后，从78653K（76.8MB）降到8699K（8.5MB），回收了88.9%的对象
       - 回收的目标为Eden+From，迁移或晋升的目标为To+Old
     - 实际耗时0.0185561秒，结束的时间戳为2020-10-24T22:50:00.41356 ≈ 2020-10-24T22:50:00.395+0.01856
       - 与下一阶段开始的时间很接近
   - 2020-10-24T22:50:00.414-0800: 0.375
     - 回收Old区的时间戳
   - Tenured: 175155K->161023K(175168K), 0.0296649 secs
     - Old区容量为175168K（171.1MB）
     - Old区经过Full GC后，从175155K（171.0MB）降到161023K（157.2MB），回收了8%的对象
     - 实际耗时0.0296649秒
   - 229043K->161023K(253824K)
     - Heap容量为253824K（247.9MB = Young + Old = 76.8MB + 171.1MB）
     - Heap经过Full GC后，从229043K降到161023K，回收了29.7%的对象
   - Metaspace: 2892K->2892K(1056768K)
     - Meta区容量为1056768K（1032MB）
     - Meta区经过Full GC后，空间没有变化
       - 因为Meta区只占用了2892K，远低于MetaspaceSize的默认值（21807104K ≈ 20.8MB）
   - 0.0485336 secs
     - 实际耗时0.0485336秒 ≈ 回收Young区耗时 + 回收 Old区耗时 =  0.0185561秒 + 0.0296649秒 = 0.048221秒

##### 128m

###### 命令

```
$ java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC -Xloggc:gc_log/serial_128.log -Xms128m -Xmx128m GCLogAnalysis
正在执行...
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at GCLogAnalysis.generateGarbage(GCLogAnalysis.java:50)
	at GCLogAnalysis.main(GCLogAnalysis.java:27)
```

###### 日志

```
OpenJDK 64-Bit Server VM (25.265-b11) for bsd-amd64 JRE (Zulu 8.48.0.53-CA-macosx) (1.8.0_265-b11), built on Jul 28 2020 03:07:54 by "zulu_re" with gcc 4.2.1 (Based on Apple Inc. build 5658) (LLVM build 2336.11.00)
Memory: 4k page, physical 16777216k(667560k free)

/proc/meminfo:

CommandLine flags: -XX:InitialHeapSize=134217728 -XX:MaxHeapSize=134217728 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC
2020-10-24T23:59:12.678-0800: 0.135: [GC (Allocation Failure) 2020-10-24T23:59:12.678-0800: 0.135: [DefNew: 34514K->4352K(39296K), 0.0086749 secs] 34514K->14050K(126720K), 0.0087644 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.698-0800: 0.156: [GC (Allocation Failure) 2020-10-24T23:59:12.698-0800: 0.156: [DefNew: 38969K->4350K(39296K), 0.0096983 secs] 48667K->25985K(126720K), 0.0097661 secs] [Times: user=0.00 sys=0.01, real=0.01 secs]
2020-10-24T23:59:12.713-0800: 0.170: [GC (Allocation Failure) 2020-10-24T23:59:12.713-0800: 0.171: [DefNew: 39065K->4348K(39296K), 0.0089042 secs] 60700K->41614K(126720K), 0.0089697 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.727-0800: 0.184: [GC (Allocation Failure) 2020-10-24T23:59:12.727-0800: 0.184: [DefNew: 39292K->4350K(39296K), 0.0056288 secs] 76558K->50807K(126720K), 0.0056891 secs] [Times: user=0.00 sys=0.01, real=0.01 secs]
2020-10-24T23:59:12.738-0800: 0.195: [GC (Allocation Failure) 2020-10-24T23:59:12.738-0800: 0.195: [DefNew: 39179K->4351K(39296K), 0.0083814 secs] 85637K->64253K(126720K), 0.0084454 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.750-0800: 0.208: [GC (Allocation Failure) 2020-10-24T23:59:12.751-0800: 0.208: [DefNew: 39295K->4349K(39296K), 0.0071665 secs] 99197K->75567K(126720K), 0.0072277 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.761-0800: 0.219: [GC (Allocation Failure) 2020-10-24T23:59:12.762-0800: 0.219: [DefNew: 39277K->39277K(39296K), 0.0000152 secs]2020-10-24T23:59:12.762-0800: 0.219: [Tenured: 71217K->86506K(87424K), 0.0164639 secs] 110495K->86506K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0165557 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
2020-10-24T23:59:12.783-0800: 0.240: [GC (Allocation Failure) 2020-10-24T23:59:12.783-0800: 0.240: [DefNew: 34608K->34608K(39296K), 0.0000165 secs]2020-10-24T23:59:12.783-0800: 0.240: [Tenured: 86506K->87352K(87424K), 0.0098137 secs] 121115K->94034K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0099141 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.797-0800: 0.254: [Full GC (Allocation Failure) 2020-10-24T23:59:12.797-0800: 0.255: [Tenured: 87352K->87423K(87424K), 0.0082987 secs] 126583K->102626K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0083694 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.810-0800: 0.267: [Full GC (Allocation Failure) 2020-10-24T23:59:12.810-0800: 0.267: [Tenured: 87423K->86752K(87424K), 0.0145636 secs] 126578K->103525K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0146465 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.828-0800: 0.285: [Full GC (Allocation Failure) 2020-10-24T23:59:12.828-0800: 0.286: [Tenured: 87277K->87277K(87424K), 0.0035623 secs] 126571K->110618K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0036302 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.834-0800: 0.292: [Full GC (Allocation Failure) 2020-10-24T23:59:12.834-0800: 0.292: [Tenured: 87277K->87277K(87424K), 0.0036043 secs] 126380K->115549K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0036788 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.840-0800: 0.297: [Full GC (Allocation Failure) 2020-10-24T23:59:12.840-0800: 0.297: [Tenured: 87277K->87374K(87424K), 0.0047766 secs] 126492K->118317K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0048435 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.846-0800: 0.303: [Full GC (Allocation Failure) 2020-10-24T23:59:12.846-0800: 0.303: [Tenured: 87374K->87201K(87424K), 0.0154019 secs] 126638K->117408K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0154748 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2020-10-24T23:59:12.864-0800: 0.321: [Full GC (Allocation Failure) 2020-10-24T23:59:12.864-0800: 0.321: [Tenured: 87417K->87417K(87424K), 0.0037510 secs] 126686K->118801K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0038198 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.869-0800: 0.326: [Full GC (Allocation Failure) 2020-10-24T23:59:12.869-0800: 0.326: [Tenured: 87417K->87417K(87424K), 0.0037826 secs] 126695K->118905K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0038570 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.874-0800: 0.332: [Full GC (Allocation Failure) 2020-10-24T23:59:12.874-0800: 0.332: [Tenured: 87417K->87417K(87424K), 0.0020277 secs] 126393K->121419K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0020863 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.877-0800: 0.334: [Full GC (Allocation Failure) 2020-10-24T23:59:12.877-0800: 0.334: [Tenured: 87417K->86965K(87424K), 0.0147902 secs] 126519K->119941K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0148591 secs] [Times: user=0.02 sys=0.00, real=0.02 secs]
2020-10-24T23:59:12.893-0800: 0.350: [Full GC (Allocation Failure) 2020-10-24T23:59:12.893-0800: 0.350: [Tenured: 86965K->86965K(87424K), 0.0033514 secs] 126184K->121520K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0034067 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.897-0800: 0.355: [Full GC (Allocation Failure) 2020-10-24T23:59:12.897-0800: 0.355: [Tenured: 87403K->87403K(87424K), 0.0029236 secs] 126663K->123820K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0029832 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.901-0800: 0.358: [Full GC (Allocation Failure) 2020-10-24T23:59:12.901-0800: 0.358: [Tenured: 87403K->87403K(87424K), 0.0021380 secs] 126513K->124891K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0021987 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.904-0800: 0.361: [Full GC (Allocation Failure) 2020-10-24T23:59:12.904-0800: 0.361: [Tenured: 87403K->87025K(87424K), 0.0115926 secs] 126437K->125164K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0116599 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.915-0800: 0.373: [Full GC (Allocation Failure) 2020-10-24T23:59:12.915-0800: 0.373: [Tenured: 87025K->87025K(87424K), 0.0017801 secs] 125832K->125164K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0018260 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.917-0800: 0.374: [Full GC (Allocation Failure) 2020-10-24T23:59:12.917-0800: 0.374: [Tenured: 87025K->87025K(87424K), 0.0016768 secs] 125805K->125196K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0017269 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.919-0800: 0.376: [Full GC (Allocation Failure) 2020-10-24T23:59:12.919-0800: 0.376: [Tenured: 87025K->87025K(87424K), 0.0016009 secs] 125861K->125196K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0016419 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-24T23:59:12.921-0800: 0.378: [Full GC (Allocation Failure) 2020-10-24T23:59:12.921-0800: 0.378: [Tenured: 87025K->87025K(87424K), 0.0017124 secs] 125678K->125196K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0017688 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.923-0800: 0.380: [Full GC (Allocation Failure) 2020-10-24T23:59:12.923-0800: 0.380: [Tenured: 87025K->87025K(87424K), 0.0014614 secs] 125853K->125853K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0015106 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-24T23:59:12.925-0800: 0.382: [Full GC (Allocation Failure) 2020-10-24T23:59:12.925-0800: 0.382: [Tenured: 87025K->87006K(87424K), 0.0085201 secs] 125853K->125834K(126720K), [Metaspace: 2892K->2892K(1056768K)], 0.0085717 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
Heap
 def new generation   total 39296K, used 39125K [0x00000007b8000000, 0x00000007baaa0000, 0x00000007baaa0000)
  eden space 34944K, 100% used [0x00000007b8000000, 0x00000007ba220000, 0x00000007ba220000)
  from space 4352K,  96% used [0x00000007ba220000, 0x00000007ba635618, 0x00000007ba660000)
  to   space 4352K,   0% used [0x00000007ba660000, 0x00000007ba660000, 0x00000007baaa0000)
 tenured generation   total 87424K, used 87006K [0x00000007baaa0000, 0x00000007c0000000, 0x00000007c0000000)
   the space 87424K,  99% used [0x00000007baaa0000, 0x00000007bff978f8, 0x00000007bff97a00, 0x00000007c0000000)
 Metaspace       used 2923K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 311K, capacity 386K, committed 512K, reserved 1048576K
```

###### 分析

1. 先执行了6次Young GC，Young区在不断增长，Old区接收晋升对象，也在不断增长
2. 再执行2次带Young区整理的Full GC，但Young区完全没有变化，新创建的对象直接进去Old区，导致Old区继续增长
3. 因为前面已经尝试整理Young区失败2次，并且Old区在不断增长，最后的20次Full GC只尝试整理Old区，但Old区依然在不断增长
4. 最后新创建的对象哪怕直接尝试在Old区分配，也无法找到足够的内存，最终导致OOM

##### 对比

|                                        | default     | 4096m     | 2048m     | 1024m   | 512m    | 256m    | 128m - OOM |
| -------------------------------------- | ----------- | --------- | --------- | ------- | ------- | ------- | ---------- |
| 生成对象次数                           | 9105        | 8302      | 8529      | 8472    | 8803    | 4645    | -          |
| Minor GC count                         | 16          | 2         | 4         | 8       | 8       | 7       | 6          |
| Minor GC min/max time (ms)             | 10.0 / 50.0 | 160 / 200 | 100 / 140 | 50 / 90 | 20 / 40 | 10 / 20 | 0 / 10     |
| Full GC Count                          | 3           | 0         | 0         | 0       | 8       | 44      | 22         |
| Full GC min/max time (ms)              | 50.0 / 110  | -         | -         | -       | 40 / 60 | 0 / 40  | 0 / 20     |
| Pause Count                            | 19          | 2         | 4         | 8       | 16      | 51      | 28         |
| Pause total time (ms)                  | 630         | 360       | 490       | 540     | 610     | 790     | 190        |
| Avg creation rate (gb/sec)             | 2.51        | 6.15      | 3.25      | 2.68    | 2.53    | 1.26    | 1.7        |
| Avg promotion rate (mb/sec)            | 686.76      | 751.84    | 681.38    | 745.75  | 395.62  | 158.07  | 281.57     |
| (1- pause) * log(creation / promotion) | 0.1853      | 0.5841    | 0.3460    | 0.2555  | 0.3142  | 0.1893  | -          |
| 备注                                   |             | 综合最佳  |           |         |         |         |            |

#### CMS

#### G1

## 第2题

### 描述

使用压测工具(wrk或sb)，演练gateway-server-0.0.1-SNAPSHOT.jar 示例。

### 解答

