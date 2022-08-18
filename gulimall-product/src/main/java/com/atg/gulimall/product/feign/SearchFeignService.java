package com.atg.gulimall.product.feign;

import com.atg.common.to.es.SkuEsModel;
import com.atg.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping("/searcj/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
