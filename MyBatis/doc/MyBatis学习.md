# JDBC 核心 API

java.sql.Array  接口

java.sql.Blob 接口

java.sql.Clob 接口

java.sql.Date class extends java.util.Date

java.sql.NClob  接口

java.sql.Struct 接口

java.sql.Time class extends java.util.Date

java.sql.TimeStamp

java.sql.SQLXML 接口

java.sql.Ref 接口

java.sql.RowId 接口

java.sql.SQLOutput 接口

java.sql.Data 接口

java.sql.SQLInput 接口



DatabaseMetaData接口

获取默认的事务隔离级别

getDefualtTransactionIsolation();



ResultSet 有三种类型，有一种可以在已经获得 ResulSet 对象后，继续可以操作数据库，改变数据

ResultSet对象关闭后，不会关闭由ResultSet对象创建的Blob、Clob、NClob或SQLXML对象，除非调用这些对象的free()方法。



JDBC API 中的事务管理符合 SQL：2003规范，主要（下面介绍的是单连接的事务）包含：

自动提交模式

事务隔离级别

保存点

## 脏读　

这种情况发生在事务中允许读取未提交的数据。例如，A事务修改了一条数据，但是未提交修改，此时A事务对数据的修改对其他事务是可见的，B事务中能够读取A事务未提交的修改。一旦A事务回滚，B事务中读取的就是不正确的数据。

## 不可重复读　

这种情况发生在如下场景：

（1）A事务中读取一行数据。

（2）B事务中修改了该行数据。

（3）A事务中再次读取该行数据将得到不同的结果。

## 可重复读

一个事务在执行过程中可以看到其他事务已经提交的新插入的记录（读已经提交的，其实是读早于本事务开始且已经提交的），但是不能看到其他事务对已有记录的更新（即晚于本事务开始的），并且，该事务不要求与其他事务是“可串行化”的。

## 幻读

　这种情况发生在如下场景：

（1）A事务中通过WHERE条件读取若干行。

（2）B事务中插入了符合条件的若干条数据。

（3）A事务中通过相同的条件再次读取数据时将会读取到B事务中插入的数据。

JDBC遵循SQL:2003规范，定义了4种事务隔离级别，另外增加了一种TRANSACTION_NONE，表示不支持事务。这几种事务隔离级别如下。

TRANSACTION_NONE：表示驱动不支持事务，这意味着它是不兼容JDBC规范的驱动程序。TRANSACTION_READ_UNCOMMITTED：允许事务读取未提交更改的数据，这意味着可能会出现脏读、不可重复读、幻读等现象。

TRANSACTION_READ_COMMITTED：表示在事务中进行的任何数据更改，在提交之前对其他事务是不可见的。这样可以防止脏读，但是不能解决不可重复读和幻读的问题。

TRANSACTION_REPEATABLE_READ：该事务隔离级别能够解决脏读和不可重复读问题，但是不能解决幻读问题。T

RANSACTION_SERIALIZABLE：该事务隔离级别下，所有事务串行执行，能够有效解决脏读、不可重复读和幻读问题，但是并发效率较低

# PreparedStatement

PreparedStatement（接口） 对象已经预编译过，继承了 Statement 所有的功能。多次执行的 SQL 语句经常创建为 PreparedStatement 对象，以提高效率。

# Executor


![image.png](https://i.loli.net/2020/10/16/BartMfIHTilSbZX.png)


接口，提供通用操作

## BaseExecutor

BaseExecutor 中定义的的执行流程及通用的处理逻辑，具体的方法由子类来实现。

## SimpleExecutor

完成基本的增删改查

## ResuseExecutor

对 JDBC 中的 Statement对象做了缓存，当执行相同的 SQL 

## BatchExecutor

当MyBatis开启了二级缓存功能时，会使用CachingExecutor对SimpleExecutor、ResueExecutor、BatchExecutor进行装饰，为查询操作增加二级缓存功能，这是装饰器模式的应用

# MappedStatement详解

MyBatis通过MappedStatement描述<select|update|insert|delete>或者@Select、@Update等注解配置的SQL信息

## Select

```xml
<select id="getUserById"
        parameterType="int"
        parameterMap="deprecated"
        resultType="userResultMap"
        flushCache="false"
        useCache="true"
        timeout="10000"
        fetchSize="256"
        statementType="PREPARED"
        resultSetType="FORWARD_ONLY">
    SELECT * 
    	FROM sys_user
    LIMIT 10
</select>
```

+ id：在 namespace 中唯一的标识符，可以被用来引用这条配置信息。

+ parameterType：用于指定这条语句的参数类的完全限定名或者别名，这个属性是可选的，MyBatis 能够根据 Mapper 接口方法中的参数类型推断出传入语句的类型。

+ parameterMap：引用通过`<paremeterMap>`标签定义的参数映射，该**<font style="color:red">属性已经废弃</font>**

+ <font style="color:red">resultType</font>：从这条语句中返回的期望类型的类的全限定名或别名。如果结果是集合类型，则 resultType 属性应该是指定集合中可以包含的类型，而不是集合本身。即返回 `List<SysUser>`，应该写成 `xxx.xxx.SysUser`或者配置 SysUser 的别名。

+ <font style="color:red">resultMap</font>：用于引用通过 `<resultMap>` 标签配置的实体属性与数据库字段之间建立的结果集的映射。resultMap 和 resultType 属性不能同时使用。

+ flushCache：用于控制是否刷新缓存。如果将其设置为 true，则任何时候只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值为 false。
+ <font style="color:red">useCache</font>：是否使用二级缓存。如果设置为 true，则会导致本条语句的结果被缓存在 Mybatis 的二级缓存中，对应 `<select>`标签，该属性的默认值为 true。
+ timeout：驱动程序等待数据库返回请求结果的秒数，超时将会抛出异常。
+ <font style="color:red">fetchSize</font>：用于设置 JDBC 中 Statement 对象的 fetchSize 属性，该属性用于指定 SQL 执行后返回的最大行数。
+ statementType：参数可选值为 STATEMENT、PREPARED 或 CALLABLE。默认值为 PREPARED
+ <font style="color:red">resultSetType</font>：参数值可选值为 FORWARD_ONLY、SCROLL_SENSITIVE 或 SCROLL_INSENETIVE。默认未设置，由 JDBC 驱动决定。
+ databaseId：如果配置了 databaseIdProvider，Mybatis 会加载所有不带 databaseId 或匹配当前 databaseId 的语句。
+ <font style="color:red">resultOrdered</font>：这个设置仅针对嵌套结果包含在一起或分组在一起，这样的话，当返回一个主结果行的时候，就不会发生前面结果集引用的情况。这就使得在获取嵌套结果集的时候，不至于导致内存不够用，默认值为 false。
+ <font style="color:red">resultSets</font>：这个设置仅对多结果集的情况适用，它将列出语句执行后返回的结果集并每个结果集给一个名称，名称使用逗号分隔。
+ lang：该属性用于指定 LanguageDriver 实现，MyBatis 中的 LanguageDriver 用于解析 `<select|updaet|insert|delete>`标签中的 SQL 语句，生成 SqlSource 对象。

红色为 select 独有的

## Insert

```xml
<insert id="insertUser"
        parameterType="me.young1lin.SysUser"
        flushCache="true"
        statementType="PREPARED"
        keyProperty="id"
        keyColumn="id"
        useGeneratedKeys="true"
        timeout="20"
        >
	INSERT INTO TABLE 
    	sys_user(name,age)
    values
    	("张三",24);
</insert>
```

**<font style="color:red">useGeneratedKeys</font>：**设置值为 true 时，Mybatis 使用 JDBC Statement 对象的 getGeneratedKeys() 方法来取出由数据库内部生成的键值，例如 MySQL 自增主键。

**<font style="color:red">keyProperty</font>：**

<font style="color:red">仅在 update 和 insert 标签有用</font>用于将数据库自增主键或者`<insert>`标签中 \<selectKey\>标签返回的值填充到实体的属性中，如果有多个属性，则使用逗号分隔

<font style="color:red">**keyColumn:**</font> <font style="color:red">仅在 update 和 insert 标签有用</font>，通过生成的键值设置表中的列名，这个设置仅在某些数据库例如PG中时必需的，当主键列不是表中的第一列时需要设置，如果有多个字段，则使用逗号分隔。

MappedStatement 类通过下面这些属性保存`<select|update|insert|delete>`标签的属性信息。**是这样的，Mybatis 的代码没有注释，有也是废注释。**

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.ibatis.mapping;

/**
 * @author Clinton Begin
 */
public final class MappedStatement {
    // Mapper 资源路径
    private String resource;
    // Configuration 对象的引用
    private Configuration configuration;
    private String id;
    private Integer fetchSize;
    private Integer timeout;
    private StatementType statementType;
    private ResultSetType resultSetType;
    // 解析 SQL 语句生成的 SqlSource 实例
    private SqlSource sqlSource;
    // 二级缓存实例
    private Cache cache;
    private ParameterMap parameterMap;
    private List<ResultMap> resultMaps;
    private boolean flushCacheRequired;
    private boolean useCache;
    private boolean resultOrdered;
    private SqlCommandType sqlCommandType;
    // 主键生成策略
    private KeyGenerator keyGenerator;
    private String[] keyProperties;
    private String[] keyColumns;
    // 是否有嵌套的 ResultMap
    private boolean hasNestedResultMaps;
    private String databaseId;
    // 输出日志
    private Log statementLog;
    private LanguageDriver lang;
    private String[] resultSets;

    MappedStatement() {
    }
}
```

cache：二级缓存实例，根据 Mapper 中的 `<cache>`标签配置信息创建对应的 Cache 实现。

SqlSource：解析`<select|update|insert|delete>`，将 SQL 语句配置信息解析为 SqlSource 对象。

resource：Mapper 配置文件路径

configuration：Configuration对象的引用，方便获取MyBatis配置信息及TypeHandler、TypeAlias等信息。

keyGenerator：主键生成策略，默认为Jdbc3KeyGenerator，即数据库自增主键。当配置了`<selectKey>`时，使用SelectKeyGenerator生成主键。

hasNestedResultMaps：`<select>`标签中通过resultMap属性指定ResultMap是不是嵌套的ResultMap。statementLog：用于输出日志。

# StatementHandler

```java
/**
 *    Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor.statement;

/**
 * @author Clinton Begin
 */
public interface StatementHandler {

  Statement prepare(Connection connection, Integer transactionTimeout)
      throws SQLException;

  void parameterize(Statement statement)
      throws SQLException;

  void batch(Statement statement)
      throws SQLException;

  int update(Statement statement)
      throws SQLException;

  <E> List<E> query(Statement statement, ResultHandler resultHandler)
      throws SQLException;

  <E> Cursor<E> queryCursor(Statement statement)
      throws SQLException;

  BoundSql getBoundSql();

  ParameterHandler getParameterHandler();

}
```

prepare：该方法用于创建JDBC Statement对象，并完成Statement对象的属性设置。

parameterize：该方法使用MyBatis中的ParameterHandler组件为PreparedStatement和CallableStatement参数占位符设置值。

batch：将SQL命令添加到批处量执行列表中。

update：调用Statement对象的execute()方法执行更新语句，例如UPDATE、INSERT、DELETE语句。

query：执行查询语句，并使用ResultSetHandler处理查询结果集。queryCursor：带游标的查询，返回Cursor对象，能够通过Iterator动态地从数据库中加载数据，适用于查询数据量较大的情况，避免将所有数据加载到内存中。

getBoundSql：获取Mapper中配置的SQL信息，BoundSql封装了动态SQL解析后的SQL文本和参数映射信息。

getParameterHandler：获取ParameterHandler实例。

![image-20201020210103638](/Users/linyoung/Library/Application Support/typora-user-images/image-20201020210103638.png)

BaseStatementHandler是一个抽象类，封装了通用的处理逻辑及方法执行流程，具体方法的实现由子类完成，这里使用到了设计模式中的模板方法模式。

SimpleStatementHandler继承至BaseStatementHandler，封装了对JDBCStatement对象的操作，PreparedStatementHandler封装了对JDBCPreparedStatement对象的操作，而CallableStatementHandler则封装了对JDBCCallableStatement对象的操作。

RoutingStatementHandler会根据Mapper配置中的statementType属性（取值为STATEMENT、PREPARED或CALLABLE）创建对应的StatementHandler实现。

# TypeHandler

为了解决两个问题，一进一出，都要类型

1. PrepareStatement#setXXX 方法将值设置进去。
2. 执行 SQL 语句获取 ResultSet 对象后，需要调用 ResultSet 对象的 getXXX() 方法获取字段值。

```java
/**
 * @author Clinton Begin
 */
public interface TypeHandler<T> {
  // 进
  void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

  /**
   * Gets the result.
   *
   * @param rs
   *          the rs
   * @param columnName
   *          Colunm name, when configuration <code>useColumnLabel</code> is <code>false</code>
   * @return the result
   * @throws SQLException
   *           the SQL exception
   */
  // 出
  T getResult(ResultSet rs, String columnName) throws SQLException;

  T getResult(ResultSet rs, int columnIndex) throws SQLException;

  T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
```
**判空，捕捉异常，缝合怪罢了。不干任何实际的活。**

```java
/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.session.Configuration;

/**
 * The base {@link TypeHandler} for references a generic type.
 * <p>
 * Important: Since 3.5.0, This class never call the {@link ResultSet#wasNull()} and
 * {@link CallableStatement#wasNull()} method for handling the SQL {@code NULL} value.
 * In other words, {@code null} value handling should be performed on subclass.
 * </p>
 *
 * @author Clinton Begin
 * @author Simone Tripodi
 * @author Kzuki Shimizu
 */
public abstract class BaseTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {

  /**
   * @deprecated Since 3.5.0 - See https://github.com/mybatis/mybatis-3/issues/1203. This field will remove future.
   */
  @Deprecated
  protected Configuration configuration;

  /**
   * Sets the configuration.
   *
   * @param c
   *          the new configuration
   * @deprecated Since 3.5.0 - See https://github.com/mybatis/mybatis-3/issues/1203. This property will remove future.
   */
  @Deprecated
  public void setConfiguration(Configuration c) {
    this.configuration = c;
  }

  @Override
  public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
    if (parameter == null) {
      if (jdbcType == null) {
        throw new TypeException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
      }
      try {
        ps.setNull(i, jdbcType.TYPE_CODE);
      } catch (SQLException e) {
        throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
              + "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. "
              + "Cause: " + e, e);
      }
    } else {
      try {
        setNonNullParameter(ps, i, parameter, jdbcType);
      } catch (Exception e) {
        throw new TypeException("Error setting non null for parameter #" + i + " with JdbcType " + jdbcType + " . "
              + "Try setting a different JdbcType for this parameter or a different configuration property. "
              + "Cause: " + e, e);
      }
    }
  }

  @Override
  public T getResult(ResultSet rs, String columnName) throws SQLException {
    try {
      return getNullableResult(rs, columnName);
    } catch (Exception e) {
      throw new ResultMapException("Error attempting to get column '" + columnName + "' from result set.  Cause: " + e, e);
    }
  }

  @Override
  public T getResult(ResultSet rs, int columnIndex) throws SQLException {
    try {
      return getNullableResult(rs, columnIndex);
    } catch (Exception e) {
      throw new ResultMapException("Error attempting to get column #" + columnIndex + " from result set.  Cause: " + e, e);
    }
  }

  @Override
  public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
    try {
      return getNullableResult(cs, columnIndex);
    } catch (Exception e) {
      throw new ResultMapException("Error attempting to get column #" + columnIndex + " from callable statement.  Cause: " + e, e);
    }
  }

  public abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

  /**
   * Gets the nullable result.
   *
   * @param rs
   *          the rs
   * @param columnName
   *          Colunm name, when configuration <code>useColumnLabel</code> is <code>false</code>
   * @return the nullable result
   * @throws SQLException
   *           the SQL exception
   */
  public abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

  public abstract T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

  public abstract T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException;

}
```

其子类有各种类型，实际干活的也其实很简单。

```java
/**
 * @author Clinton Begin
 */
public class StringTypeHandler extends BaseTypeHandler<String> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setString(i, parameter);
  }

  @Override
  public String getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    return rs.getString(columnName);
  }

  @Override
  public String getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    return rs.getString(columnIndex);
  }

  @Override
  public String getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return cs.getString(columnIndex);
  }
}
```

![image-20201020211608295](/Users/linyoung/Library/Application Support/typora-user-images/image-20201020211608295.png)

通过 TypeHandlerRegistry 建立 JDBC 类型、Java 类型于 TypeHandler 之间的映射关系。依旧是少得可怜的注释。

```java
/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.type;

/**
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public final class TypeHandlerRegistry {

  private final Map<JdbcType, TypeHandler<?>>  jdbcTypeHandlerMap = new EnumMap<>(JdbcType.class);
  private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new ConcurrentHashMap<>();
  private final TypeHandler<Object> unknownTypeHandler;
  private final Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = new HashMap<>();

  private static final Map<JdbcType, TypeHandler<?>> NULL_TYPE_HANDLER_MAP = Collections.emptyMap();

  private Class<? extends TypeHandler> defaultEnumTypeHandler = EnumTypeHandler.class;

  /**
   * The default constructor.
   */
  public TypeHandlerRegistry() {
    this(new Configuration());
  }

  /**
   * The constructor that pass the MyBatis configuration.
   *
   * @param configuration a MyBatis configuration
   * @since 3.5.4
   */
  public TypeHandlerRegistry(Configuration configuration) {
    this.unknownTypeHandler = new UnknownTypeHandler(configuration);

    register(Boolean.class, new BooleanTypeHandler());
    register(boolean.class, new BooleanTypeHandler());
    register(JdbcType.BOOLEAN, new BooleanTypeHandler());
    register(JdbcType.BIT, new BooleanTypeHandler());

    register(Byte.class, new ByteTypeHandler());
    register(byte.class, new ByteTypeHandler());
    register(JdbcType.TINYINT, new ByteTypeHandler());

    register(Short.class, new ShortTypeHandler());
    register(short.class, new ShortTypeHandler());
    register(JdbcType.SMALLINT, new ShortTypeHandler());

    register(Integer.class, new IntegerTypeHandler());
    register(int.class, new IntegerTypeHandler());
    register(JdbcType.INTEGER, new IntegerTypeHandler());

    register(Long.class, new LongTypeHandler());
    register(long.class, new LongTypeHandler());

    register(Float.class, new FloatTypeHandler());
    register(float.class, new FloatTypeHandler());
    register(JdbcType.FLOAT, new FloatTypeHandler());

    register(Double.class, new DoubleTypeHandler());
    register(double.class, new DoubleTypeHandler());
    register(JdbcType.DOUBLE, new DoubleTypeHandler());

    register(Reader.class, new ClobReaderTypeHandler());
    register(String.class, new StringTypeHandler());
    register(String.class, JdbcType.CHAR, new StringTypeHandler());
    register(String.class, JdbcType.CLOB, new ClobTypeHandler());
    register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
    register(String.class, JdbcType.LONGVARCHAR, new StringTypeHandler());
    register(String.class, JdbcType.NVARCHAR, new NStringTypeHandler());
    register(String.class, JdbcType.NCHAR, new NStringTypeHandler());
    register(String.class, JdbcType.NCLOB, new NClobTypeHandler());
    register(JdbcType.CHAR, new StringTypeHandler());
    register(JdbcType.VARCHAR, new StringTypeHandler());
    register(JdbcType.CLOB, new ClobTypeHandler());
    register(JdbcType.LONGVARCHAR, new StringTypeHandler());
    register(JdbcType.NVARCHAR, new NStringTypeHandler());
    register(JdbcType.NCHAR, new NStringTypeHandler());
    register(JdbcType.NCLOB, new NClobTypeHandler());

    register(Object.class, JdbcType.ARRAY, new ArrayTypeHandler());
    register(JdbcType.ARRAY, new ArrayTypeHandler());

    register(BigInteger.class, new BigIntegerTypeHandler());
    register(JdbcType.BIGINT, new LongTypeHandler());

    register(BigDecimal.class, new BigDecimalTypeHandler());
    register(JdbcType.REAL, new BigDecimalTypeHandler());
    register(JdbcType.DECIMAL, new BigDecimalTypeHandler());
    register(JdbcType.NUMERIC, new BigDecimalTypeHandler());

    register(InputStream.class, new BlobInputStreamTypeHandler());
    register(Byte[].class, new ByteObjectArrayTypeHandler());
    register(Byte[].class, JdbcType.BLOB, new BlobByteObjectArrayTypeHandler());
    register(Byte[].class, JdbcType.LONGVARBINARY, new BlobByteObjectArrayTypeHandler());
    register(byte[].class, new ByteArrayTypeHandler());
    register(byte[].class, JdbcType.BLOB, new BlobTypeHandler());
    register(byte[].class, JdbcType.LONGVARBINARY, new BlobTypeHandler());
    register(JdbcType.LONGVARBINARY, new BlobTypeHandler());
    register(JdbcType.BLOB, new BlobTypeHandler());

    register(Object.class, unknownTypeHandler);
    register(Object.class, JdbcType.OTHER, unknownTypeHandler);
    register(JdbcType.OTHER, unknownTypeHandler);

    register(Date.class, new DateTypeHandler());
    register(Date.class, JdbcType.DATE, new DateOnlyTypeHandler());
    register(Date.class, JdbcType.TIME, new TimeOnlyTypeHandler());
    register(JdbcType.TIMESTAMP, new DateTypeHandler());
    register(JdbcType.DATE, new DateOnlyTypeHandler());
    register(JdbcType.TIME, new TimeOnlyTypeHandler());

    register(java.sql.Date.class, new SqlDateTypeHandler());
    register(java.sql.Time.class, new SqlTimeTypeHandler());
    register(java.sql.Timestamp.class, new SqlTimestampTypeHandler());

    register(String.class, JdbcType.SQLXML, new SqlxmlTypeHandler());

    register(Instant.class, new InstantTypeHandler());
    register(LocalDateTime.class, new LocalDateTimeTypeHandler());
    register(LocalDate.class, new LocalDateTypeHandler());
    register(LocalTime.class, new LocalTimeTypeHandler());
    register(OffsetDateTime.class, new OffsetDateTimeTypeHandler());
    register(OffsetTime.class, new OffsetTimeTypeHandler());
    register(ZonedDateTime.class, new ZonedDateTimeTypeHandler());
    register(Month.class, new MonthTypeHandler());
    register(Year.class, new YearTypeHandler());
    register(YearMonth.class, new YearMonthTypeHandler());
    register(JapaneseDate.class, new JapaneseDateTypeHandler());

    // issue #273
    register(Character.class, new CharacterTypeHandler());
    register(char.class, new CharacterTypeHandler());
  }

}
```

# ParameterHandler

```java
/**
 * A parameter handler sets the parameters of the {@code PreparedStatement}.
 *
 * @author Clinton Begin
 */
public interface ParameterHandler {

  Object getParameterObject();

  void setParameters(PreparedStatement ps) throws SQLException;

}
```

getParameterObject：该方法用于获取执行Mapper时传入的参数对象。

setParameters：该方法用于为JDBCPreparedStatement或者CallableStatement对象设置参数值。

**只有一个默认实现类**

```java
/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.scripting.defaults;


/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class DefaultParameterHandler implements ParameterHandler {

  private final TypeHandlerRegistry typeHandlerRegistry;

  private final MappedStatement mappedStatement;
  private final Object parameterObject;
  private final BoundSql boundSql;
  private final Configuration configuration;

  public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    this.mappedStatement = mappedStatement;
    this.configuration = mappedStatement.getConfiguration();
    this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    this.parameterObject = parameterObject;
    this.boundSql = boundSql;
  }

  @Override
  public Object getParameterObject() {
    return parameterObject;
  }

  @Override
  public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          Object value;
          String propertyName = parameterMapping.getProperty();
          if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (parameterObject == null) {
            value = null;
          } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            value = parameterObject;
          } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            value = metaObject.getValue(propertyName);
          }
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          JdbcType jdbcType = parameterMapping.getJdbcType();
          if (value == null && jdbcType == null) {
            jdbcType = configuration.getJdbcTypeForNull();
          }
          try {
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } catch (TypeException | SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          }
        }
      }
    }
  }

}
```

# ResultSetHandler

```java
/**
 * @author Clinton Begin
 */
public interface ResultSetHandler {

  <E> List<E> handleResultSets(Statement stmt) throws SQLException;

  <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;

  void handleOutputParameters(CallableStatement cs) throws SQLException;

}
```

handleResultSets：获取Statement对象中的ResultSet对象，对ResultSet对象进行处理，返回包含结果实体的List对象。

handleCursorResultSets：将ResultSet对象包装成Cursor对象，对Cursor进行遍历时，能够动态地从数据库查询数据，避免一次性将所有数据加载到内存中。

handleOutputParameters：处理存储过程调用结果。

也是只有一个默认的实现类

```java
/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor.resultset;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 * @author Iwao AVE!
 * @author Kazuki Shimizu
 */
public class DefaultResultSetHandler implements ResultSetHandler {

  private static final Object DEFERRED = new Object();

  private final Executor executor;
  private final Configuration configuration;
  private final MappedStatement mappedStatement;
  private final RowBounds rowBounds;
  private final ParameterHandler parameterHandler;
  private final ResultHandler<?> resultHandler;
  private final BoundSql boundSql;
  private final TypeHandlerRegistry typeHandlerRegistry;
  private final ObjectFactory objectFactory;
  private final ReflectorFactory reflectorFactory;

  // nested resultmaps
  private final Map<CacheKey, Object> nestedResultObjects = new HashMap<>();
  private final Map<String, Object> ancestorObjects = new HashMap<>();
  private Object previousRowValue;

  // multiple resultsets
  private final Map<String, ResultMapping> nextResultMaps = new HashMap<>();
  private final Map<CacheKey, List<PendingRelation>> pendingRelations = new HashMap<>();

  // Cached Automappings
  private final Map<String, List<UnMappedColumnAutoMapping>> autoMappingsCache = new HashMap<>();

  // temporary marking flag that indicate using constructor mapping (use field to reduce memory usage)
  private boolean useConstructorMappings;

  private static class PendingRelation {
    public MetaObject metaObject;
    public ResultMapping propertyMapping;
  }

  private static class UnMappedColumnAutoMapping {
    private final String column;
    private final String property;
    private final TypeHandler<?> typeHandler;
    private final boolean primitive;

    public UnMappedColumnAutoMapping(String column, String property, TypeHandler<?> typeHandler, boolean primitive) {
      this.column = column;
      this.property = property;
      this.typeHandler = typeHandler;
      this.primitive = primitive;
    }
  }

  public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql,
                                 RowBounds rowBounds) {
    this.executor = executor;
    this.configuration = mappedStatement.getConfiguration();
    this.mappedStatement = mappedStatement;
    this.rowBounds = rowBounds;
    this.parameterHandler = parameterHandler;
    this.boundSql = boundSql;
    this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    this.objectFactory = configuration.getObjectFactory();
    this.reflectorFactory = configuration.getReflectorFactory();
    this.resultHandler = resultHandler;
  }

  //
  // HANDLE OUTPUT PARAMETER
  //

  @Override
  public void handleOutputParameters(CallableStatement cs) throws SQLException {
    final Object parameterObject = parameterHandler.getParameterObject();
    final MetaObject metaParam = configuration.newMetaObject(parameterObject);
    final List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    for (int i = 0; i < parameterMappings.size(); i++) {
      final ParameterMapping parameterMapping = parameterMappings.get(i);
      if (parameterMapping.getMode() == ParameterMode.OUT || parameterMapping.getMode() == ParameterMode.INOUT) {
        if (ResultSet.class.equals(parameterMapping.getJavaType())) {
          handleRefCursorOutputParameter((ResultSet) cs.getObject(i + 1), parameterMapping, metaParam);
        } else {
          final TypeHandler<?> typeHandler = parameterMapping.getTypeHandler();
          metaParam.setValue(parameterMapping.getProperty(), typeHandler.getResult(cs, i + 1));
        }
      }
    }
  }

  private void handleRefCursorOutputParameter(ResultSet rs, ParameterMapping parameterMapping, MetaObject metaParam) throws SQLException {
    if (rs == null) {
      return;
    }
    try {
      final String resultMapId = parameterMapping.getResultMapId();
      final ResultMap resultMap = configuration.getResultMap(resultMapId);
      final ResultSetWrapper rsw = new ResultSetWrapper(rs, configuration);
      if (this.resultHandler == null) {
        final DefaultResultHandler resultHandler = new DefaultResultHandler(objectFactory);
        handleRowValues(rsw, resultMap, resultHandler, new RowBounds(), null);
        metaParam.setValue(parameterMapping.getProperty(), resultHandler.getResultList());
      } else {
        handleRowValues(rsw, resultMap, resultHandler, new RowBounds(), null);
      }
    } finally {
      // issue #228 (close resultsets)
      closeResultSet(rs);
    }
  }

  //
  // HANDLE RESULT SETS
  // 重点
  @Override
  public List<Object> handleResultSets(Statement stmt) throws SQLException {
    ErrorContext.instance().activity("handling results").object(mappedStatement.getId());

    final List<Object> multipleResults = new ArrayList<>();

    int resultSetCount = 0;
    ResultSetWrapper rsw = getFirstResultSet(stmt);

    List<ResultMap> resultMaps = mappedStatement.getResultMaps();
    int resultMapCount = resultMaps.size();
    validateResultMapsCount(rsw, resultMapCount);
    while (rsw != null && resultMapCount > resultSetCount) {
      ResultMap resultMap = resultMaps.get(resultSetCount);
      handleResultSet(rsw, resultMap, multipleResults, null);
      rsw = getNextResultSet(stmt);
      cleanUpAfterHandlingResultSet();
      resultSetCount++;
    }

    String[] resultSets = mappedStatement.getResultSets();
    if (resultSets != null) {
      while (rsw != null && resultSetCount < resultSets.length) {
        ResultMapping parentMapping = nextResultMaps.get(resultSets[resultSetCount]);
        if (parentMapping != null) {
          String nestedResultMapId = parentMapping.getNestedResultMapId();
          ResultMap resultMap = configuration.getResultMap(nestedResultMapId);
          handleResultSet(rsw, resultMap, null, parentMapping);
        }
        rsw = getNextResultSet(stmt);
        cleanUpAfterHandlingResultSet();
        resultSetCount++;
      }
    }

    return collapseSingleResultList(multipleResults);
  }
}
```

# SqlSession

## Configuration

书上简单介绍了下 XmlConfigurationBuilder 

## SqlSession 实例创建过程

```java
private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
  Transaction tx = null;
  try {
    final Environment environment = configuration.getEnvironment();
    final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
    tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
    final Executor executor = configuration.newExecutor(tx, execType);
    return new DefaultSqlSession(configuration, executor, autoCommit);
  } catch (Exception e) {
    closeTransaction(tx); // may have fetched a connection so lets call close()
    throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
  } finally {
    ErrorContext.instance().reset();
  }
}
```

上面的代码中，首先通过Configuration对象获取MyBatis主配置文件中通过`<environment>`标签配置的环境信息，然后根据配置的事务管理器类型创建对应的事务管理器工厂。MyBatis提供了两种事务管理器，分别为JdbcTransaction和ManagedTransaction。其中，JdbcTransaction是使用JDBC中的Connection对象实现事务管理的，而ManagedTransaction表示事务由外部容器管理。这两种事务管理器分别由对应的工厂类JdbcTransactionFactory和ManagedTransactionFactory创建。

事务管理器对象创建完毕后，接着调用Configuration对象的newExecutor()方法，根据MyBatis主配置文件中指定的Executor类型创建对应的Executor对象，最后以Executor对象和Configuration对象作为参数，通过Java中的new关键字创建一个DefaultSqlSession对象。DefaultSqlSession对象中持有Executor对象的引用，真正执行SQL操作的是Executor对象。

## Mapper 接口的注册过程

