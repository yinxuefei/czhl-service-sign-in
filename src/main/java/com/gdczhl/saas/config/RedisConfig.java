package com.gdczhl.saas.config;

//@Configuration
//@EnableRedisRepositories
public class RedisConfig {

//    @Value("${spring.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.redis.port}")
//    private String redisPort;
//
//    @Value("${spring.redis.timeout}")
//    private String redistimeout;
//
//    @Value("${spring.redis.password}")
//    private String redisPassword;


//    @Bean
//    public RedissonClient redissonClient(){
//        Config conf = new Config();
//        //单节点模式
//        SingleServerConfig singleServerConfig = conf.useSingleServer();
//        //设置连接地址：redis://127.0.0.1:6379
//        singleServerConfig.setAddress("redis://" + redisHost  + ":"+ redisPort);
//        //设置连接密码
//        if (!StringUtils.isBlank(redisPassword)) {
//            singleServerConfig.setPassword(redisPassword);
//        }
//        //使用json序列化方式
//        Codec codec = new StringCodec();
//        conf.setCodec(codec);
//        RedissonClient redissonClient = Redisson.create(conf);
//        return redissonClient;
//    }


}
