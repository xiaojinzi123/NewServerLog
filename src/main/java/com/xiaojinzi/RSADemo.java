package com.xiaojinzi;

import com.xiaojinzi.util.RSAUtil;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class RSADemo {

    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMtGv5X/dvxI/Mx3bnBdtmE2h0O98DW2sDByFHboO9lL6wxSHlDZbXanJQkhoGYxo9tFIbC3QMhFf95bNH6g32r02n45E3md6WN6PlCfhJFbowcGhW0FVzDkGlrvrizN11iwK1PEsdKxw8N+uaBVT/gvQ3rEmgmz6J4OCOk1dALTAgMBAAECgYEAvzUnVaLde2X0VSgqfAvKWBsAlVn+r6fOh2NnmInOchGVYRuFZKuA6dFDZxl4VWhwJvsaO63EhB3Lr46/DDWqsLQGsrmkHIkVbm2LP/+H3jlteBSDk/Ho17IXzgAmH8MLLegHr68IEAecWYb7T19b0ettO0cq+Ql/+KmfG+Ls/EECQQD5nWiDOX/Sv0UcGYj9yJ8skgY8VNSxFiyJJKcHoNcXfeE9QdHkycbnlyBVPcwwhz3U75XjJc5vnj+T2pmPDlpxAkEA0Hnk+wyfM2HEdPGNsIB60gE1RbGebW7U6zYnJ1CAbC11zsOir8b1/Twjk3EOFHm4iX1ipiS9JV+QYNmhZHbrgwJAHyvA+WIczDyGbNPjf42mEvLJRI9zYAnc1eN12EYFljFqBzRI+cEYzaLZrstgzE6XMhgZJ5x5AwdH+Ta7JlosgQJAa4aTTdECw7OnalG0LNb3gh1RZrLn7bV+aBq0MxjYQ/NmkdBhtpu+AHDmQIPNKU5mmbNsEBle3Une47UcAv87FQJAHdxgoIT6wckkept/TxgxB2jxO6f5kkbGm1N7MCK2XZ0CxKu5NC7wtktRgalzoJsVKil35yEgtIEEOw/EY2fBPg==";
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLRr+V/3b8SPzMd25wXbZhNodDvfA1trAwchR26DvZS+sMUh5Q2W12pyUJIaBmMaPbRSGwt0DIRX/eWzR+oN9q9Np+ORN5neljej5Qn4SRW6MHBoVtBVcw5Bpa764szddYsCtTxLHSscPDfrmgVU/4L0N6xJoJs+ieDgjpNXQC0wIDAQAB";

    public static void main(String[] args) throws Exception {

        PublicKey publicKey = RSAUtil.string2PublicKey(PUBLIC_KEY);

        /*byte[] bytes = "小金子测试".getBytes("UTF-8");
        // 公钥加密
        bytes = RSAUtil.publicEncrypt(bytes, RSAUtil.string2PublicKey(PUBLIC_KEY));
        String content = Base64.getUrlEncoder().encodeToString(bytes);
        System.out.println(content);*/

        byte[] bytes = RSAUtil.base642Byte("AWa/3S0DLL4DKZxrZt+618VNWPVNoG8xPUkTdBvZmXKUx5pMHBVkQ9dTaz2WWftHfhfknpUACF1A84RKnjA1Xh0kzNHTUXNaa/8rQXRfFA/9/XAptgj6TrOmSJlByjnh6JI2wluCBBS97lFDXVQ0m3e/znxWGs71VDDwJIByuRI=");
        bytes = RSAUtil.publicDecrypt(bytes, publicKey);

        System.out.println(new String(bytes, "UTF-8"));

    }

    private static void createRSAKey() throws NoSuchAlgorithmException {
        //获得对象 KeyPairGenerator 参数 RSA 1024个字节
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        //通过对象 KeyPairGenerator 获取对象KeyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();
        //通过对象 KeyPair 获取RSA公私钥对象RSAPublicKey RSAPrivateKey
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("down");
    }

}
