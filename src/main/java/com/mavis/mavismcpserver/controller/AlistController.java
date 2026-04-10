package com.mavis.mavismcpserver.controller;

import com.mavis.entity.AlistConfig;
import com.mavis.mavismcpserver.entity.TAlistConfig;
import com.mavis.mavismcpserver.service.TAlistConfigService;
import com.mavis.util.AlistUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Alist REST Controller
 */
@RestController
@RequestMapping("/alist")
public class AlistController {

    @Resource
    private TAlistConfigService tAlistConfigService;

    @PostMapping("/init")
    public Mono<Map<String, Object>> alistInit(@RequestBody TAlistConfig request) {
        Map<String, Object> result = new HashMap<>();

        if (StringUtils.isAnyBlank(request.getUrl(), request.getUsername(), request.getPassword())) {
            result.put("success", false);
            result.put("message", "所有参数均不能为空");
            return Mono.just(result);
        }

        // 测试账号密码是否正确
        AlistConfig alistConfig = new AlistConfig(request.getUrl(), request.getUsername(), request.getPassword());
        String token;
        try {
            token = AlistUtils.getToken(alistConfig);
        } catch (Exception e) {
            token = null;
        }

        if (StringUtils.isBlank(token)) {
            result.put("success", false);
            result.put("message", "Alist登录失败，请检查账号密码是否正确");
            return Mono.just(result);
        }

        // 删除已有的配置-仅保留一条
        boolean hasExisting = !CollectionUtils.isEmpty(tAlistConfigService.list());
        if (hasExisting) {
            tAlistConfigService.remove(null);
        }
        TAlistConfig tAlistConfig = new TAlistConfig(request.getUrl(), request.getUsername(), request.getPassword());
        boolean flag = tAlistConfigService.save(tAlistConfig);

        if (flag) {
            result.put("success", true);
            result.put("message", "Alist初始化成功");
        } else {
            result.put("success", false);
            result.put("message", "Alist初始化失败");
        }
        return Mono.just(result);
    }

    @GetMapping("/status")
    public Mono<Map<String, Object>> alistStatus() {
        Map<String, Object> result = new HashMap<>();
        boolean initialized = !CollectionUtils.isEmpty(tAlistConfigService.list());
        result.put("initialized", initialized);
        return Mono.just(result);
    }
}
