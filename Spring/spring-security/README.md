# Authentication

有关认证方面的，官方文档上写的是，最先是明文保存密码，但是为了防止 SQL 注入等漏洞的应用，导致用户名和密码被拉库。所以需要 Hash 算法，例如 SHA-256，对密码 SHA-256 Hash 一下，再存入数据库。如果密码需要验证，则对密码进行 SHA-256 Hash 再进行比对即可。但是这会有 Rainbow Table（彩虹🌈表）的出现，导致即使存了密码，被拉库，也是有问题的。这时候就需要加盐（Salt）。每个用户都可以有自己的盐值，掺入其中，这样就可以避免 Rainbow Table 的问题。Md5 就可以用彩虹表破解，
SHA-256 如果不加盐，现在的算力，也很快会被破解。

下面是一些关于密码加密的一些常见 PasswordEncoder。官方文档上写了 PasswordEncoderFactories#createDelegatingPasswordEncoder 相关的内容。
但是其@since 5.0 结果 5.3 被标记弃用了。
```java
public class PasswordEncoderTest {

   /**
    * The BCryptPasswordEncoder implementation uses the widely supported bcrypt algorithm
    * to hash the passwords. In order to make it more resistent to password cracking,
    * bcrypt is deliberately slow. Like other adaptive one-way functions,
    * it should be tuned to take about 1 second to verify a password on your system.
    * The default implementation of BCryptPasswordEncoder uses strength 10 as mentioned
    * in the Javadoc of BCryptPasswordEncoder. You are encouraged to tune and test the
    * strength parameter on your own system so that it takes roughly 1 second to verify a password
    */
   @Test
   public void bCryptTest() {
      // Create an encoder with strength 16
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
      String result = encoder.encode("myPassword");
      assertTrue(encoder.matches("myPassword", result));
      System.out.println(result);
   }

   /**
    * The Argon2PasswordEncoder implementation uses the Argon2 algorithm to hash the passwords.
    * Argon2 is the winner of the Password Hashing Competition. In order to defeat password
    * cracking on custom hardware, Argon2 is a deliberately slow algorithm that requires
    * large amounts of memory. Like other adaptive one-way functions,
    * it should be tuned to take about 1 second to verify a password on your system.
    * The current implementation if the Argon2PasswordEncoder requires BouncyCastle.
    */
   @Test
   public void argon2Test() {
      // Create an encoder with all the defaults
      Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
      String result = encoder.encode("myPassword");
      assertTrue(encoder.matches("myPassword", result));
      System.out.println(result);
   }

   /**
    * The Pbkdf2PasswordEncoder implementation uses the PBKDF2 algorithm to hash the passwords.
    * In order to defeat password cracking PBKDF2 is a deliberately slow algorithm.
    * Like other adaptive one-way functions, it should be tuned to take about 1 second
    * to verify a password on your system. This algorithm is a good choice when FIPS certification is required.
    */
   @Test
   public void pbkdf2Test() {
      // Create an encoder with all the defaults
      Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
      String result = encoder.encode("myPassword");
      assertTrue(encoder.matches("myPassword", result));
      System.out.println(result);
   }

   /**
    * The SCryptPasswordEncoder implementation uses scrypt algorithm to hash the passwords.
    * In order to defeat password cracking on custom hardware scrypt is a deliberately
    * slow algorithm that requires large amounts of memory.
    * Like other adaptive one-way functions, it should be tuned to
    * take about 1 second to verify a password on your system.
    */
   @Test
   public void sCryptTest(){
      // Create an encoder with all the defaults
      SCryptPasswordEncoder encoder = new SCryptPasswordEncoder();
      String result = encoder.encode("myPassword");
      assertTrue(encoder.matches("myPassword", result));
      System.out.println(result);
   }

}
```

# Protection Against Exploits（防止利用漏洞）

## Cross Site Request Forgery (CSRF 跨站请求伪造)
CSRF 跨站请求伪造，是一种对网站的恶意利用。尽管听起来跟 XSS 跨站脚本攻击有点相似，但事实上 CSRF 与 XSS 差别很大，
XSS 利用的是站点内信任用户，而 CSRF 则是通过伪装来自手信任用户的请求来利用受信任的网站。你可以这么理解 CSRF 攻击：
攻击者盗用了你的身份，以你的名义向第三方网站发送恶意请求。CSRF 能做的事情包括利用你的身份发送邮件、发短信、进行交易
转账等，甚至盗取你的账号<sup>[^1]:来自《大型分布式网站架构-设计与实践》，但是书上写成了 CRSF，没有人校对应该</sup>。

下面是原理图
![CSRF.png](https://i.loli.net/2021/05/05/ONasQ3jxDb7zMX6.png) 
官方文档上讲的转账的例子，已经很明确了，和这图一样。
https://docs.spring.io/spring-security/site/docs/5.3.9.RELEASE/reference/html5/#csrf

### CSRF 的防御
(1) 将 Cookie 设置为 HttpOnly
```java
response.setHeader("Set-Cookie","cookiename=cookievalue;HttpOnly");
```
(2) 增加 Token
```java
HttpSession session = request.getSession();
Object token = session.getAttribute("_token");
if(Objects.isNull(token)){
    session,setAttribute("_token",UUID.randomUUID().toString());
}
```
(3) 通过 Referer 识别
根据 HTTP 协议，在 HTTP 头中又一个字段叫 Referer，它记录了该 HTTP 请求的来源地址。在通常情况下，访问一个安全受限
的页面的请求都来自同一个网站，验证这个地址即可。

安全方法必须是幂等的。
