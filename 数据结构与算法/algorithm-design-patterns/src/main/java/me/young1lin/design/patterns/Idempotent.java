//package me.young1lin.design.patterns;
//
//import java.util.UUID;
//
///**
// * 幂等框架实现
// *
// * @author young1lin
// * @version 1.0
// * @date 2020/10/4 3:33 下午
// */
//public class Idempotence {
//    private JedisCluster jedisCluster;
//    public Idempotence(String redisClusterAddress, GenericObjectPoolConfig config) {
//        String[] addressArray= redisClusterAddress.split(";");
//        Set<HostAndPort> redisNodes = new HashSet<>();
//        for (String address : addressArray) {
//            String[] hostAndPort = address.split(":");
//            redisNodes.add(new HostAndPort(hostAndPort[0], Integer.valueOf(hostAndPort[1])));
//        }
//        this.jedisCluster = new JedisCluster(redisNodes, config);
//    }
//    public String genId() {
//        return UUID.randomUUID().toString();
//    }
//    public boolean saveIfAbsent(String idempotenceId) {
//        Long success = jedisCluster.setnx(idempotenceId, "1");
//        return success == 1;
//    }
//    public void delete(String idempotenceId) {
//        jedisCluster.del(idempotenceId);
//    }
//}
//
//
