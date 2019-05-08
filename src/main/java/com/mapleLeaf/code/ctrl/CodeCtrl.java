package com.mapleLeaf.code.ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mapleLeaf.code.other.DataBase2File;
import com.mapleLeaf.common.bean.AjaxResult;
import com.mapleLeaf.common.util.FileTool;
import com.mapleLeaf.common.util.GlobalConst;



@Controller
@RequestMapping("/code")
public class CodeCtrl {
	
	@Autowired
	DataBase2File dataBase2File;
	
	
	
	@RequestMapping(value = "/renderConfig")
	@ResponseBody
	public AjaxResult<Map<String,Object>> renderConfig(){
		
		AjaxResult<Map<String,Object>> rst = new AjaxResult<>();
		
		Map<String,Object> resultMap = new HashMap<String, Object>(); 
		String configPath = this.getClass().getResource("/"+GlobalConst.CONFIG).getPath();
		
		try {
			resultMap.put("config", FileTool.readLocalFileContent(configPath));
			
		} catch (IOException e) {
			e.printStackTrace();
			rst.setCode("500");
			return rst;
		}
		
		//列出模板
		List<String> files = FileTool.listLocalFiles(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH)
				.getPath(),"1");
		
		List<String> tpls = new ArrayList<>();
		for(String f:files){
			
			if(f.startsWith("tpl")){
				tpls.add(f);
			}
		}
		resultMap.put("tpls", tpls);
		
		rst.setData(resultMap);
		rst.setCode("0");
		
		return rst;
	}
	
	@RequestMapping(value = "/updateConfig")
	@ResponseBody
	public AjaxResult<String> updateConfig(String config){
		
	
		AjaxResult<String> rst = new AjaxResult<>();
		String configPath = this.getClass().getResource("/"+GlobalConst.CONFIG).getPath();
		try {
			FileTool.writeLocalFileContent(configPath, config.trim().replace("&", "&amp;"));
			rst.setCode("0");
		} catch (IOException e) {
			e.printStackTrace();
			rst.setCode("500");
		}
		
		return rst;
	}
	@RequestMapping(value = "/renderTemplate")
	@ResponseBody
	public AjaxResult<Map<String,Object>> renderTemplate(){
		
		AjaxResult<Map<String,Object>> rst = new AjaxResult<>();
		
		Map<String,Object> resultMap = new HashMap<String, Object>(); 
		
		List<String> files = FileTool.listLocalFiles(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH)
					.getPath(),"1");
		
		List<String> tpls = new ArrayList<>();
		for(String f:files){
			if(f.startsWith("tpl")){
				tpls.add(f);
			}
		}
		resultMap.put("tpls", tpls);
		
		//第一个文件夹下的 文件
		String firstDir = tpls.get(0);
		
		List<String> firstFiles = FileTool.listLocalFiles(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH
				+"/"+firstDir).getPath(),"2");
		
		List<String> tplFiles = new ArrayList<>();
		for(String f:firstFiles){
			if(f.endsWith("ftl")){
				tplFiles.add(f);
			}
		}
		resultMap.put("tplFiles", tplFiles);
		
		//第一个文件的内容
		String firstFile = tplFiles.get(0);
		String content="";
		try {
			content = FileTool.readLocalFileContent(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH
					+"/"+firstDir+"/"+firstFile).getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rst.setCode("500");
			return rst;
		}
		
		resultMap.put("tplContent", content);
		
		rst.setData(resultMap);
		rst.setCode("0");
		
		return rst;
	}
	
	
	@RequestMapping(value = "/getTplFiles")
	@ResponseBody
	public AjaxResult<Map<String,Object>> getTplFiles(String dir){
		
		AjaxResult<Map<String,Object>> rst = new AjaxResult<>();
		
		Map<String,Object> resultMap = new HashMap<String, Object>(); 
		
		
		List<String> firstFiles = FileTool.listLocalFiles(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH
				+"/"+dir).getPath(),"2");
		List<String> tplFiles = new ArrayList<>();
		for(String f:firstFiles){
			if(f.endsWith("ftl")){
				tplFiles.add(f);
			}
		}
		resultMap.put("tplFiles", tplFiles);
		
		//第一个文件内容
		String firstFile = tplFiles.get(0);
		String content="";
		try {
			content = FileTool.readLocalFileContent(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH+"/"
					+dir+"/"+firstFile).getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rst.setCode("500");
			return rst;
		}
		
		resultMap.put("tplContent", content);
		rst.setData(resultMap);
		rst.setCode("0");
		
		return rst;
	}
	
	@RequestMapping(value = "/getTplContent")
	@ResponseBody
	public AjaxResult<String> getTplContent(String dir,String file){
		
		AjaxResult<String> rst = new AjaxResult<>();
		
		String content="";
		try {
			content = FileTool.readLocalFileContent(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH+"/"
					+dir+"/"+file).getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rst.setCode("500");
			return rst;
		}
		rst.setData(content);
		rst.setCode("0");
		return rst;
	}
	@RequestMapping(value = "/updateTplContent")
	@ResponseBody
	public AjaxResult<String> updateTplContent(String dir,String file,String tplcontent){
		
		AjaxResult<String> rst = new AjaxResult<>();
		
		try {
			FileTool.writeLocalFileContent(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH
					+"/"+dir+"/"+file).getPath(), tplcontent.trim());
			rst.setCode("0");
		} catch (IOException e) {
			e.printStackTrace();
			rst.setCode("500");
		}
		return rst;
	}
	@RequestMapping(value = "/addTemplate")
	@ResponseBody
	public AjaxResult<String> addTemplate(String dir,String flag,String tplname,String tpltype,String typename){
		
		//System.out.println(dir+"--"+flag+"--"+tplname+"--"+tpltype+"--"+typename);
		AjaxResult<String> rst = new AjaxResult<>();
		boolean boo=false;
		String fileName="";
		if("Other".equals(tpltype)){
			fileName = typename+".ftl";
		}else{
			fileName = tpltype+".ftl";
		}
		
		if("1".equals(flag)){
			boo=FileTool.createLocalFile(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH+"/"
					+dir).getPath(), fileName);
		}else{
			boo=FileTool.createLocalFile(this.getClass().getResource("/"+GlobalConst.TEMPLATE_PATH).getPath()
					+"/tpl_"+tplname, fileName);
		}
		if(boo){
			rst.setCode("0");
		}else{
			rst.setCode("1");
		}
		
		return rst;
	}
	
	@RequestMapping(value = "/generateCode")
	@ResponseBody
	public AjaxResult<String> generateCode(String tplname){
		
		AjaxResult<String> rst = new AjaxResult<>();
		
		//生成代码
        try {
        	dataBase2File.generateFiles("/"+GlobalConst.TEMPLATE_PATH+"/"+tplname);
		} catch (Exception e) {
			e.printStackTrace();
			rst.setCode("500");
			return rst;
		} 
		
		rst.setCode("0");
		return rst;
	}
	
	
}
