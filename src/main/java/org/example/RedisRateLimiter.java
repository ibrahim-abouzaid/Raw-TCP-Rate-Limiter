package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

public class RedisRateLimiter {
    private final JedisPool jedisPool;
    private static final int MAX_REQUESTS = 10;
    private static final int WINDOW_SECONDS = 60;

    // Lua script to atomically INCR and EXPIRE if new
    //if we use normal execution like redis.call('INCR', KEYS[1])
    //may the redis crash and never set the Expiration time and it will increment untill it will block this user forever
    //because the request limit did not reset
        private static final String LUA_SCRIPT =
                "local current = redis.call('INCR', KEYS[1]) " +
                        "if current == 1 then " +
                        "   redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
                        "end " +
                        "return current";

    public RedisRateLimiter(JedisPool pool) {
        this.jedisPool = pool;
    }

    public boolean isAllowed(String clientId) {
        String redisKey = "rate_limit:" + clientId;

        try (Jedis jedis = jedisPool.getResource()) {

            //to avoid the small race condition where Redis crashes between INCR and EXPIRE,
            // I use a Lua script to make it fully atomic
            long count = (Long) jedis.eval(LUA_SCRIPT, 1, redisKey, String.valueOf(WINDOW_SECONDS));


//            long count = jedis.incr(redisKey);
//            if (count == 1) {
//                jedis.expire(redisKey, WINDOW_SECONDS);
//            }

            return count <= MAX_REQUESTS;
        }catch (Exception e){
            System.err.println("Redis error: " + e.getMessage());
            return false; // deny request
        }
    }
}
