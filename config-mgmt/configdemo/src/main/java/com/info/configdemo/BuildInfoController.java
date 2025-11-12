package com.info.configdemo;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BuildInfoController {

//    @Value("${build.id:default}")   //"${OS:default}")
//    private String buildId;
//
//    @Value("${build.name:default}")   //"${USER:default}")
//    private String buildVersion;
//
//    @Value("${build.type:default}")  //"${JAVA_HOME:default}")
//    private String buildName;

    private BuildInfo buildInfo;

    @GetMapping("/build-info")
    public String getBuildInfo(){
        return ("Build ID: " + buildInfo.getId()  + ", Version: " +
                buildInfo.getVersion() + ", Name: "+ buildInfo.getName());

    }

}
