#### natapp

调试工具，能让微信访问到自己的域名，该域名映射到localhost:8080。



#### 什么是内网穿透?

将内网外网通过natapp隧道打通,让内网的数据可以被外网获取。比如常用的办公室软件等，一般在办公室或家里，通过拨号上网，这样办公软件只有在本地的局域网之内才能访问，如果是手机上，或者公司外地的办公人员，如何访问到办公软件呢？这就需要natapp内网穿透工具了。运行natapp隧道之后，natapp会分配一个专属域名,办公软件就已经在公网上了,在外地的办公人员可以在任何地方访问办公软件了。



#### 内网穿透可以做什么?

微信本地开发。现在微信需要服务器接收微信发送的回调信息,然而在本地开发程序的话,还得实时上传到服务器,以便支持微信的回调信息,如果使用了natapp内网穿透软件,将回调地址设置成natapp提供的地址（域名）,回调数据立即传递回本地,这样很方便的在本地就可以实时调试程序,无须再不断上传服务器等繁琐且无意义的步骤。



#### Nginx

Nginx服务器在虚拟机中，客户端发来的请求访问虚拟机中的Nginx服务器，然后通过负载均衡算法，访问本机上的不同的微服务。