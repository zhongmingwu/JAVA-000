package me.zhongmingwu.week03;

import me.zhongmingwu.week03.client.Client;
import me.zhongmingwu.week03.constant.Constant;
import me.zhongmingwu.week03.gateway.Gateway;
import me.zhongmingwu.week03.service.Service;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GatewayTest {

    @Test
    public void test() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);

        pool.submit(new Service());
        TimeUnit.SECONDS.sleep(1);

        pool.submit(new Gateway());
        TimeUnit.SECONDS.sleep(1);

        pool.submit(new Client(Constant.GATEWAY_URL));
        Thread.currentThread().join();
    }
}
