package ${modulePackage}.controller;

import com.github.yogurt.core.exception.ServiceException;
import org.springframework.http.HttpStatus;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.github.yogurt.core.controller.BaseController;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.service.${className}Service;
import com.github.yogurt.core.annotations.LogContent;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * ${lowerName}控制器
 *
 */
@RestController
@RequestMapping("/${lowerName}s")
public class ${className}Controller extends BaseController {


	/**
	 * ${lowerName}服务类
	 */
	@Resource
	private ${className}Service ${lowerName}Service;

//	/**
//	 * 查询${lowerName}
//	 */
//	@LogContent("查询${lowerName}")
//	@GetMapping
//	public ResponseEntity<?> list(${className}PO ${lowerName}PO , Pageable pageable) {
//        return new ResponseEntity<>(${lowerName}Service.list(${lowerName}PO,pageable),HttpStatus.OK);
//	}
//
//   /**
//    * 查询单个${lowerName}
//	  */
//	 @LogContent("查询单个${lowerName}")
//	 @GetMapping(value = "/{${priKey.codeName}}")
//	 public ResponseEntity<?> get(@PathVariable ${priKey.className} ${priKey.codeName}) {
//		 return new ResponseEntity<>(${lowerName}Service.findById(${priKey.codeName}),HttpStatus.OK);
//	 }
//
//	/**
//	 * 添加${lowerName}
//	 */
//	@LogContent("添加${lowerName}")
//	@PostMapping
//	public ResponseEntity<?> add(@RequestBody ${className}PO ${lowerName}PO) throws ServiceException {
//		${lowerName}Service.save(${lowerName}PO);
//        return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	/**
//	 * 编辑${lowerName}
//	 */
//	@LogContent("编辑${lowerName}")
//	@PutMapping
//	public ResponseEntity<?> edit(@RequestBody ${className}PO ${lowerName}PO) {
//		${lowerName}Service.update(${lowerName}PO);
//        return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	/**
//	 * 删除单个${lowerName}
//	 */
//	@LogContent("删除${lowerName}")
//	@DeleteMapping(value = "/{${priKey.codeName}}")
//	public ResponseEntity<?> logicDelete(@PathVariable ${priKey.className} ${priKey.codeName}) {
//
//		try {
//			${lowerName}Service.logicDelete(${priKey.codeName});
//		} catch (ServiceException e) {
//			return new ResponseEntity<>("操作失败",HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//

}
