# 作业0

第8周作业已补，辛苦助教老师审阅，链接：https://github.com/zhongmingwu/JAVA-000/tree/main/Week_08

# 作业1

1. 描述：改造自定义RPC的程序，提交到GitHub
   - 尝试将服务端写死查找接口实现类变成泛型和反射
   - 尝试将客户端动态代理改成AOP，添加异常处理
   - 尝试使用Netty+HTTP作为Client端传输方式
2. 解答
   - https://github.com/zhongmingwu/java-training-camp/tree/main/week09/rpc

# 作业2
1. 描述：结合dubbo+hmily，实现一个TCC外汇交易处理，代码提交到GitHub
   - 用户A的美元账户和人民币账户都在A库，使用1美元兑换7人民币
   - 用户B的美元账户和人民币账户都在B库，使用7人民币兑换1美元
   - 设计账户表，冻结资产表，实现上述两个本地事务的分布式事务
2. 解答
   - https://github.com/zhongmingwu/java-training-camp/tree/main/week09/tcc