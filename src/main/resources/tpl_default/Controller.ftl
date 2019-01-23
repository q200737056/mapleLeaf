package ${basePackage}.${controllerPackage};

import ${basePackage}.${entityPackage}.${entityName};
import ${basePackage}.${servicePackage}.${entityName}Service;
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
@RequestMapping(value = "/${fstLowEntityName}")
public class ${entityName}Controller {

    
    @Autowired
    private ${entityName}Service ${fstLowEntityName}Service;

    /**
    * 跳转到列表页面
    */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        Jedis jedis = RedisUtil.getJedis();
        Map<String, String> statusMap = jedis.hgetAll("dict:status");
        model.addAttribute("statusMap", statusMap);
        jedis.close();
        return "fav/${fstLowEntityName}/list${entityName}";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model, String id){
        if (id != null) {
            ${entityName} item = ${fstLowEntityName}Service.selectById(id);
            model.addAttribute("item", item);
        }
        Jedis jedis = RedisUtil.getJedis();
        Map<String, String> statusMap = jedis.hgetAll("dict:status");
        model.addAttribute("statusMap", statusMap);
        jedis.close();
        return "fav/${fstLowEntityName}/add${entityName}";
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(Model model, String id){
        if (id != null) {
            ${entityName} item = ${fstLowEntityName}Service.selectById(id);
            Jedis jedis = RedisUtil.getJedis();
            Map<String, String> statusMap = jedis.hgetAll("dict:status");
            model.addAttribute("statusMap", statusMap);
            jedis.close();
            model.addAttribute("item", item);
        }
        return "fav/${fstLowEntityName}/view${entityName}";
    }

    @RequestMapping(value = "/addAjax", method = RequestMethod.POST)
    @ResponseBody
    public ResultEntity addAjax(HttpServletRequest request, ${entityName} item) {
        ResultEntity res = new ResultEntity(ErrorCodeType.P_FAILURE, "失败", null);
        try {
            ${entityName} entity = ${fstLowEntityName}Service.selectById(item.getId());
            List<${entityName}> ${fstLowEntityName}List = ${fstLowEntityName}Service.selectList(
                new EntityWrapper<${entityName}>().eq("${fstLowEntityName}_name", item.get${entityName}Name()));
            if (entity == null) {
                if(${fstLowEntityName}List.size() > 0){
                    res.setMessage("名称已存在！");
                    return res;
                }
                item.setCreateTime(DateUtil.getToday("yyyy-MM-dd HH:mm:ss"));
                item.setCreateUser(getLoginUser(request).getUserName());
            }else{
                if(${fstLowEntityName}List.size() > 0){
                    if(!${fstLowEntityName}List.get(0).getId().equals(item.getId())){
                        res.setMessage("名称已存在！");
                        return res;
                    }
                }
            }
            item.setUpdateUser(getLoginUser(request).getUserName());
            item.setUpdateTime(DateUtil.getToday("yyyy-MM-dd HH:mm:ss"));
            boolean result = ${fstLowEntityName}Service.insertOrUpdate(item);
            if (result) {
                res.setErrorcode(ErrorCodeType.SUCCESS);
                res.setMessage("成功!");
                if (entity == null) {
                    logger(request,logType,item.get${entityName}Name(),"新增成功");
                }else{
                    logger(request,logType,item.get${entityName}Name(),"修改成功");
                }
            }else{
                if (entity == null) {
                    logger(request,logType,item.get${entityName}Name(),"新增失败");
                }else{
                    logger(request,logType,item.get${entityName}Name(),"修改失败");
                }
            }
        }catch (Exception e){
            logger(request,logType,item.get${entityName}Name(),"新增或修改异常");
            e.printStackTrace();
            logger.error("${entityName}Controller[addAjax]===="+e.toString());
        }
        return res;
    }

    @RequestMapping(value = "/queryPageList",method = RequestMethod.POST)
    @ResponseBody
    public ResultEntity queryPageList(Integer pageIndex, Integer rows,
    <#if columns??>
    <#list columns as col>
    <#if !col.pk >
    <#if col_index < 4>
    String ${col.propertyName},
    </#if>
    </#if>
    </#list>
    </#if>) {
        ResultEntity res = new ResultEntity(ErrorCodeType.P_FAILURE, "查询失败!", null);
        try {
            EntityWrapper<${entityName}> entityWrapper = new EntityWrapper<>();
            <#if columns??>
            <#list columns as col>
            <#if !col.pk >
            <#if col_index < 4>
            if(${col.propertyName} != null){
                entityWrapper.eq(StringUtil.upperCharToUnderLine("${col.propertyName}"),${col.propertyName});
            }
            </#if>
            </#if>
            </#list>
            </#if>
            if (rows == null) {
                rows = 10;
            }
            if (pageIndex == null) {
                pageIndex = 1;
            }
            Page<${entityName}> page = new Page<>(pageIndex, rows);
            Page<${entityName}> pageList = ${fstLowEntityName}Service.selectPage(page,
            entityWrapper.orderBy("update_time",false));
            List<${entityName}> list = pageList.getRecords();
            if (list != null) {
                res.setErrorcode(ErrorCodeType.SUCCESS);
                res.setMessage("查询成功!");
                res.setData(list);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("${entityName}Controller[queryPageList]===="+e.toString());
        }
        return res;
    }

    @RequestMapping(value = "/queryByCount",method = RequestMethod.POST)
    @ResponseBody
    public ResultEntity queryByCount(
    <#if columns??>
    <#list columns as col>
    <#if !col.pk >
    <#if col_index < 4>
    String ${col.propertyName},
    </#if>
    </#if>
    </#list>
    </#if>){
        ResultEntity res = new ResultEntity(ErrorCodeType.SUCCESS,"查询失败!",null);
        try {
            EntityWrapper<${entityName}> entityWrapper = new EntityWrapper<>();
            <#if columns??>
            <#list columns as col>
            <#if !col.pk >
            <#if col_index < 4>
            if(${col.propertyName} != null){
                entityWrapper.eq(StringUtil.upperCharToUnderLine("${col.propertyName}"),${col.propertyName});
            }
            </#if>
            </#if>
            </#list>
            </#if>
            long count = ${fstLowEntityName}Service.selectCount(entityWrapper);
            if (count >= 0) {
                res.setErrorcode(ErrorCodeType.SUCCESS);
                res.setMessage("查询成功!");
                res.setData(count);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("${entityName}Controller[queryByCount]===="+e.toString());
        }
        return res;
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.POST,produces = "application/json;charest=UTF-8")
    @ResponseBody
    public ResultEntity batchDelete(HttpServletRequest request,@RequestParam("id[]") List<String> idList){
        ResultEntity res = new ResultEntity(ErrorCodeType.P_FAILURE, "失败", null);
        try {
            boolean bool = ${fstLowEntityName}Service.deleteBatchIds(idList);
            if (bool) {
                res.setErrorcode(ErrorCodeType.SUCCESS);
                res.setMessage("成功!");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("${entityName}Controller[batchDelete]===="+e.toString());
        }
        return res;
    }
}
