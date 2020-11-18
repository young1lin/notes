package tk.young1lin.rpc.zookeeper;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.Arrays;
import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/29 5:02 下午
 */
public class ZkClientDemo {

    private static final ZkClient ZK_CLIENT;

    static {
        String zkServer = "150.223.200.246:2181";
        ZK_CLIENT = new ZkClient(zkServer);
    }

    public static void main(String[] args) {
        String path = "/zk-client-root";
        //创建持久化节点
        ZK_CLIENT.createPersistent(path);

        //创建子节点
        ZK_CLIENT.create(path + "/child", "child node", CreateMode.EPHEMERAL);

        // 获得子节点
        List<String> children = ZK_CLIENT.getChildren(path);
        String childrenStr = Arrays.toString(children.toArray());
        System.out.println(childrenStr);

        //获得子节点个数
        int childCount = ZK_CLIENT.countChildren(path);
        System.out.printf("childCount: %d ", childCount);

        // 判断节点是否存在
        ZK_CLIENT.exists(path);

        // 写入数据
        ZK_CLIENT.writeData(path + "/child", "hello !!");

        // 获得节点数据
        Object data = ZK_CLIENT.readData(path + "/child");
        System.out.printf("data : %s", data);

        // 删除节点
        ZK_CLIENT.delete(path + "/child");

        while (true) {
            ZK_CLIENT.subscribeChildChanges(path, (s, list) -> {
                System.out.printf("s : %s  x c, list : %s", s, Arrays.toString(list.toArray()));
            });
            ZK_CLIENT.subscribeDataChanges(path, new IZkDataListener() {
                @Override
                public void handleDataChange(String s, Object o) throws Exception {
                    System.out.printf("s : %s,o : %s", s, String.valueOf(o));
                }

                @Override
                public void handleDataDeleted(String s) throws Exception {
                    System.out.printf("s : %s", s);
                }
            });
 /*           zkClient.subscribeStateChanges(path, new IZkStateListener() {
                @Override
                public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {

                }

                @Override
                public void handleNewSession() throws Exception {

                }

                @Override
                public void handleSessionEstablishmentError(Throwable throwable) throws Exception {

                }
            });*/

        }
    }

}
