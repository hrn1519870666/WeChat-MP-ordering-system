#### 代码中与Redis有关的类

**分布式锁**

SecondKillController

RedisLock（重要）

SecondKillServiceImpl

**缓存**

SellerProductController：@CachePut，@CacheEvict

BuyerProductController：@Cacheable

代码中没有，后续补充



#### 压测

Apache ab：

打开cmd，输入：

d:

cd apache24/bin

使用Apache ab模拟高并发，命令如下：

```bash
>ab -n 100 -c 100 http://www.baidu.com
# -n 100 ：表示发出100个请求
# -c 100：表示模拟100个并发
# 整个命令的含义：100个人同时访问这个url

>ab -t 60 -c 100 http://www.baidu.com
# 整个命令的含义：表示60秒内100个人访问这个url
```



**不加锁**

<img src="C:\Users\黄睿楠\AppData\Roaming\Typora\typora-user-images\image-20220410152958531.png" alt="image-20220410152958531" style="zoom:50%;" />



**synchronized互斥锁**

<img src="C:\Users\黄睿楠\AppData\Roaming\Typora\typora-user-images\image-20220410153213983.png" alt="image-20220410153213983" style="zoom:50%;" />



synchronized锁是一种解决方法。

缺点：

1. 无法做到细粒度控制。因为synchronized锁直接作用在秒杀下单的方法上，不管是什么商品，多少人秒杀，都是一样慢，如果秒杀A商品的人很多而秒杀B商品的人很少，则秒杀A和B都很慢。
2. 只适用于单节点，若水平扩展成集群，负载均衡后，不同的用户看到的结果不同。



**Redis分布式锁**

```java
/**
 * 加锁，使用setnx命令
 * @param key   productId
 * @param value  当前时间+超时时间
 * @return
 */
    public boolean lock(String key,String value){
        //加锁成功
        if(redisTemplate.opsForValue().setIfAbsent(key,value)){
            return true;
        }

        //解决因未执行解锁操作导致的死锁问题——通过锁超时解决，并且多个线程到来，只会有一个线程获取锁
        //currentValue=A,新来的两个线程A和B的value都是B，则只会有其中一个线程拿到锁
        //类似DCL双重校验锁的思想
        String currentValue = redisTemplate.opsForValue().get(key);
        //如果锁过期
        if(!StringUtils.isEmpty(currentValue)
                &&Long.parseLong(currentValue) < System.currentTimeMillis()){
            //获取上一个锁的时间(再get一次)
            String oldValue = redisTemplate.opsForValue().getAndSet(key,value);
            if(!StringUtils.isEmpty(oldValue)
                    &&oldValue.equals(currentValue)){
                return true;
            }
        }

        //加锁失败
        return false;
    }
```



**压测：**

ab -n 500 -c 100 http://localhost:8080/sell/skill/order/1

<img src="C:\Users\黄睿楠\AppData\Roaming\Typora\typora-user-images\image-20220410153303395.png" alt="image-20220410153303395" style="zoom:50%;" />

结果：国庆活动，皮蛋粥特价，限量份100000还剩99994份该商品成功下单用户数：6人

发送了500个请求，但成功下单的只有6个，因为其他的请求没有获取到锁。



ab -n 5000 -c 1000 http://localhost:8080/sell/skill/order/1

<img src="C:\Users\黄睿楠\AppData\Roaming\Typora\typora-user-images\image-20220410154017274.png" alt="image-20220410154017274" style="zoom:50%;" />

国庆活动，皮蛋粥特价，限量份100000还剩99980份该商品成功下单用户数：20人



ab -n 10000 -c 1000 http://localhost:8080/sell/skill/order/1

<img src="C:\Users\黄睿楠\AppData\Roaming\Typora\typora-user-images\image-20220410154215246.png" alt="image-20220410154215246" style="zoom:50%;" />

国庆活动，皮蛋粥特价，限量份100000还剩99965份该商品成功下单用户数：35人



**相比于synchronized锁：**支持分布式场景，且能更细粒度的控制代码。

Redis可以做分布式锁的一个重要原因：Redis是单线程的。

Redis分布式锁可以实现：多台机器上多个线程对**一个数据**进行**互斥**操作。



**除了分布式锁之外，解决超卖问题的方法还有哪些？**

1. 在sql上加判断，防止数据变为负数。 
2. Redis预减库存：系统初始化的时候，将商品库存加载到Redis 缓存中保存。收到请求时，先在Redis中拿到该商品的库存值，进行库存预减，如果减完之后库存不足，直接抛异常，不需要访问数据库，如果库存值正确，则执行秒杀逻辑。



**Redis 分布式锁的实现方法**

我用了四种方法 ，不同版本的缺陷以及演进的过程如下：

1. 没有操作，在并发场景中容易出现问题。
2. 加synchronized锁。
3. **加Redis分布式锁，在访问核心方法前，加入Redis锁可以阻塞其他线程访问,可以很好的处理并发问题,但是缺陷是如果机器突然宕机，就会造成死锁问题。**
4. **加入时间对比，如果当前时间>释放锁（锁超时）的时间，说明已经可以释放这个锁重新再获取锁，getset方法可以把之前的锁去掉再重新获取，getset方法get到的值与旧值比较，如果无变化，则说明这个期间没有人获取或者操作这个Redis锁，可以重新获取锁。**



自己实现的RedisLock的加锁和解锁操作不是原子性的，改进方法：

1. Redis事务
2. Lua脚本



Redis分布式锁如何续期？

RedLock之Redisson
