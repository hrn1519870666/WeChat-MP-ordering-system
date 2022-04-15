package com.imooc.service;

import com.imooc.dataobject.ProductCategory;

import java.util.List;

/**
 * 类目
 */
public interface CategoryService {

    // findOne和findAll方法是给后台管理用的
    ProductCategory findOne(Integer categoryId);

    List<ProductCategory> findAll();

    // 给买家端用
    List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);

    // 新增和更新都是save方法
    ProductCategory save(ProductCategory productCategory);
}
