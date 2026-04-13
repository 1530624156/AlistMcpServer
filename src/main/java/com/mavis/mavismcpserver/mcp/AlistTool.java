package com.mavis.mavismcpserver.mcp;

import com.mavis.entity.AlistConfig;
import com.mavis.mavismcpserver.entity.TAlistConfig;
import com.mavis.mavismcpserver.service.TAlistConfigService;
import com.mavis.util.AlistUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;

@Configuration
public class AlistTool {

    @Component
    public static class AlistToolService {


        @Resource
        private TAlistConfigService tAlistConfigService;

        @Tool(name = "初始化Alist配置", description = "用于初始化Alist配置-如果用户没有初始化Alist配置则无法使用其他Tool")
        public String alistInit(@ToolParam(description = "Alist的URL") String url, @ToolParam(description = "Alist的账号") String username, @ToolParam(description = "Alist的密码") String password) {
            String result = StringUtils.EMPTY;
            boolean flag = false;
            if (!StringUtils.isAnyBlank(url, username, password)) {
                AlistConfig alistConfig = new AlistConfig(url, username, password);
                String token = null;
                try {
                    token = AlistUtils.getToken(alistConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (StringUtils.isNotBlank(token)) {
                    TAlistConfig tAlistConfig = new TAlistConfig(url, username, password);
                    flag = tAlistConfigService.save(tAlistConfig);
                }else {
                    result = "【MCP】:Alist登录失败，请检查账号密码是否正确";
                }
            }else {
                result = "【MCP】:所有参数均不能为空";
            }

            if (flag) {
                result = "【MCP】:Alist初始化成功";
            }
            return result;
        }

        @Tool(name = "Alist文件列表", description = "用于获取Alist文件列表")
        public String alistFileList(@ToolParam(description = "Alist的目录") String path) {
            AlistConfig alistConfig = getAlistConfig();
            if (alistConfig == null) {
                return "【MCP】:请先初始化Alist配置";
            }
            String alistFileList = AlistUtils.getAlistFileList(alistConfig, path);
            return alistFileList;
        }

        @Tool(name = "Alist文件信息", description = "用于获取Alist文件信息")
        public String alistFileInfo(@ToolParam(description = "Alist的文件目录") String filePath) {
            AlistConfig alistConfig = getAlistConfig();
            if (alistConfig == null) {
                return "【MCP】:请先初始化Alist配置";
            }
            String alistFileInfo = AlistUtils.getAlistFileInfo(alistConfig, filePath, alistConfig.getAlistPassword());
            return alistFileInfo;
        }

        @Tool(name = "Alist所有文件信息", description = "递归获取Alist所有文件信息")
        public String alistAllFileInfo(@ToolParam(description = "Alist的目录") String path) {
            AlistConfig alistConfig = getAlistConfig();
            if (alistConfig == null) {
                return "【MCP】:请先初始化Alist配置";
            }
            List<HashMap<String, String>> alistAllFilesInfo = AlistUtils.getAlistAllFilesInfo(alistConfig, path, alistConfig.getAlistPassword());
            if (CollectionUtils.isEmpty(alistAllFilesInfo)) {
                return "【MCP】:没有找到文件";
            }
            return alistAllFilesInfo.toString();

        }


        /**
         * 获取Alist配置
         * @return
         */
        private AlistConfig getAlistConfig() {
            List<TAlistConfig> tAlistConfig = tAlistConfigService.list();
            if (CollectionUtils.isEmpty(tAlistConfig)) {
                return null;
            }
            return new AlistConfig(tAlistConfig.get(0).getUrl(), tAlistConfig.get(0).getUsername(), tAlistConfig.get(0).getPassword());
        }

    }
}
