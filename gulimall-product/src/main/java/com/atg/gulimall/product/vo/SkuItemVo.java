package com.atg.gulimall.product.vo;

import com.atg.gulimall.product.entity.SkuImagesEntity;
import com.atg.gulimall.product.entity.SkuInfoEntity;
import com.atg.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    //sku基本信息
    SkuInfoEntity info;
    //有货无货
    boolean hasStock = true;
    //sku图片信息
    List<SkuImagesEntity> images;
    //获取spu得销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;
    //spu介绍
    SpuInfoDescEntity desc;
    //spu规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;




}
