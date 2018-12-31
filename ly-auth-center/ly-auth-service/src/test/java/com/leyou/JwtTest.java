package com.leyou;

import com.leyou.entity.UserInfo;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.stream.Stream;

public class JwtTest {

    private static final String pubKeyPath = "D:/heima/rsa/rsa.pub";

    private static final String priKeyPath = "D:/heima/rsa/rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU0NjA1MzUxMn0.CkUSYK2HM3k1MDbh4DoOVhnXycde-3huiMTkxce0LOp5I2jJSF77MLYB1_DKbCeB20GV-FabeK7eHCZ2-jobdZNf5HHITDZMh6ZVox3vDJWhOKyqZckzaLJID1iMSPbkGZwWIRaOv2mY23FiMqZCPYljagt8Mgv-UqIVdNgsEmc";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }

    @Test
    public void testByte(){
        String s = "ly@Login(Auth}*^51)&heiMa%";
        byte[] bytes = s.getBytes();
    }
}