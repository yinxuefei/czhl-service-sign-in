package com.gdczhl.saas.megbox;

import com.gdczhl.saas.config.MegBoxApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;

/**
 * 基础client
 */
public class DigestHttpClient {

    /**
     * 创建http客户端
     *
     * @param megBox 传入用户的账号和密码
     * @return
     */
    public static HttpClient createClient(MegBoxApi megBox) {
        return createClient(megBox.getUsername(), megBox.getPassword(), null, null);
    }

    /**
     * @param username:
     * @param password:
     * @param host:
     * @param port:
     * @return HttpClient
     * @author cuipengfei
     * @description TODO httpclient 4.3 后  ，生成认证的httpclient
     * @date 2022/6/22 18:33
     */
    public static HttpClient createClient(String username, String password, String host, Integer port) {
        //ip和端口为空
        //创建基础凭证摘要提供类
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        //设置凭证 (认证范围(根据端口和ip创建(ip,port), 用户密码凭据)
        credentialsProvider.setCredentials(new AuthScope(StringUtils.isBlank(host) ? AuthScope.ANY_HOST : host,
                        port == null ? AuthScope.ANY_PORT : port),
                new UsernamePasswordCredentials(username, password));
        //设置自定义凭证,然后构建客户端
        return HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
    }

    public static HttpClient httpClient() {
        return HttpClients.custom().build();
    }

}
