package com.xiaojinzi.controller;


import com.google.gson.Gson;
import com.xiaojinzi.NetworkLog;
import com.xiaojinzi.bean.Message;
import com.xiaojinzi.bean.ResultVORes;
import com.xiaojinzi.util.RSAUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.Base64;

@Controller
@RequestMapping("network")
public class NetworkLogController {

    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMtGv5X/dvxI/Mx3bnBdtmE2h0O98DW2sDByFHboO9lL6wxSHlDZbXanJQkhoGYxo9tFIbC3QMhFf95bNH6g32r02n45E3md6WN6PlCfhJFbowcGhW0FVzDkGlrvrizN11iwK1PEsdKxw8N+uaBVT/gvQ3rEmgmz6J4OCOk1dALTAgMBAAECgYEAvzUnVaLde2X0VSgqfAvKWBsAlVn+r6fOh2NnmInOchGVYRuFZKuA6dFDZxl4VWhwJvsaO63EhB3Lr46/DDWqsLQGsrmkHIkVbm2LP/+H3jlteBSDk/Ho17IXzgAmH8MLLegHr68IEAecWYb7T19b0ettO0cq+Ql/+KmfG+Ls/EECQQD5nWiDOX/Sv0UcGYj9yJ8skgY8VNSxFiyJJKcHoNcXfeE9QdHkycbnlyBVPcwwhz3U75XjJc5vnj+T2pmPDlpxAkEA0Hnk+wyfM2HEdPGNsIB60gE1RbGebW7U6zYnJ1CAbC11zsOir8b1/Twjk3EOFHm4iX1ipiS9JV+QYNmhZHbrgwJAHyvA+WIczDyGbNPjf42mEvLJRI9zYAnc1eN12EYFljFqBzRI+cEYzaLZrstgzE6XMhgZJ5x5AwdH+Ta7JlosgQJAa4aTTdECw7OnalG0LNb3gh1RZrLn7bV+aBq0MxjYQ/NmkdBhtpu+AHDmQIPNKU5mmbNsEBle3Une47UcAv87FQJAHdxgoIT6wckkept/TxgxB2jxO6f5kkbGm1N7MCK2XZ0CxKu5NC7wtktRgalzoJsVKil35yEgtIEEOw/EY2fBPg==";
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLRr+V/3b8SPzMd25wXbZhNodDvfA1trAwchR26DvZS+sMUh5Q2W12pyUJIaBmMaPbRSGwt0DIRX/eWzR+oN9q9Np+ORN5neljej5Qn4SRW6MHBoVtBVcw5Bpa764szddYsCtTxLHSscPDfrmgVU/4L0N6xJoJs+ieDgjpNXQC0wIDAQAB";

    private static Gson g = new Gson();

    /**
     * 接受请求发送到浏览器, 但是仅用于测试
     */
    @ResponseBody
    @PostMapping("log")
    public String log(@RequestParam("tag") String tag,
                      @RequestParam("data") String data) {
        if (tag == null || tag.length() == 0) {
            throw new NullPointerException("data is null");
        }
        if (data == null || data.length() == 0) {
            throw new NullPointerException("data is null");
        }
        Message message = g.fromJson(data, Message.class);
        String deviceName = NetworkLog.getInstance().getDeviceName(tag);
        if (deviceName == null) {
            return "fail";
        } else {
            message.setSelfTag(deviceName);
            NetworkLog.getInstance().sendNetworkLog(message);
            return "success";
        }
    }

    @ResponseBody
    @PostMapping("auth")
    public ResultVORes<String> auth(@RequestParam("content") String content) throws Exception {
        PrivateKey privateKey = RSAUtil.string2PrivateKey(PRIVATE_KEY);
        byte[] contentBytes = Base64.getDecoder().decode(content);
        // 解密出来的东西
        byte[] decryptBytes = RSAUtil.privateDecrypt(contentBytes, privateKey);
        // System.out.println(new String(decryptBytes, "UTF-8"));
        byte[] encryptBytes = RSAUtil.privateEncrypt(decryptBytes, privateKey);
        String encryptStr = Base64.getEncoder().encodeToString(encryptBytes);
        return ResultVORes.success(encryptStr);
    }

}
