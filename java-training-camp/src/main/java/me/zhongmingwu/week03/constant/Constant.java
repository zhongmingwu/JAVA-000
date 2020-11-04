package me.zhongmingwu.week03.constant;

public class Constant {
    public static String GATEWAY_HOST = System.getProperty("gateway_host", "127.0.0.1");
    public static int GATEWAY_PORT = Integer.parseInt(System.getProperty("gateway_port", "8888"));
    public static String GATEWAY_URL = String.format("http://%s:%d", GATEWAY_HOST, GATEWAY_PORT);

    public static String SERVICE_HOST = System.getProperty("service_host", "127.0.0.1");
    public static int SERVICE_PORT = Integer.parseInt(System.getProperty("service_port", "9999"));
    public static String SERVICE_URL = String.format("http://%s:%d", SERVICE_HOST, SERVICE_PORT);
}
