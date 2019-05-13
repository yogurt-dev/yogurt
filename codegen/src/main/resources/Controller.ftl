package ${modulePackage}.controller;

import org.springframework.http.HttpStatus;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.github.yogurt.core.controller.BaseController;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.service.${className}Service;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * @author ${userName}
 */
@RestController
@RequestMapping("/${lowerName}s")
public class ${className}Controller extends BaseController {



	@Resource
	private ${className}Service ${lowerName}Service;

//	/**
//	 * 查询${tableComment}
//	 */
//	@GetMapping
//	public ResponseEntity list(${className}PO ${lowerName}PO , Pageable pageable) {
//        return new ResponseEntity<>(${lowerName}Service.list(${lowerName}PO,pageable),HttpStatus.OK);
//	}
//
//   /**
//    * 查询单个${tableComment}
//	  */
//	 @GetMapping(value = "<#list priKeys as priKey>/{${priKey.codeName}}</#list>")
//	 public ResponseEntity get( <#list priKeys as priKey>@PathVariable ${priKey.className} ${priKey.codeName}<#if priKey_has_next>,</#if></#list>) {
<#if (priKeys?size=1)>
//		 return new ResponseEntity<>(${lowerName}Service.findById(${priKeys[0].codeName}),HttpStatus.OK);
</#if>
<#if (priKeys?size>1)>
//		 return new ResponseEntity<>(${lowerName}Service.findById(new ${className}PO()<#list priKeys as priKey>.set${priKey.codeName?cap_first}(${priKey.codeName})</#list>),HttpStatus.OK);
</#if>
//	 }
//
//	/**
//	 * 添加${tableComment}
//	 */
//	@PostMapping
//	public ResponseEntity save(@RequestBody ${className}PO ${lowerName}PO){
//		${lowerName}Service.save(${lowerName}PO);
//        return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	/**
//	 * 编辑${tableComment}
//	 */
//	@PutMapping
//	public ResponseEntity update(@RequestBody ${className}PO ${lowerName}PO) {
//		${lowerName}Service.update(${lowerName}PO);
//        return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	/**
//	 * 删除单个${tableComment}
//	 */
//	@DeleteMapping(value = "<#list priKeys as priKey>/{${priKey.codeName}}</#list>")
//	public ResponseEntity logicDelete(<#list priKeys as priKey>@PathVariable ${priKey.className} ${priKey.codeName}<#if priKey_has_next>,</#if></#list>) {
<#if (priKeys?size=1)>
//		${lowerName}Service.logicDelete(${priKeys[0].codeName});
</#if>
<#if (priKeys?size>1)>
//		${lowerName}Service.logicDelete(new ${className}PO()<#list priKeys as priKey>.set${priKey.codeName?cap_first}(${priKey.codeName})</#list>);
</#if>
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//

}
