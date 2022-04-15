package com.imooc.repository;

import com.imooc.dataobject.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


// JpaRepository<参数1,参数2> 其中参数1表示是对哪个实体(数据表)操作，参数2表示是该实体的主键属性类型
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    //    根据类目编号的列表，查询类目列表，必须以findBy固定格式开头
    List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);
}
