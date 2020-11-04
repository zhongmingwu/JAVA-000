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
