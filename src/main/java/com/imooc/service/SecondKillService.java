package com.imooc.service;

public interface SecondKillService {

    String querySecondKillProductInfo(String productId);

    void orderProductMockDiffUser(String productId);

    void orderProductMockDiffUserByRedis(String productId);
}
