package com.atg.gulimall.product.web;

import com.atg.gulimall.product.entity.CategoryEntity;
import com.atg.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @GetMapping({"/","index.html"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }
}
