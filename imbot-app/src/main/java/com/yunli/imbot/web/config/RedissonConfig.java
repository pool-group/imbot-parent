//package com.yunli.imbot.web.config;
//
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
///**
// * Redisson 配置类
// *
// * @author k.y
// * @version Id: RedissonConfig.java, v 0.1 2018年11月08日 下午20:14 k.y Exp $
// */
//@Configuration
//public class RedissonConfig {
//
//    @Value("${spring.redis.host}")
//    private String host;
//
//    @Value("${spring.redis.port}")
//    private String port;
//
//    @Value("${spring.redis.password}")
//    private String password;
//
//    @Value("${spring.redis.database}")
//    private int database;
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Bean
//    public RedissonClient getRedisson(){
//
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://" + host + ":" + port).setDatabase(database);
//
//        return Redisson.create(config);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> stringSerializerRedisTemplate() {
//        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
//        redisTemplate.setKeySerializer(stringSerializer);
//        redisTemplate.setValueSerializer(stringSerializer);
//        redisTemplate.setHashKeySerializer(stringSerializer);
//        redisTemplate.setHashValueSerializer(stringSerializer);
//        return redisTemplate;
//    }
//
//}
