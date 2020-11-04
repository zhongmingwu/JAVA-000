# Constant

```java
package me.zhongmingwu.week03.constant;

public class Constant {
    public static String GATEWAY_HOST = System.getProperty("gateway_host", "127.0.0.1");
    public static int GATEWAY_PORT = Integer.parseInt(System.getProperty("gateway_port", "8888"));
    public static String GATEWAY_URL = String.format("http://%s:%d", GATEWAY_HOST, GATEWAY_PORT);

    public static String SERVICE_HOST = System.getProperty("service_host", "127.0.0.1");
    public static int SERVICE_PORT = Integer.parseInt(System.getProperty("service_port", "9999"));
    public static String SERVICE_URL = String.format("http://%s:%d", SERVICE_HOST, SERVICE_PORT);
}
```

# Client

```java
package me.zhongmingwu.week03.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Client implements Callable<String> {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build();

    private String url;

    public Client(String url) {
        this.url = url;
    }

    @Override
    public String call() throws Exception {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String str = Objects.requireNonNull(response.body()).string();
            System.out.println("===== " + LocalDateTime.now());
            System.out.println(str);
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

# GatewayHandler

```java
package me.zhongmingwu.week03.gateway;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import me.zhongmingwu.week03.client.Client;
import me.zhongmingwu.week03.constant.Constant;

public class GatewayHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Client client = new Client(Constant.SERVICE_URL);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            FullHttpResponse response = new DefaultFullHttpResponse(
                    request.protocolVersion(), HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer((client.call() + "\nI am Gateway").getBytes()));
            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                    .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            ctx.write(response);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
```

# Gateway

```java
package me.zhongmingwu.week03.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import me.zhongmingwu.week03.constant.Constant;

public class Gateway implements Runnable {

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpServerExpectContinueHandler());
                            pipeline.addLast(new GatewayHandler());
                        }
                    });
            Channel channel = bootstrap.bind(Constant.GATEWAY_PORT).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```

# ServiceHandler

```java
package me.zhongmingwu.week03.service;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

public class ServiceHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            FullHttpResponse response = new DefaultFullHttpResponse(
                    request.protocolVersion(), HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer("I am Service".getBytes()));
            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                    .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            ctx.write(response);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
```

# Service

```java
package me.zhongmingwu.week03.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import me.zhongmingwu.week03.constant.Constant;

public class Service implements Runnable {

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpServerExpectContinueHandler());
                            pipeline.addLast(new ServiceHandler());
                        }
                    });
            Channel channel = bootstrap.bind(Constant.SERVICE_PORT).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```

# GatewayTest

```java
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
```

# Run Time

```
Nov 04, 2020 8:43:45 PM io.netty.handler.logging.LoggingHandler channelRegistered
INFO: [id: 0x1ff2a0eb] REGISTERED
Nov 04, 2020 8:43:45 PM io.netty.handler.logging.LoggingHandler bind
INFO: [id: 0x1ff2a0eb] BIND: 0.0.0.0/0.0.0.0:9999
Nov 04, 2020 8:43:45 PM io.netty.handler.logging.LoggingHandler channelActive
INFO: [id: 0x1ff2a0eb, L:/0:0:0:0:0:0:0:0:9999] ACTIVE
Nov 04, 2020 8:43:46 PM io.netty.handler.logging.LoggingHandler channelRegistered
INFO: [id: 0x9985ed10] REGISTERED
Nov 04, 2020 8:43:46 PM io.netty.handler.logging.LoggingHandler bind
INFO: [id: 0x9985ed10] BIND: 0.0.0.0/0.0.0.0:8888
Nov 04, 2020 8:43:46 PM io.netty.handler.logging.LoggingHandler channelActive
INFO: [id: 0x9985ed10, L:/0:0:0:0:0:0:0:0:8888] ACTIVE
Nov 04, 2020 8:43:47 PM io.netty.handler.logging.LoggingHandler channelRead
INFO: [id: 0x9985ed10, L:/0:0:0:0:0:0:0:0:8888] READ: [id: 0xa539b22e, L:/127.0.0.1:8888 - R:/127.0.0.1:62041]
Nov 04, 2020 8:43:47 PM io.netty.handler.logging.LoggingHandler channelReadComplete
INFO: [id: 0x9985ed10, L:/0:0:0:0:0:0:0:0:8888] READ COMPLETE
Nov 04, 2020 8:43:47 PM io.netty.handler.logging.LoggingHandler channelRead
INFO: [id: 0x1ff2a0eb, L:/0:0:0:0:0:0:0:0:9999] READ: [id: 0xe4f89bc8, L:/127.0.0.1:9999 - R:/127.0.0.1:62042]
Nov 04, 2020 8:43:47 PM io.netty.handler.logging.LoggingHandler channelReadComplete
INFO: [id: 0x1ff2a0eb, L:/0:0:0:0:0:0:0:0:9999] READ COMPLETE
===== 2020-11-04T20:43:47.964
I am Service
===== 2020-11-04T20:43:47.966
I am Service
I am Gateway
```

