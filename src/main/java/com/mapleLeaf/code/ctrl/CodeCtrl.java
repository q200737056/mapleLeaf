package com.mapleLeaf.code.ctrl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mapleLeaf.code.other.GenCodeFile;
import com.mapleLeaf.common.bean.AjaxResult;
import com.mapleLeaf.common.util.FileTool;
import com.mapleLeaf.common.util.GlobalConst;



@Controller
@RequestMapping("/code")
public class CodeCtrl {
	
	@Autowired
	public GenCodeFile genCode;
	
	
	
	@RequestMapping(value = "/renderConfig")
	@ResponseBody
	public AjaxResult<Map<String,Object>> renderConfig(){
		
		AjaxResult<Map<String,Object>> rst = new AjaxResult<>();
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		String configPath = FileTool.getRealPath(GlobalConst.CONFIG);
		
		try {
			resultMap.put("config", FileTool.readLocalFileContent(configPath));
			
		} catch (IOException e) {
			e.printStackTrace();
			rst.setCode("500");
			return rst;
		}
		
		//列出模板
		List<String> files = FileTool.listLocalFiles(FileTool.getRealPath(GlobalConst.TEMPLATE_PATH)
				,"1");
		
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
		String configPath = FileTool.getRealPath(GlobalConst.CONFIG);
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
		
		List<String> files = FileTool.listLocalFiles(
				FileTool.getRealPath(GlobalConst.TEMPLATE_PATH),"1");
		
		List<String> tpls = new ArrayList<>();
		for(String f:files){
			if(f.startsWith("tpl")){
				tpls.add(f);
			}
		}
		resultMap.put("tpls", tpls);
		
		//第一个文件夹下的 文件
		String firstDir = tpls.get(0);
		
		List<String> firstFiles = FileTool.listLocalFiles(
				FileTool.getRealPath(GlobalConst.TEMPLATE_PATH+"/"+firstDir),"2");
		
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
			content = FileTool.readLocalFileContent(
					FileTool.getRealPath(GlobalConst.TEMPLATE_PATH+"/"+firstDir+"/"+firstFile));
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
		
		List<String> firstFiles = FileTool.listLocalFiles(
				FileTool.getRealPath(GlobalConst.TEMPLATE_PATH+"/"+dir),"2");
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
			content = FileTool.readLocalFileContent(
					FileTool.getRealPath(GlobalConst.TEMPLATE_PATH+"/"+dir+"/"+firstFile));
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
			content = FileTool.readLocalFileContent(
					FileTool.getRealPath(GlobalConst.TEMPLATE_PATH+"/"
					+dir+"/"+file));
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
			FileTool.writeLocalFileContent(
					FileTool.getRealPath(GlobalConst.TEMPLATE_PATH
							+"/"+dir+"/"+file), tplcontent.trim());
			rst.setCode("0");
		} catch (IOException e) {
			e.printStackTrace();
			rst.setCode("500");
		}
		
		return rst;
	}
	@RequestMapping(value = "/addTemplate")
	@ResponseBody
	public AjaxResult<String> addTemplate(String dir,String flag,String tplname,String  tpltype,String typename){
		
		//System.out.println(dir+"--"+flag+"--"+tplname+"--"+tpltype+"--"+typename);
		
		String addnew="";
		String addold="";
		
		String[] tpltypes = tpltype.split(",");
		String[] typenames = typename.split(",");
		
		AjaxResult<String> rst = new AjaxResult<>();
		boolean boo=false;
		
		for(int i=0;i<tpltypes.length;i++){
			String fileName="";
			
			if(!"Other".equals(tpltypes[i])){
				fileName = tpltypes[i]+".ftl";
				
				if("1".equals(flag)){
					boo=FileTool.createLocalFile(
							FileTool.getRealPath(GlobalConst.TEMPLATE_PATH+"/"
							+dir), fileName);
				}else{
					boo=FileTool.createLocalFile(
							FileTool.getRealPath(GlobalConst.TEMPLATE_PATH)
								+File.separator+"tpl_"+tplname, fileName);
				}
				if(boo){
					addnew+=tpltypes[i]+",";
				}else{
					addold+=tpltypes[i]+",";
				}
			}else{
				for(int j=0;j<typenames.length;j++){
					fileName = typenames[j].trim()+".ftl";
					
					if("1".equals(flag)){
						boo=FileTool.createLocalFile(
								FileTool.getRealPath(GlobalConst.TEMPLATE_PATH+"/"+dir), fileName);
					}else{
						boo=FileTool.createLocalFile(
								FileTool.getRealPath(GlobalConst.TEMPLATE_PATH)
								+File.separator+"tpl_"+tplname, fileName);
					}
					if(boo){
						addnew+=typenames[j]+",";
					}else{
						addold+=typenames[j]+",";
					}
				}
				
			}
			
		}
		
		if(addold.equals("")){
			rst.setCode("0");
		}else{
			rst.setCode("1");
			if(!addnew.equals("")){
				rst.setMsg("新增了"+addnew.substring(0,addnew.length()-1)+"模板文件，"
						+addold.substring(0,addold.length()-1)+"模板文件已存在！");
			}else{
				rst.setMsg(addold.substring(0,addold.length()-1)+"模板文件已存在！");
			}
			
		}
		return rst;
	}
	
	@RequestMapping(value = "/generateCode")
	@ResponseBody
	public AjaxResult<String> generateCode(String tplname){
		
		AjaxResult<String> rst = new AjaxResult<>();
		
		//生成代码
        try {
        	
        	genCode.generateFiles(GlobalConst.CONFIG,
        			GlobalConst.TEMPLATE_PATH+"/"+tplname);
		} catch (Exception e) {
			e.printStackTrace();
			rst.setCode("500");
			return rst;
		} 
		
		rst.setCode("0");
		return rst;
	}
	
	
}
