package me.zhongmingwu.week01;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;

public class HelloClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = decode();
        return defineClass(name, bytes, 0, bytes.length);
    }

    private byte[] decode() {
        byte[] tmpBytes = new byte[0];
        try {
            tmpBytes = Files.readAllBytes(new File(System.getProperty("hello_class_location")).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = new byte[tmpBytes.length];
        for (int i = 0; i < tmpBytes.length; i++) {
            bytes[i] = (byte) (255 - tmpBytes[i]);
        }
        return bytes;
    }

    public static void main(String[] args) throws Exception {
        Object hello = new HelloClassLoader().findClass("Hello").newInstance();
        Method method = hello.getClass().getMethod("hello");
        method.invoke(hello);
    }
}
