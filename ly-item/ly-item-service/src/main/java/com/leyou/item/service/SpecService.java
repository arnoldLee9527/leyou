package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecService {
    List<SpecGroup> querySpecGroups(Long cid);

    List<SpecParam> querySpecParams(Long gid, Long cid, Boolean generic, Boolean searching);
}
