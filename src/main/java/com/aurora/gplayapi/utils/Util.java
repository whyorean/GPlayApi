package com.aurora.gplayapi.utils;

import okhttp3.HttpUrl;

import java.util.*;

public class Util {

    public static Map<String, String> parseResponse(String response) {
        final Map<String, String> keyValueMap = new HashMap<String, String>();
        final StringTokenizer stringTokenizer = new StringTokenizer(response, "\n\r");
        while (stringTokenizer.hasMoreTokens()) {
            final String[] keyValue = stringTokenizer.nextToken().split("=", 2);
            if (keyValue.length >= 2) {
                keyValueMap.put(keyValue[0], keyValue[1]);
            }
        }
        return keyValueMap;
    }

    public static String buildUrlEx(String url, Map<String, List<String>> params) {
        final HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (null != params && !params.isEmpty()) {
            for (String name : params.keySet()) {
                for (String value : params.get(name)) {
                    urlBuilder.addQueryParameter(name, value);
                }
            }
        }
        return urlBuilder.build().toString();
    }
}
