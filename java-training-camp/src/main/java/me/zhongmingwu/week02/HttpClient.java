package me.zhongmingwu.week02;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class HttpClient {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://localhost:8081").build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println(Objects.requireNonNull(response.body()).string());
        }
    }
}
