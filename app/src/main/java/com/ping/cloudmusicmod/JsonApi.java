package com.ping.cloudmusicmod;

import static com.ping.cloudmusicmod.utils.CommonUtils.LogError;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class JsonApi {
    private File file;

    public JsonApi(String jsonfFileName) {
        try {
            file = File.createTempFile(jsonfFileName, null);
        } catch (IOException e) {
            LogError("DataStoreJsonApi单例初始化错误", e);
        }
    }

    public void writeJson(String jsonContent) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(jsonContent.getBytes());
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String readJson() {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    public void set(String key, String value) {
        // 读取 JSON 文件内容
        String jsonString = readJson();
        // 解析 JSON 字符串为 JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        // 修改指定 key 的 value
        jsonObject.addProperty(key, value);
        // 将 JsonObject 转换为 JSON 字符串
        String updatedJsonString = jsonObject.toString();
        // 将更新后的 JSON 字符串写入文件
        writeJson(updatedJsonString);
    }

    public String get(String key) {
        // 读取 JSON 文件内容
        String jsonString = readJson();
        // 解析 JSON 字符串为 JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        // 获取指定 key 的 value
        JsonElement jsonElement = jsonObject.get(key);
        return jsonElement != null ? jsonElement.getAsString() : null;
    }
}


