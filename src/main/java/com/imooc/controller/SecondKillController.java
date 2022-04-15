package com.imooc.controller;

import com.imooc.service.SecondKillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀Controller，模拟高并发下的Redis分布式锁的实现
 */
@RequestMapping("/skill")
@RestController
@Slf4j
public class SecondKillController {

    @Autowired
    private SecondKillService secondKillService;

    /**
     * 查询秒杀商品的信息
     * @param productId
     * @return
     */
    @GetMapping("/query/{productId}")
    public String query(@PathVariable() String productId){
        return secondKillService.querySecondKillProductInfo(productId);
    }

    /**
     * 秒杀操作：妙杀失败返回“哎呦喂，xxxxx”。秒杀成功返回商品的剩余库存量
     * @param productId
     * @return
     */
    @GetMapping("/order/{productId}")
    public String skill(@PathVariable() String productId){
        log.info("@skill request, productId = "+productId);
        // secondKillService.orderProductMockDiffUser(productId);
        secondKillService.orderProductMockDiffUserByRedis(productId);
        return secondKillService.querySecondKillProductInfo(productId);
    }
}
