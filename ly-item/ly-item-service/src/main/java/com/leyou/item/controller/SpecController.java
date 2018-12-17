package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroups = specService.querySpecGroups(cid);

        if (specGroups != null && 0 != specGroups.size()) {
            return ResponseEntity.ok(specGroups);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false)Long cid
        ){
        List<SpecParam> specParams = specService.querySpecParams(gid,cid);

        if (specParams != null && 0 != specParams.size()) {
            return ResponseEntity.ok(specParams);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
