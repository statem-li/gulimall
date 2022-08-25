package com.atg.gulimall.product;

import com.atg.gulimall.product.service.SkuInfoService;
import com.atg.gulimall.product.service.SkuSaleAttrValueService;
import com.atg.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atg.gulimall.product.vo.SkuItemVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;

    @Test
    void contextLoads() {
    }

    @Test
    void test(){
//        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
//        ops.set("hello", UUID.randomUUID().toString());
//
//        System.out.println(ops.get("hello"));
//        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(6L);
        SkuItemVo item = skuInfoService.item(6L);
        System.out.println(item);
    }


}
