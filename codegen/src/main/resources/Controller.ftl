package ${modulePackage}.controller;

import org.springframework.http.HttpStatus;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.github.yogurt.core.controller.BaseController;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.ao.${className}AO;
import ${modulePackage}.service.${className}Service;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;

/**
 * @author ${userName}
 */
@Api(tags = "${tableComment}")
@RestController
@RequestMapping("/${lowerName}s")
public class ${className}Controller extends BaseController {



	@Resource
	private ${className}Service ${lowerName}Service;


//	@ApiOperation(value = "${tableComment}")
//	@GetMapping
//	public ResponseEntity list(${className}AO ${lowerName}AO , Pageable pageable) {
//        return new ResponseEntity<>(${lowerName}Service.findAll(${lowerName}AO,pageable),HttpStatus.OK);
//	}
//
//	@ApiImplicitParam("主键")
//	@ApiOperation(value = "查询单个${tableComment}")
//	@GetMapping(value = "<#list priKeys as priKey>/{${priKey.codeName}}</#list>")
//	public ResponseEntity get( <#list priKeys as priKey>@PathVariable ${priKey.className} ${priKey.codeName}<#if priKey_has_next>,</#if></#list>) {
<#if (priKeys?size=1)>
//		return new ResponseEntity<>(${lowerName}Service.findById(${priKeys[0].codeName}),HttpStatus.OK);
</#if>
<#if (priKeys?size>1)>
//		return new ResponseEntity<>(${lowerName}Service.findById(new ${className}PO()<#list priKeys as priKey>.set${priKey.codeName?cap_first}(${priKey.codeName})</#list>),HttpStatus.OK);
</#if>
//	}
//
//	@ApiOperation(value = "添加${tableComment}")
//	@PostMapping
//	public ResponseEntity save(@RequestBody ${className}PO ${lowerName}PO){
//		${lowerName}Service.save(${lowerName}PO);
//        return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@ApiOperation(value = "编辑${tableComment}")
//	@PutMapping
//	public ResponseEntity update(@RequestBody ${className}PO ${lowerName}PO) {
//		${lowerName}Service.updateForSelective(${lowerName}PO);
//        return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@ApiOperation(value = "删除单个${tableComment}")
//	@ApiImplicitParam("主键")
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
