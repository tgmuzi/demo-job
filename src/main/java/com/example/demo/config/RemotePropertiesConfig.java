package com.example.demo.config;

import com.example.demo.modules.user.entity.SysUser;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RemotePropertiesConfig {


    @Value("${adminPath}")
    private String adminPath;

    protected SysUser getUser() {
        return (SysUser) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getUserId() {
        return getUser().getUserId();
    }

    /**
     * @return String return the adminPath
     */
    public String getAdminPath() {
        return adminPath;
    }

    protected String getUserCode() {
        return getUser().getUserName();
    }

    /**
     * @param adminPath the adminPath to set
     */
    public void setAdminPath(String adminPath) {
        this.adminPath = adminPath;
    }


}
