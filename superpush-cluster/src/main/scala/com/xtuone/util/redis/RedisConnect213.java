package com.xtuone.util.redis;

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.ConfigFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class RedisConnect213 {
	/**/
//	private static String HOST = "112.124.105.13";
	private static String HOST = "10.161.187.112";
//	private static String HOST = "192.168.0.36";
//	private static String HOST = "192.168.0.225";
	
	/**/

//	private static int PORT_MASTER = 9420;
//	private static int PORT_MASTER = 6379;
	private static int PORT_MASTER = 6400;

	private static int PORT_SLAVE = 6380;
	/**/
	private static int MAX_ACTIVE = 10000;
	/**/
	private static int MAX_IDLE = 10000;
	/* 最大等待时间5s */
	private static int MAX_WAIT = 10000;
	/* 设置获取连接超时时间 5s */
	private static int TIMEOUT = 10000;
	/**/
	private static boolean TEST_ON_BORROW = true;
	
	private static ShardedJedisPool jedisPool = null;
	
	private static ShardedJedis resource;

	static {

		HOST = ConfigFactory.load().getString("redis.host");
		PORT_MASTER = ConfigFactory.load().getInt("redis.port");
		
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxActive(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWait(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			config.setMaxWait(1000);
			List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
			shards.add(new JedisShardInfo(HOST, PORT_MASTER, TIMEOUT));
			// shards.add(new JedisShardInfo(HOST, PORT_SLAVE, TIMEOUT));
			setJedisPool(new ShardedJedisPool(config, shards));
		} catch (Exception e) {
			System.out.println("初始化Redis缓存连接池失败.....");
		}
	}

	public synchronized static ShardedJedis getJedis() {
		try {
			if(null == resource) {
				if (getJedisPool() != null) {
					resource = getJedisPool().getResource();
					return resource;
				} else {
					return null;
				}
			} else {
				return resource;
			}
			
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	public static void returnResource(final ShardedJedis jedis) {
		if (jedis != null) {
			getJedisPool().returnResource(jedis);
		}
	}

	public static ShardedJedisPool getJedisPool() {
		return jedisPool;
	}

	public static void setJedisPool(ShardedJedisPool jedisPool) {
		RedisConnect213.jedisPool = jedisPool;
	}
}
