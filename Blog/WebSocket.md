# Preface
## Design Philosophy
WebSocket and HTTP Server can use same port, it  

# HTTP Hyper Text Transfer Protocol
Text based protocol, all the things is text.

```http
GET /orders/123 HTTP/1.1\r\n
Host: example.com\r\n
\r\n
```


```http
### 
POST /orders/123 HTTP/1.1\r\n
Host: example.com\r\n
Content-Type: application/json\r\n
\r\n
{
 "foo": "bar"
}
\r\n
```

# WebSocket

WebSocket is application layer protocol, it based on TCP.

# Example 

## JavaScript + NodeJs

## Java

## JavaScript + Java


# Case Study
## ChatGPT

## Claude
Old version
## Twitch

## Douyu
Danmu delivery used by WebScoket, douyu custom text protocol just with binary message because of  the bandwidth saving.

# What about STOMP?
Streaming Text Oriented Messaging Protocol 
or
Simple Text Oriented Messaging Protocol

Whatever you call it. It just a protocol based on TCP or WebSocket, even if WebSocket protocol based on TCP. 

It also designed by Googlers.
# Refrence
1. [The WebSocket Protocol RFC 6455](https://datatracker.ietf.org/doc/html/rfc6455)
2. [ Hypertext Transfer Protocol RFC 2616](https://datatracker.ietf.org/doc/html/rfc2616)
3. [STOMP](https://stomp.github.io/)
4. JSR 356, Java API for WebSocket
5. [Spring WebSocket API](https://docs.spring.io/spring-framework/docs/5.3.37/reference/html/web.html#websocket-server)
6. [Tomcat core](https://github.com/apache/tomcat)
