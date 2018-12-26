package com.leyou.item.service.impl;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecServiceImpl implements SpecService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    @Override
    public List<SpecGroup> querySpecGroups(Long cid) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);

        //根据组查询组内参数
        specGroups.forEach(group ->{

            SpecParam specParam = new SpecParam();

            specParam.setGroupId(group.getId());
            specParam.setCid(group.getCid());

            group.setParams(specParamMapper.select(specParam));
        });

        return specGroups;
    }

    @Override
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        return this.specParamMapper.select(specParam);
    }
}
