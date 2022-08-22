package com.atg.gulimall.product.web;

import com.atg.gulimall.product.entity.CategoryEntity;
import com.atg.gulimall.product.service.CategoryService;
import com.atg.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;

    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntityList);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        return categoryService.getCatelogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        RLock lock = redissonClient.getLock("hello-lock");
        lock.lock();
        try {
            System.out.println("加锁");
        } finally {
            lock.unlock();
        }
        return "hello";
    }

    @ResponseBody
    @GetMapping("/read")
    public String read() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("readWrite-lock");
        readWriteLock.readLock().lock();
        try {
            System.out.println("加锁");
        } finally {
            readWriteLock.readLock().unlock();
        }
        return "hello";
    }

    @ResponseBody
    @GetMapping("/write")
    public String write() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("readWrite-lock");
        readWriteLock.writeLock().lock();
        try {
            System.out.println("加锁");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return "hello";
    }

    @ResponseBody
    @GetMapping("/semaphore/park")
    public String lockGo() {
        RSemaphore rSemaphore = redissonClient.getSemaphore("semaphore-lock");
        rSemaphore.release();
        System.out.println("释放一个");
        return "hello";
    }

    @ResponseBody
    @GetMapping("/semaphore/go")
    public String lockDoor1() throws InterruptedException {
        RSemaphore rSemaphore = redissonClient.getSemaphore("semaphore-lock");
        rSemaphore.acquire();
        System.out.println("进来了一个");
        return "hello";
    }

    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch downLatch = redissonClient.getCountDownLatch("door-lock");
        downLatch.trySetCount(5);
        downLatch.await();//闭锁
        return "闭锁";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String lockDoor(@PathVariable("id") Long id) {
        RCountDownLatch downLatch = redissonClient.getCountDownLatch("door-lock");
        downLatch.countDown();//计数减一
        return String.valueOf(id);
    }


}
