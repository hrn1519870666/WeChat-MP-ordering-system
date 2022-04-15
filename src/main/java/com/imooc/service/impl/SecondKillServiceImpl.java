package com.imooc.service.impl;

import com.imooc.exception.SellException;
import com.imooc.lock.RedisLock;
import com.imooc.service.SecondKillService;
import com.imooc.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecondKillServiceImpl implements SecondKillService {

    private static final int TIMEOUT = 10 * 1000;//超时时间10秒

    @Autowired
    private RedisLock redisLock;

    /**
     * 国庆活动，皮蛋粥特价限量100000份
     *
     * 模拟多个表，商品信息表、库存表、秒杀成功订单表
     */
    static Map<String,Object> products;
    static Map<String,Object> stock;
    static Map<String,Object> orders;

    static {
        products = new HashMap<>();
        stock = new HashMap<>();
        orders = new HashMap<>();

        products.put("1",100000);
        stock.put("1",100000);
    }
    /**
     * 模拟查询秒杀商品的信息
     * @param productId
     * @return
     */
    @Override
    public String querySecondKillProductInfo(String productId) {
        return "国庆活动，皮蛋粥特价，限量份"
                + products.get(productId)
                + "还剩" + stock.get(productId) + "份"
                + "该商品成功下单用户数：" + orders.size() + "人";
    }

    /**
     * 高并发秒杀操作.妙杀失败返回“哎呦喂，xxxxx”。
     * @param productId
     * @return
     */
    @Override
    public void orderProductMockDiffUser(String productId) {
        skill(productId);
    }

    /**
     * 使用Redis分布式锁处理高并发
     * @param productId
     */
    public void orderProductMockDiffUserByRedis(String productId) {
        //1. 加Redis分布式锁
        long time = System.currentTimeMillis() + TIMEOUT;
        if(!redisLock.lock(productId,String.valueOf(time))){
            //加锁失败
            throw new SellException(101,"哎呦喂，人也太多了，还个姿势再试试~~~");
        }

        //秒杀操作
        skill(productId);

        //4.解除Redis分布式锁
        redisLock.unlock(productId,String.valueOf(time));
    }

    /**
     * 秒杀操作
     * @param productId
     */
    private void skill(String productId){
        //1. 查询该商品库存，为0则活动结束
        Integer stockNum = Integer.valueOf(stock.get(productId).toString());
        if(stockNum == 0){
            throw new SellException(100,"活动结束");
        }else{
            //2. 下单（模拟不同用户openid不同）
            orders.put(KeyUtil.genUniqueKey(),productId);
            //3. 减库存
            stockNum -= 1;
            try {
                Thread.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            stock.put(productId,stockNum);
        }
    }
}
