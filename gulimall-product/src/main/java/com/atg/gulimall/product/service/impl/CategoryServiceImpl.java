package com.atg.gulimall.product.service.impl;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atg.common.utils.PageUtils;
import com.atg.common.utils.Query;
import com.atg.gulimall.product.dao.CategoryDao;
import com.atg.gulimall.product.entity.CategoryEntity;
import com.atg.gulimall.product.service.CategoryBrandRelationService;
import com.atg.gulimall.product.service.CategoryService;
import com.atg.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    //    @Autowired
//    CategoryDao categoryDao;
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    private Map<String, Object> cache = new HashMap<>();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1）、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {

        String catelogJson = redisTemplate.opsForValue().get("catelogJsonDb");
        Map<String, List<Catelog2Vo>> result;
        if (StringUtils.isEmpty(catelogJson)) {
            getCatelogJsonFromDbWithRedissonLock();
            catelogJson =  redisTemplate.opsForValue().get("catelogJsonDb");
            return JSON.parseObject(catelogJson,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        }
        result = JSON.parseObject(catelogJson,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        return result;

    }

    /**
     * redisson分布式锁
     */
    public void getCatelogJsonFromDbWithRedissonLock() {
        RLock lock = redissonClient.getLock("catelogJson");
        lock.lock();
        try {
            getCatelogJsonFromDb();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 原始的分布式锁
     */
    public void getCatelogJsonFromDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if(lock){
            //设置过期时间必须和加锁是同步的,原子的
            try {
                getCatelogJsonFromDb();
            } finally {
                //redis官方文档
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                //删除锁
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script,Long.class), Arrays.asList("lock"), uuid);
            }
        }else{
            //加锁失败...
            //休眠100ms
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void getCatelogJsonFromDb() {
        String catelogJson = redisTemplate.opsForValue().get("catelogJsonDb");
        if (catelogJson != null) {
            return;
        }
        System.out.println("查询数据库");
        List<CategoryEntity> list = this.list();
        List<CategoryEntity> level1Categorys = getParent_cid(list, 0L);
        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查出2级分类
            List<CategoryEntity> entities = getParent_cid(list, v.getCatId());
            //遍历转换为vo
            List<Catelog2Vo> catelog2Vos = null;
            if (entities != null) {
                catelog2Vos = entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Catelog = getParent_cid(list, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> catelog3Vos = level3Catelog.stream().map(l3 -> {
                            return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3Vos);
                    }
                    return catelog2Vo;

                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        redisTemplate.opsForValue().set("catelogJsonDb", JSON.toJSONString(collect));
    }


    /**
     * 本地缓存
     */
//    @Override
//    public Map<String, List<Catelog2Vo>> getCatelogJson() {
//        Map<String, List<Catelog2Vo>> catelogJson = (Map<String, List<Catelog2Vo>>) cache.get("catelogJson");
//        if (catelogJson == null) {
//            List<CategoryEntity> list = this.list();
//            List<CategoryEntity> level1Categorys = getParent_cid(list, 0L);
//            Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//                //查出2级分类
//                List<CategoryEntity> entities = getParent_cid(list, v.getCatId());
//                //遍历转换为vo
//                List<Catelog2Vo> catelog2Vos = null;
//                if (entities != null) {
//                    catelog2Vos = entities.stream().map(l2 -> {
//                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
//                        List<CategoryEntity> level3Catelog = getParent_cid(list, l2.getCatId());
//                        if (level3Catelog != null) {
//                            List<Catelog2Vo.Catelog3Vo> catelog3Vos = level3Catelog.stream().map(l3 -> {
//                                return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                            }).collect(Collectors.toList());
//                            catelog2Vo.setCatalog3List(catelog3Vos);
//                        }
//                        return catelog2Vo;
//
//                    }).collect(Collectors.toList());
//                }
//                return catelog2Vos;
//            }));
//            cache.put("catelogJson",collect);
//            return collect;
//        }
//        return catelogJson;
//
//    }
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> list, Long parent_cid) {
        return list.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;

    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }


}
