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