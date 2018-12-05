package com.github.yogurt.sample.test.controller;

import org.springframework.http.HttpStatus;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.github.yogurt.core.controller.BaseController;
import com.github.yogurt.sample.test.po.TestPO;
import com.github.yogurt.sample.test.service.TestService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/tests")
public class TestController extends BaseController {



	@Resource
	private TestService testService;

	/**
	 * 查询测试
	 */
	@GetMapping
	public ResponseEntity<?> list(TestPO testPO , Pageable pageable) {
        return new ResponseEntity<>(testService.list(testPO,pageable),HttpStatus.OK);
	}

   /**
    * 查询单个测试
	  */
	 @GetMapping(value = "/{id}")
	 public ResponseEntity<?> get( @PathVariable Long id) {
		 return new ResponseEntity<>(testService.findById(id),HttpStatus.OK);
	 }

	/**
	 * 添加测试
	 */
	@PostMapping
	public ResponseEntity<?> save(@RequestBody TestPO testPO){
		testService.save(testPO);
        return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 编辑测试
	 */
	@PutMapping
	public ResponseEntity<?> update(@RequestBody TestPO testPO) {
		testService.update(testPO);
        return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 删除单个测试
	 */
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> logicDelete(@PathVariable Long id) {
		testService.logicDelete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}


}
