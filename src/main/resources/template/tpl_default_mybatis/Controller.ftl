package ${basePackage}.${controllerPackage};

import ${basePackage}.${entityPackage}.${entName};
import ${basePackage}.${servicePackage}.${entName}Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

/**
 * ${remark!}
 */
@Controller
@RequestMapping(value = "/${lowEntName}")
public class ${entName}Controller {

    
    @Autowired
    private ${entName}Service ${lowEntName}Service;

    /**
    * 分页查询，显示列表
    */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model,${entName} ${lowEntName}) {
        List<${entName}> list = ${lowEntName}Service.find${entName}Page(${lowEntName});
        model.addAttribute("list", list);
        
        return "${lowEntName}/list";
    }
    /**
    * to新增页面
    */
    @RequestMapping(value = "/toAdd", method = RequestMethod.POST)
    public String toAdd(Model model){
       	
        return "${lowEntName}/add";
    }
     /**
    * to修改页面
    */
    @RequestMapping(value = "/toUpdate", method = RequestMethod.POST)
    public String toUpdate(Model model){
       	
        return "${lowEntName}/update";
    }
    /**
    * 新增
    */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(RedirectAttributes redirectAttributes, ${entName} ${lowEntName}){
       	
       	int rst = ${lowEntName}Service.insert${entName}(${lowEntName});
       	if(rst>0){
       		redirectAttributes.addFlashAttribute("msg", "新增成功");
       	}else{
       		redirectAttributes.addFlashAttribute("msg", "新增失败");
       	}
        return "redirect:/user/list";
    }
    <#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
   /**
    * 修改
    */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(RedirectAttributes redirectAttributes, ${entName} ${lowEntName}){
       	
       	int rst = ${lowEntName}Service.update${entName}By${key?lower_case?cap_first}(${lowEntName});
       	if(rst>0){
       		redirectAttributes.addFlashAttribute("msg", "修改成功");
       	}else{
       		redirectAttributes.addFlashAttribute("msg", "修改失败");
       	}
        return "redirect:/user/list";
    }
    /**
    * 删除
    */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(RedirectAttributes redirectAttributes, ${entName} ${lowEntName}){
       	
       	int rst = ${lowEntName}Service.delete${entName}By${key?lower_case?cap_first}(${lowEntName});
       	if(rst>0){
       		redirectAttributes.addFlashAttribute("msg", "删除成功");
       	}else{
       		redirectAttributes.addFlashAttribute("msg", "删除失败");
       	}
        return "redirect:/user/list";
    }
    </#list>
    </#if>
	
}
