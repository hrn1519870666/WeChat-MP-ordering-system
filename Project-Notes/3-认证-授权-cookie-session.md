### 认证和授权

- **Authentication（认证）** 是验证身份的凭据（例如用户名/用户ID和密码），通过这个凭据，系统得以知道你就是你，也就是说系统存在你这个用户。所以，Authentication 被称为**身份/用户验证。**
- **Authorization（授权）** 发生在认证之后，它主要掌管我们访问系统的权限。有些特定资源只能具有特定权限的人才能访问，比如admin才能进行增删改操作。



### Cookie

**HTTP 协议是无状态的，**主要是为了让 HTTP 协议尽可能简单。HTTP/1.1 引入 Cookie 来保存状态信息。**Cookie 存放在客户端，一般用来保存用户信息**。

**Cookie 是由服务器发送到用户浏览器，并保存在本地的一小块数据，它会在浏览器再次向同一服务器发起请求时被携带上，用于告知服务端两个请求来自同一浏览器。**

#### 应用

我们在 Cookie 中保存已经登录过的用户信息，下次访问网站的时候页面可以自动帮你填写信息。除此之外，Cookie 还能保存用户首选项，主题和其他设置信息。

#### 分类

- 会话期 Cookie：浏览器关闭之后它会被自动删除，也就是说它仅在会话期内有效。
- 持久性 Cookie：指定过期时间（Expires）或有效期（max-age）之后就成为了持久性的 Cookie。



#### 如何在服务端使用 Cookie ？

**1)设置cookie返回给客户端**

```java
@GetMapping("/change-username")
public String setCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("username", "Jovan");
    cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
    response.addCookie(cookie);   //

    return "Username is changed!";
}
```

**2) 使用Spring框架提供的`@CookieValue`注解获取特定的 cookie的值**

```java
@GetMapping("/")
public String readCookie(@CookieValue(value = "username", defaultValue = "Atta") String username) {
    return "Hey! My username is " + username;
}
```

**3) 读取所有的 Cookie 值**

```java
@GetMapping("/all-cookies")
public String readAllCookies(HttpServletRequest request) {

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        return Arrays.stream(cookies)
                .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
    }

    return "No cookies";
}
```



### Session

**Session 的主要作用就是通过服务端记录用户的状态。** 典型的场景是购物车，当你要添加商品到购物车的时候，系统不知道是哪个用户操作的，因为 HTTP 协议是无状态的。服务端给特定的用户创建特定的 Session 之后就可以标识这个用户并且跟踪这个用户了。



#### Cookie和Session的区别

Cookie 保存在客户端(浏览器端)，Session 保存在服务器端。相对来说 Session 安全性更高。



#### 如何使用Session进行身份验证？

很多时候我们都是通过 SessionID（微信的openid） 来实现特定的用户，SessionID 一般会选择存放在 Redis 中。举个例子：用户成功登陆系统，然后返回给客户端具有 SessionID 的 Cookie，当用户向后端发起请求的时候会把 SessionID（或者token） 带上，这样后端就知道你的身份状态了。详细过程如下：

1. 用户向服务器发送用户名和密码用于登陆系统。
2. 服务器验证通过后，服务器为用户创建一个 Session，并将 Session信息存储起来。
3. **服务器向用户返回一个 SessionID，写入用户的 Cookie。**
4. **当用户保持登录状态时，Cookie 将与每个后续请求一起被发送出去。**
5. **服务器可以将存储在 Cookie 上的 Session ID 与 Session 信息进行比较，以验证用户的身份。**

依赖Session的关键业务一定要确保客户端开启了Cookie。



### 如果未开启Cookie的话Session还能用吗？

可以将SessionID放在请求的 url 里面`https://javaguide.cn/?session_id=xxx` 。这种方案可行，但是安全性降低。当然，也可以对  SessionID 进行一次加密之后再传入后端。



### 基于JWT进行身份验证

我们知道 Session 信息需要保存一份在服务器端。这种方式会带来一些麻烦，比如需要我们保证保存  Session  信息服务器的可用性、不适合移动端（依赖Cookie）等等。有没有一种不需要自己存放 Session  信息就能实现身份验证的方式呢？使用 Token 即可。

JWT （JSON Web Token） 就是这种方式的实现，通过这种方式，**服务器端不需要保存 Session 数据，只用在客户端保存服务端返回给客户的 Token。** 

JWT 本质上就一段签名的 JSON 格式的数据。由于它**带有签名，因此接收者便可以验证它的真实性。**

**本质是服务器端给SessionID签名，生成一个token，然后将它发送给客户端，当客户端再携带JWT请求服务器时，服务器只需要解密，即可验证。**

[JWT介绍](https://www.cnblogs.com/ittranslator/p/14595165.html)
