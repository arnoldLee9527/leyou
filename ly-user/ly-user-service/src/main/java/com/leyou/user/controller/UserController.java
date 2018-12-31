package com.leyou.user.controller;

import com.leyou.config.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    //校验用户是否存在
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> CheckUser(@PathVariable("data")String data, @PathVariable("type")Integer type){
        Boolean result = userService.CheckUser(data, type);

        if (result == null){
            //
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        //
        return ResponseEntity.ok(result);

    }

    //发送验证码
    @PostMapping("send")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone")String phone){
        Boolean boo = this.userService.sendVerifyCode(phone);
        if (boo == null || !boo) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    //注册
    @PostMapping("register")
    public ResponseEntity<Void> Register(@Valid User user, @RequestParam("code")String code){
        Boolean result = userService.Register(user,code);

        if (result){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

}
