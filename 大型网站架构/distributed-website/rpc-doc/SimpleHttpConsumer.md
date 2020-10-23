# OutputStream 写入基础类型时
会截取其低 8 位，丢弃其高 24 位，因此要对基本类型先转换为字节流。Java 采用的是
Big Endian 字节序，所有的网络协议都是采用 Big Endian 字节序进行传输的。