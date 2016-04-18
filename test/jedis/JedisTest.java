package jedis;

import com.rubic.sso.util.JedisUtils;
import redis.clients.jedis.Jedis;

/**
 * Created by Mian on 2016/3/25.
 */
public class JedisTest {

    public static Jedis createJedis() {
        Jedis jedis = new Jedis("172.22.147.3");
        return jedis;
    }

    public static Jedis createJedis(String host, int port) {
        Jedis jedis = new Jedis(host, port);
        return jedis;
    }

//    public static void main(String[] args) {
//        Jedis jedis = createJedis();
//        jedis.set("hello","world");
//        String value = jedis.get("hello");
//        System.out.println(value);
//    }


    public static void main(String[] args) {

        Jedis jedis01 = JedisUtils.getJedis();

        for (int i = 0; i < 50; i++) {
            jedis01.set(i+"",i+"");
            jedis01.expire(i+"",1);
        }
        Jedis jedis02 = JedisUtils.getJedis();
        String value = null;
        for (int i = 0; i < 50; i++) {
            value = jedis02.get(i+"");
            System.out.println(value);
        }

//        JedisUtils.returnResource(jedis01);
//        JedisUtils.returnResource(jedis02);
    }
}
