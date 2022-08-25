package com.atg.gulimall.search.web;

import com.atg.gulimall.search.service.MallSearchService;
import com.atg.gulimall.search.vo.SearchParam;
import com.atg.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String index(SearchParam param, Model model) {
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }
}
