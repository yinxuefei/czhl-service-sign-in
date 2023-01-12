package com.gdczhl.saas.config;
import cn.hutool.core.codec.Base64;
import com.gdczhl.saas.megbox.*;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ks")
public class MegBoxApi {
    private String ip;
    private Integer port;

    private String username;

    private String password;
    private String registerFaceLibName;

    @Value("${ks.ws.port}")
    private Integer wsPort;

    // 对比度
    @Value("${ks.search.score}")
    private Integer score;

    public String createUrl(String uri) {
        return String.format("http://%s:%s", this.ip, this.port) + uri;
    }

    /**
     * 获取分组
     *
     * @return
     * @throws IOException
     */
    public List<FaceGroupDto> getFaceGroups() throws IOException {
        HttpClient client = DigestHttpClient.createClient(this);
        String url = createUrl("/v1/MEGBOX/faceGroups");
        HttpGet httpGet = new HttpGet(url);
        HttpResponse resp = client.execute(httpGet);
        JSONObject respJson = new JSONObject(EntityUtils.toString(resp.getEntity()));
        if (respJson.getInt("code") == MegError.ERROR_OK.getCode()) {
            JSONArray faceGroupList = respJson.getJSONObject("data").getJSONArray("faceGroupList");
            List<FaceGroupDto> faceGroups = Lists.newArrayList();
            for (int i = 0; i < faceGroupList.length(); i++) {
                JSONObject faceGroupJSON = faceGroupList.getJSONObject(i);
                FaceGroupDto dto = new FaceGroupDto(faceGroupJSON.getString("groupName"), faceGroupJSON.getInt("groupFaces"));
                faceGroups.add(dto);
            }
            return faceGroups;
        }
        throw new RuntimeException("请求失败");
    }

    /**
     * 添加分组
     * @param groupName
     * @return
     * @throws IOException
     */
    public Boolean addFaceGroups(String groupName) throws IOException {
        HttpClient client = DigestHttpClient.createClient(this);
        String uri = "/v1/MEGBOX/faceGroups";
        String url = this.createUrl(uri);
        HttpPost httpPost = new HttpPost(url);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("groupName", groupName);
        httpPost.setEntity(new StringEntity(jsonObj.toString(), StandardCharsets.UTF_8));

        HttpResponse resp = client.execute(httpPost);
        JSONObject respJson = new JSONObject(EntityUtils.toString(resp.getEntity()));
        if (respJson.getInt("code") == MegError.ERROR_OK.getCode()) {
            return true;
        }
        return false;
    }


    /**
     * 单张入库
     *
     * @param imgId
     * @param base64
     * @return
     * @throws IOException
     */
    public AddFaceResp addFace(String imgId, String base64) throws IOException {
        HttpClient client = DigestHttpClient.createClient(this);
        String url = createUrl("/v1/MEGBOX/faces");
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头 boundary边界不可重复，重复会导致提交失败
        String boundary = UUID.randomUUID().toString();
        httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        // 创建MultipartEntityBuilder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 设置字符编码
        builder.setCharset(StandardCharsets.UTF_8);
        // 模拟浏览器
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        // 设置边界
        builder.setBoundary(boundary);
        // 设置multipart/form-data流文件
//        File file = FileUtil.writeBytes(Base64.decode(base64), new File());
        File tempFile = createTempFile(base64, boundary);
//        fos.flush();
        builder.addPart("image", new FileBody(tempFile, "image/jpeg", "utf8"));

        builder.addPart("description", new StringBody(imgId, ContentType.create("text/plain", Consts.UTF_8)));

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpResponse execute = client.execute(httpPost);
        HttpEntity respEntity = execute.getEntity();
        String resp = EntityUtils.toString(respEntity);
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(resp);
        if (jsonObject.getIntValue("code") == 0) {
            com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("data");
            String faceToken = data.getString("faceToken");
            if (bindingGroup(faceToken)) {

                return jsonObject.getObject("data", AddFaceResp.class);

            }
//            log.info("分组失败：{}", bindResp);
            return null;
        }
        log.warn("人脸入库失败：{}", resp);
        return null;
    }

    @NotNull
    private File createTempFile(String base64, String boundary) throws IOException {
        File tempFile = File.createTempFile("registerFace", boundary + ".jpg");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(Base64.decode(base64));
        return tempFile;
    }


    public boolean bindingGroup(String faceToken) throws IOException {

        HttpClient client = DigestHttpClient.createClient(this);
        String url = this.createUrl("/v1/MEGBOX/faceGroups/binding/" + faceToken);
        HttpPost httpPost = new HttpPost(url);


        String JSON_STRING = new JSONObject().put("faceGroupList", new JSONArray().put(registerFaceLibName)).toString();
        HttpEntity stringEntity = new StringEntity(JSON_STRING, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        HttpEntity respEntity = client.execute(httpPost).getEntity();
        String resp = EntityUtils.toString(respEntity);
        // System.out.println("对比度数据， resp = " + resp);
        JSONObject respJson = new JSONObject(resp);
        return respJson.getInt("code") == 0;
    }


    public FaceSearchResp search(String face64) throws IOException {
        HttpClient client = DigestHttpClient.createClient(this);
        String uri = "/v1/MEGBOX/search";
        String url = this.createUrl(uri);
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头 boundary边界不可重复，重复会导致提交失败
        String boundary = UUID.randomUUID().toString();
        httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        // 创建MultipartEntityBuilder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 设置字符编码
        builder.setCharset(StandardCharsets.UTF_8);
        // 模拟浏览器
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        // 设置边界
        builder.setBoundary(boundary);


        //创建arraylist集合
        JSONArray faceGroupList = new JSONArray().put(registerFaceLibName);


        builder.addPart("group", new StringBody(faceGroupList.toString(), ContentType.create("text/plain", Consts.UTF_8)));
        // 设置multipart/form-data流文件
        File file = createTempFile(face64, boundary);
        builder.addPart("image", new FileBody(file, "image/jpeg", "utf8"));

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpEntity respEntity = client.execute(httpPost).getEntity();
        String resp = EntityUtils.toString(respEntity);
        // System.out.println("对比度数据， resp = " + resp);
        JSONObject respJson = new JSONObject(resp);
        if (respJson.getInt("code") == 0) {
            JSONObject topJson = respJson.getJSONObject("data").getJSONObject("top1");
            double searchScore = topJson.getDouble("searchScore");
            String faceToken = topJson.getString("faceToken");
            String description = topJson.getString("description");
            String imageUrl = topJson.getString("imageUrl");
            FaceSearchResp searchResp = FaceSearchResp.builder()
                    .searchScore(searchScore)
                    .faceToken(faceToken)
                    .description(description)
                    .imageUrl(imageUrl)
                    .build();
            return searchResp;
        }

        return null;
    }

    public String getFaceImageId(String faceToken) throws IOException {
        HttpClient httpClient = DigestHttpClient.createClient(this);
        String url = createUrl("/v1/MEGBOX/faces/faceToken/" + faceToken);

        HttpGet httpGet = new HttpGet(url);
        HttpResponse resp = httpClient.execute(httpGet);
        String respEntity = null;
        try {
            respEntity = EntityUtils.toString(resp.getEntity(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("resp = " + respEntity);
        JSONObject respJson = new JSONObject(respEntity);
        if (respJson.getInt("code") != 0) {
            // throw new RunException("获取某一人脸信息与图片失败！！！");
//            System.out.println("获取某一人脸信息与图片失败！！！");
            return null;
        }
        JSONObject dataJson = respJson.getJSONObject("data");
        // 原始图片唯一ID，用于获取底库中原始照片
        return dataJson.getString("imageId");
    }

//
}