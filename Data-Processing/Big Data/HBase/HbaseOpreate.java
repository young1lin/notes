import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseTable {
    // 声明静态配置
    private static Configuration conf = HBaseConfiguration.create();

    // 创建表（tableName 表名; family 列族列表）
    public static void createTable(String tableName, String[] familys)
            throws IOException{
        HBaseAdmin admin = new HBaseAdmin(conf);
        if (admin.tableExists(tableName)){
            System.out.println(tableName+" already exists!");
        }
        else {
            HTableDescriptor descr = new HTableDescriptor(TableName.valueOf(tableName));
            for (String family:familys) {
                descr.addFamily(new HColumnDescriptor(family)); //添加列族
            }
            admin.createTable(descr); //建表
            System.out.println(tableName+" created successfully!");
        }
    }

    //插入数据（rowKey rowKey；tableName 表名；family 列族；qualifier 限定名；value 值）
    public static void addData(String tableName, String rowKey, String familyName, String columnName, String value)
            throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));// HTable负责跟记录相关的操作如增删改查等//
        Put put = new Put(Bytes.toBytes(rowKey));// 设置rowkey
        put.add(Bytes.toBytes(familyName), Bytes.toBytes(columnName), Bytes.toBytes(value));
        table.put(put);
        System.out.println("Add data successfully!rowKey:"+rowKey+", column:"+family+":"+column+", cell:"+value);
    }

    //遍历查询hbase表（tableName 表名）
    public static void getResultScann(String tableName) throws IOException {
        Scan scan = new Scan();
        ResultScanner rs = null;
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        try {
            rs = table.getScanner(scan);
            for (Result r : rs) {
                for (KeyValue kv : r.list()) {
                    System.out.println("row:" + Bytes.toString(kv.getRow()));
                    System.out.println("family:" + Bytes.toString(kv.getFamily()));
                    System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
                    System.out.println("value:" + Bytes.toString(kv.getValue()));
                    System.out.println("timestamp:" + kv.getTimestamp());
                    System.out.println("-------------------------------------------");
                }
            }
        } finally {
            rs.close();
        }
    }


    //查询表中的某一列（
    public static void getResultByColumn(String tableName, String rowKey, String familyName, String columnName)
            throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName)); // 获取指定列族和列修饰符对应的列
        Result result = table.get(get);
        for (KeyValue kv : result.list()) {
            System.out.println("family:" + Bytes.toString(kv.getFamily()));
            System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
            System.out.println("value:" + Bytes.toString(kv.getValue()));
            System.out.println("Timestamp:" + kv.getTimestamp());
            System.out.println("-------------------------------------------");
        }
    }

    //更新表中的某一列（tableName 表名；rowKey rowKey；familyName 列族名；columnName 列名；value 更新后的值）
    public static void updateTable(String tableName, String rowKey,
                                   String familyName, String columnName, String value)
            throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Put put = new Put(Bytes.toBytes(rowKey));
        put.add(Bytes.toBytes(familyName), Bytes.toBytes(columnName),
                Bytes.toBytes(value));
        table.put(put);
        System.out.println("update table Success!");
    }

    //删除指定单元格
    public static void deleteColumn(String tableName, String rowKey,
                                    String familyName, String columnName) throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Delete deleteColumn = new Delete(Bytes.toBytes(rowKey));
        deleteColumn.deleteColumns(Bytes.toBytes(familyName),
                Bytes.toBytes(columnName));
        table.delete(deleteColumn);
        System.out.println("rowkey:"+rowKey+",column:"+family+":"+column+" deleted!");
    }

    //删除指定的行
    public static void deleteAllColumn(String tableName, String rowKey)
            throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Delete deleteAll = new Delete(Bytes.toBytes(rowKey));
        table.delete(deleteAll);
        System.out.println("rowkey:"+rowKey+" are all deleted!");
    }

    //删除表（tableName 表名）
    public static void deleteTable(String tableName) throws IOException {
        HBaseAdmin admin = new HBaseAdmin(conf);
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
        System.out.println(tableName + " is deleted!");
    }

    public static void main(String[] args) throws Exception {
        // 创建表
        String tableName = "test";
        String[] family = { "f1", "f2" };
        creatTable(tableName, family);
        // 为表插入数据
        String[] rowKey = {"r1", "r2"};
        String[] columnName = { "c1", "c2", "c3" };
        String[] value = {
            "value1", "value2", "value3", "value4", "value5", "value6",
        };
        addData(tableName,rowKey[0],family[0],columnName[0],value[0]);
        addData(tableName,rowKey[0],family[0],columnName[1],value[1]);
        addData(tableName,rowKey[0],family[1],columnName[2],value[2]);
        addData(tableName,rowKey[1],family[0],columnName[0],value[3]);
        addData(tableName,rowKey[1],family[0],columnName[1],value[4]);
        addData(tableName,rowKey[1],family[1],columnName[2],value[5]);
        // 扫描整张表
        getResultScann(tableName);
        // 更新指定单元格的值
        updateTable(tableName, rowKey[0], family[0], columnName[0], "update value");
        // 查询刚更新的列的值
        getResultByColumn(tableName, rowKey[0], family[0], columnName[0]);
        // 删除一列
        deleteColumn(tableName, rowKey[0], family[0], columnName[1]);
        // 再次扫描全表
        getResultScann(tableName);
        // 删除整行数据
        deleteAllColumn(tableName, rowKey[0]);
        // 再次扫描全表
        getResultScann(tableName);
        // 删除表
        deleteTable(tableName);
    }
}