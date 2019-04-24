package com.mapleLeaf.common.ctrl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mapleLeaf.common.bean.AjaxResult;


@Controller
@RequestMapping("/index")
public class IndexCtrl {
	
	@RequestMapping("/index")
	@ResponseBody
	public AjaxResult<Map<String,String>> index(HttpServletRequest request){
		Properties props = System.getProperties();
        //java版本
        String javaVersion = props.getProperty("java.version");
        //操作系统名称
        String osName = props.getProperty("os.name") + props.getProperty("os.version");
        //用户的主目录
        String userHome = props.getProperty("user.home");
        //用户的当前工作目录
        String userDir = props.getProperty("user.dir");
        //服务器IP
        String serverIP = request.getLocalAddr();
        //客户端IP
        String clientIP = request.getRemoteHost();
        //WEB服务器
        String webVersion = request.getServletContext().getServerInfo();
        //CPU个数
        String cpu = Runtime.getRuntime().availableProcessors() + "核";
        //虚拟机内存总量
        String totalMemory = (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "M";
        //虚拟机空闲内存量
        String freeMemory = (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "M";
        //虚拟机使用的最大内存量
        String maxMemory = (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "M";
       
        //网站根目录
        String webRootPath = request.getSession().getServletContext().getRealPath("");
        Map<String, String> propsMap = new HashMap<>();
        propsMap.put("javaVersion", javaVersion);
        propsMap.put("osName", osName);
        propsMap.put("userHome", userHome);
        propsMap.put("userDir", userDir);
        propsMap.put("clientIP", clientIP);
        propsMap.put("serverIP", serverIP);
        propsMap.put("cpu", cpu);
        propsMap.put("totalMemory", totalMemory);
        propsMap.put("freeMemory", freeMemory);
        propsMap.put("maxMemory", maxMemory);
        propsMap.put("webVersion", webVersion);
        propsMap.put("webRootPath", webRootPath);
        
        AjaxResult<Map<String,String>> result = new AjaxResult<Map<String,String>>();
        result.setCode("0");
        result.setData(propsMap);
        
        return result;
	}
}
