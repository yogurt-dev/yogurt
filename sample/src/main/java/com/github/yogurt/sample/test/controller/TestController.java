package com.github.yogurt.sample.test.controller;

import com.github.yogurt.core.exception.ServiceException;
import org.springframework.http.HttpStatus;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.github.yogurt.core.controller.BaseController;
import com.github.yogurt.sample.test.po.TestPO;
import com.github.yogurt.sample.test.service.TestService;
import com.github.yogurt.core.annotations.LogContent;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/tests")
public class TestController extends BaseController {

	/**
	 * test服务类
	 */
	@Resource
	private TestService testService;

	/**
	 * 查询test
	 */
	@LogContent("查询test")
	@GetMapping
	public ResponseEntity<?> list(TestPO testPO , Pageable pageable) {
        return new ResponseEntity<>(testService.list(testPO,pageable),HttpStatus.OK);
	}

   /**
    * 查询单个test
	  */
	 @LogContent("查询单个test")
	 @GetMapping(value = "/{id}")
	 public ResponseEntity<?> get(@PathVariable Long id) {
		 return new ResponseEntity<>(testService.findById(id),HttpStatus.OK);
	 }

	/**
	 * 添加test
	 */
	@LogContent("添加test")
	@PostMapping
	public ResponseEntity<?> save(@RequestBody TestPO testPO) throws ServiceException {
		testService.save(testPO);
        return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 编辑test
	 */
	@LogContent("编辑test")
	@PutMapping
	public ResponseEntity<?> update(@RequestBody TestPO testPO) {
		testService.updateForSelective(testPO);
        return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 删除单个test
	 */
	@LogContent("删除test")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> logicDelete(@PathVariable Long id) {
		try {
			testService.logicDelete(id);
		} catch (ServiceException e) {
			return new ResponseEntity<>("操作失败",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
