package tk.young1lin.rpc.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.young1lin.rpc.zookeeper.service.EventTypeDoService;
import tk.young1lin.rpc.zookeeper.service.impl.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 后面才说 zkClient 好用，这个做不了 Pub/Sub 模式，一直都是 None
 * @author young1lin
 * @version 1.0
 * @date 2020/7/29 3:24 下午
 */
public class ZookeeperSimpleDemo {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperSimpleDemo.class);

	private static ZooKeeper zooKeeper;

	static {
		String url = "150.223.200.246:2181";
		int sessionTimeOut = 3000;
		try {
//            zooKeeper = new ZooKeeper(url, sessionTimeOut, (watchedEvent) -> {
//              System.out.println(watchedEvent.toString());
//        });
			zooKeeper = new ZooKeeper(url, sessionTimeOut, new ZkWatcher(generatorEventTypeToDoMap()));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (zooKeeper == null) {
				System.exit(0);
			}
		}
	}

	private static Map<Watcher.Event.EventType, EventTypeDoService> generatorEventTypeToDoMap() {
		Map<Watcher.Event.EventType, EventTypeDoService> eventTypeToDoMap = new HashMap<>(5);
		eventTypeToDoMap.put(Watcher.Event.EventType.NodeDeleted, new DeletedEventTypeDoService());
		eventTypeToDoMap.put(Watcher.Event.EventType.NodeDataChanged, new DataChangedEventTypeDoService());
		eventTypeToDoMap.put(Watcher.Event.EventType.NodeCreated, new CreatedEventTypeDoService());
		eventTypeToDoMap.put(Watcher.Event.EventType.NodeChildrenChanged, new ChildrenChangedEventTypeDoService());
		eventTypeToDoMap.put(Watcher.Event.EventType.None, new NoneEventTypeDoService());
		return eventTypeToDoMap;
	}

	public static void main(String[] args) throws KeeperException, InterruptedException {
		String zkNodeName = "/root";
		String zkData = "root data";
		String subZkNodeName = "/root/child";
		String subZkData = "I'm child";
		int version = -1;
		// 创建 zkNode
		createZkNode(zkNodeName, zkData);
		// 删除 zkNode，这里执行了删除，那后面的操作就执行不了
		// deleteZkNode(zkNodeName, version);
		// 获取 zkNode 信息
		// getZkNodesInfo(subZkNodeName);
		// 添加子节点，在父节点必须存在
		// createZkNode(subZkNodeName, subZkData);
		// 判断节点是否存在
		// existZkNode(subZkNodeName);
		// watcher 的实现
	}

	/**
	 * 创建 zkNode 节点
	 *
	 * @param zkNodeName       zkNode名字
	 * @param data             包含的数据
	 * @param ids              权限方式
	 * @param zkCreateNodeType 节点类型
	 */
	private static void doCreateZkNode(String zkNodeName, String data, ArrayList<ACL> ids, CreateMode zkCreateNodeType) throws KeeperException, InterruptedException {
		String message = zooKeeper.create(zkNodeName, data.getBytes(), ids, zkCreateNodeType);
		logger.info("message:{}", message);
	}

	private static void createZkNode(String zkNodeName, String data) throws KeeperException, InterruptedException {
		doCreateZkNode(zkNodeName, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 删除 zkNode
	 *
	 * @param zkNodeName zkNode名称
	 * @param version    版本
	 */
	private static void deleteZkNode(String zkNodeName, int version) throws KeeperException, InterruptedException {
		zooKeeper.delete(zkNodeName, version);
	}

	private static void getZkNodesInfo(String zkNodeName) throws KeeperException, InterruptedException {
		Stat stat = new Stat();
		byte[] data = zooKeeper.getData(zkNodeName, false, stat);
		String dataStr = Arrays.toString(data);
		logger.info("stat : {},data : {}", stat.toString(), dataStr);
		System.out.printf("stat : %s,data : %s", stat.toString(), dataStr);

	}

	/**
	 * 判断 zkNode 是否存在
	 *
	 * @param zkNodeName zkNode 名称 如 "/root"
	 * @return boolean 存在 true，不存在 false
	 */
	private static boolean existZkNode(String zkNodeName) throws KeeperException, InterruptedException {
		Stat stat = zooKeeper.exists(zkNodeName, false);
		if (stat == null) {
			logger.info("{}不存在", zkNodeName);
			return false;
		}
		logger.info("{}存在", zkNodeName);
		return true;
	}

}
