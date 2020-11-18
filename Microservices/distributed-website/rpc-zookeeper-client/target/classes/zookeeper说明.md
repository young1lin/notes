| 节点类型 | 说明 |
| :--- | :--- |
| PERSISTENT | 持久节点，该节点在客户端断开连接后不会被删除 |
| EPHEMERAL | 临时节点，该节点在客户端断开连接后会被删除|
| PERSISTENT_SEQUENTIAL | 持久节点，该节点在客户端断开连接后不会被删除，并在其名下附加一个单调递增数 |
| EPHEMERAL_SEQUENTIAL | 临时节点，该节点在客户端断开连接后会被删除，并在其名下附加一个单调递增数 |


| 节点状态变化 | 说明 |
| :--- | :--- |
| NodeDeleted  | 节点删除 |
| None | 无改变 |
| NodeCreated | 创建节点 |
| NodeDataChanged | 临节点数据改变 |
| NodeChildrenChanged | 修改节点的子节点 |
