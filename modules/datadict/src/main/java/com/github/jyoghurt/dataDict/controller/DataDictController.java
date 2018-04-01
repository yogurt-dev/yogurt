package com.github.jyoghurt.dataDict.controller;

import com.github.jyoghurt.dataDict.domain.DataDictItem;
import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.dataDict.enums.ErrorCode;
import com.github.jyoghurt.dataDict.enums.SysVarEnum;
import com.github.jyoghurt.dataDict.exception.DataDictValueException;
import com.github.jyoghurt.dataDict.service.DataDictItemService;
import com.github.jyoghurt.dataDict.service.DataDictUtils;
import com.github.jyoghurt.dataDict.service.DataDictValueService;
import com.github.jyoghurt.dataDict.util.NumberUtils;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.handle.SQLJoinHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by dell on 2016/1/15.
 *
 * @author baoxiaobing@lvyushequ.com
 * @Date 2016/1/29
 * @Desc 新增字典项 code不能重名的 请求 checkItemCodeDulp
 */
@RestController
@LogContent(module = "数据字典")
@RequestMapping("/dataDict")
public class DataDictController extends BaseController {
    @Resource
    private DataDictItemService dataDictItemService;
    @Resource
    DataDictValueService dataDictValueService;

    @LogContent("清除数据字典缓存")
    @RequestMapping(value = "/cacheEvict", method = RequestMethod.GET)
    public HttpResultEntity<?> cacheEvict() {
        DataDictUtils.cacheEvict();
        return getSuccessResult();
    }

    @LogContent("根据字典项查询字典值列表")
    @RequestMapping(value = "/getDataDictValuesByItemCode/{dictItemCode}", method = RequestMethod.GET)
    public HttpResultEntity<?> getDataDictValuesByItemCode(@PathVariable String dictItemCode) {
        return getSuccessResult(DataDictUtils.getDataDictValuesByItemCode(dictItemCode));
    }

    @LogContent("根据字典项查询字典值列表")
    @RequestMapping(value = "/dataDictValue/list", method = RequestMethod.GET)
    public HttpResultEntity<?> getDictValues(String dictItemCode, String parentDictValueCode) {
        return getSuccessResult(DataDictUtils.getDataDictValues(dictItemCode, parentDictValueCode, null));
    }


    @LogContent("根据字典项查询字典值树")
    @RequestMapping(value = "/dataDictValue/tree", method = RequestMethod.GET)
    public HttpResultEntity<?> getDictValuesTree(String dictItemCode, String dictValueCode) {
        return getSuccessResult(DataDictUtils.getDataDictAndSubValues(dictItemCode, dictValueCode));
    }

    @LogContent("查询字典值")
    @RequestMapping(value = "/dataDictValue/{dictItemCode}/{dictValueCode}", method = RequestMethod.GET)
    public HttpResultEntity<?> getDictValue(@PathVariable String dictItemCode, @PathVariable String dictValueCode) {
        return getSuccessResult(DataDictUtils.getDataDictValue(dictItemCode, dictValueCode));
    }

    @LogContent("根据字典项查询字典值列表")
    @RequestMapping(value = "/getDataDictValues/{dictItemCode}/{dictValueDesc}", method = RequestMethod.GET)
    public HttpResultEntity<?> getDataDictValues(@PathVariable String dictItemCode, @PathVariable String dictValueDesc) {
        return getSuccessResult(DataDictUtils.geDataDictValues(dictItemCode, dictValueDesc));
    }

    @LogContent("根据字典项及父字典值查询字典值列表")
    @RequestMapping(value = "/getDataDictValuesByParent/{dictItemCode}/{parentDictValueCode}", method = RequestMethod.GET)
    public HttpResultEntity<?> getDataDictValuesByParent(@PathVariable String dictItemCode, @PathVariable String parentDictValueCode) {
        return getSuccessResult(DataDictUtils.getDataDictValues(dictItemCode, parentDictValueCode, null));
    }

    @LogContent("根据字典项及父字典值查询字典值列表")
    @RequestMapping(value = "/getDataDictValuesByParentAndDesc/{dictItemCode}/{parentDictValueCode}/{dictValueDesc}", method = RequestMethod.GET)
    public HttpResultEntity<?> getDataDictValuesByParentAndDesc(@PathVariable String dictItemCode, @PathVariable String parentDictValueCode, @PathVariable String dictValueDesc) {
        return getSuccessResult(DataDictUtils.getDataDictValues(dictItemCode, parentDictValueCode, dictValueDesc));
    }

    @LogContent("根据字典项查询字典值列表")
    @RequestMapping(value = "/dataDictValueName/{dictItemCode}/{dictValueCode}/{extend}", method = RequestMethod.GET)
    public HttpResultEntity<?> getDataDictValueName(@PathVariable String dictItemCode, @PathVariable String dictValueCode, @PathVariable Boolean extend) {
        return getSuccessResult(DataDictUtils.getDataDictValueName(dictItemCode, dictValueCode, extend));
    }

    /**
     * 列出数据项
     */
    @LogContent("查询数据项")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpResultEntity<?> list(DataDictItem dataDictItem, QueryHandle queryHandle) {
        dataDictItem.setUiConfigurable(true);
        dataDictItem.setDeleteFlag(false);
        return getSuccessResult(dataDictItemService.getData(dataDictItem, queryHandle.configPage().addOrderBy("createDateTime",
                "desc").addJoinHandle("p.dictItemName as parentDictItemName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN,
                "DataDictItem p on t.parentDictItemId = p.dictItemId")));

    }

    /**
     * 查询父数据项
     */
    @LogContent("查询父数据项")
    @RequestMapping(value = "/queryItemForDict", method = RequestMethod.GET)
    public HttpResultEntity<?> queryItemForDict(DataDictItem dataDictItem, QueryHandle queryHandle) {
        return getSuccessResult(dataDictItemService.findAll(dataDictItem.setDeleteFlag(false), queryHandle.configPage()
                .addOrderBy("createDateTime", "desc")));
    }

    /**
     * 添加数据项
     */
    @LogContent("添加数据项")
    @RequestMapping(method = RequestMethod.POST)
    public HttpResultEntity<?> add(@RequestBody DataDictItem dataDictItem) {
        dataDictItemService.save(dataDictItem);
        return getSuccessResult();
    }

    /**
     * 编辑数据项
     */
    @LogContent("编辑数据项")
    @RequestMapping(method = RequestMethod.PUT)
    public HttpResultEntity<?> edit(@RequestBody DataDictItem dataDictItem) {
        dataDictItemService.updateForSelective(dataDictItem);
        return getSuccessResult();
    }

    /**
     * 逻辑删除当前节点和子节点.
     *
     * @param dictItemCode dictItemCode
     */
    @LogContent("删除数据项")
    @RequestMapping(value = "/{dictItemCode}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delete(@PathVariable String dictItemCode) {
        dataDictItemService.logicDeleteDictItemAndSubItemsByCode(dictItemCode);
        return getSuccessResult();
    }

    /**
     * 查询单个数据项.
     */
    @LogContent("查询单个数据项")
    @RequestMapping(value = "/{dictItemId}", method = RequestMethod.GET)
    public HttpResultEntity<?> get(@PathVariable String dictItemId) {
        return getSuccessResult(dataDictItemService.find(dictItemId));
    }

    /**
     * 添加数据项
     */
    @LogContent("新增数据值")
    @RequestMapping(value = "/addValue", method = RequestMethod.POST)
    public HttpResultEntity<?> addValue(@RequestBody DataDictValue dataDictValue, boolean flag) throws DataDictValueException {
        dataDictValue.setDictItemCode(dataDictValue.getParentDictItemCode());
        dataDictValueService.addDataDictValue(dataDictValue, flag);
        return getSuccessResult();
    }

    /**
     * 添加数据项
     */
    @LogContent("编辑数据值")
    @RequestMapping(value = "/editValue", method = RequestMethod.PUT)
    public HttpResultEntity<?> editValue(@RequestBody DataDictValue dataDictValue, boolean flag) throws DataDictValueException {
        dataDictValue.setDictItemCode(dataDictValue.getParentDictItemCode());
        dataDictValueService.updateDataDictValue(dataDictValue, flag);
        return getSuccessResult();
    }

    /**
     * 删除单个数据值
     */
    @LogContent("删除单个数据值")
    @RequestMapping(value = "/deleteValue/{dictValueId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> deleteValue(@PathVariable String dictValueId) {
        DataDictValue dataDictValue = dataDictValueService.find(dictValueId);
        dataDictValue.setDeleteFlag(true);
        dataDictValueService.update(dataDictValue);
        return getSuccessResult();
    }


    /**
     * 查询父字典值编码
     */
    @LogContent("查询父字典值编码")
    @RequestMapping(value = "/queryValueForDict", method = RequestMethod.GET)
    public HttpResultEntity<?> queryValueForDict(DataDictValue dataDictValue, QueryHandle queryHandle) {
        return getSuccessResult(dataDictValueService.findAll(dataDictValue.setDeleteFlag(false), queryHandle.configPage()
                .addOrderBy("createDateTime", "desc")));
    }


    /**
     * 根据字典项查询字典值
     */
    @LogContent("根据字典项查询字典值")
    @RequestMapping(value = "/listChildValue/{dictItemCode}", method = RequestMethod.GET)
    public HttpResultEntity<?> listChildValue(@PathVariable String dictItemCode) {
        return getSuccessResult(dataDictValueService.getData(new DataDictValue().setDictItemCode(dictItemCode)
                .setDeleteFlag(false), new QueryHandle().configPage().addOrderBy("createDateTime", "desc")
                .addJoinHandle("i.dictItemName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN, "DataDictItem i on t.dictItemCode = i.dictItemCode")
        ));
    }


    /**
     * 检查 字典项Code是否有重名
     * 如果是修改 判断增加去除自己
     * 如果是新增 直接判断是否有重复
     *
     * @param dataDictItem 字典项
     * @return
     */
    @LogContent("检查 字典项Code是否有重名")
    @RequestMapping(value = "/checkItemCodeDulp", method = RequestMethod.PUT)
    public HttpResultEntity<?> checkItemCodeDulp(@RequestBody DataDictItem dataDictItem) {
        Boolean modify = StringUtils.isNotEmpty(dataDictItem.getDictItemId()) ? true : false;
        String result;

        if (modify) {
            //检查是否有重复数据
            List<DataDictItem> dataDictItems = dataDictItemService.findAll(new DataDictItem().setDeleteFlag(false),
                    new QueryHandle().addWhereSql("t.dictItemCode = '".concat(dataDictItem.getDictItemCode()
                            .concat("' and t.dictItemId<>'").concat(dataDictItem.getDictItemId()).concat("'"))));
            result = CollectionUtils.isNotEmpty(dataDictItems) ? SysVarEnum.YES_STATICVAR.getCode() : SysVarEnum
                    .NO_STATICVAR.getCode();
            return getSuccessResult(result);
        }

        List<DataDictItem> dataDictItemList = dataDictItemService.findAll(new DataDictItem().setDeleteFlag(false)
                .setDictItemCode(dataDictItem.getDictItemCode()));
        result = CollectionUtils.isNotEmpty(dataDictItemList) ? SysVarEnum.YES_STATICVAR.getCode() : SysVarEnum
                .NO_STATICVAR.getCode();

        return getSuccessResult(result);
    }


    /**
     * 检查 字典值Code是否有重名
     * 如果是新增 同一字典项下 的 字典值不能重名
     * 如果是修改 判断 其他字典项下 是否有重名
     *
     * @param dataDictValue 字典值
     * @return
     * @author baoxiaobing@lvyushequ.com
     */
    @LogContent("检查 字典值Code是否有重名")
    @RequestMapping(value = "/checkValueCodeDulp", method = RequestMethod.PUT)
    public HttpResultEntity<?> checkValueCodeDulp(@RequestBody DataDictValue dataDictValue) {

        Boolean modify = StringUtils.isNotEmpty(dataDictValue.getDictValueId()) ? true : false;
        String result;

        if (modify) {
            //检查是否有重复数据
            List<DataDictValue> dataDictValues = dataDictValueService.findAll(new DataDictValue().setDeleteFlag(false),
                    new QueryHandle().addWhereSql("(t.dictValueCode = '".concat(dataDictValue.getDictValueCode()).concat("' " +
                            "and t.dictItemCode = '").concat(dataDictValue.getDictItemCode()).concat("') and t.dictValueId" +
                            " <> '").concat(dataDictValue.getDictValueId()).concat("'")));
            result = CollectionUtils.isNotEmpty(dataDictValues) ? SysVarEnum.YES_STATICVAR.getCode() : SysVarEnum
                    .NO_STATICVAR.getCode();

            return getSuccessResult(result);
        }

        List<DataDictValue> dataDictValues = dataDictValueService.findAll(new DataDictValue().setDeleteFlag(false),
                new QueryHandle().addWhereSql("t.dictValueCode = '".concat(dataDictValue.getDictValueCode()).concat("' and t" +
                        ".dictItemCode = '").concat(dataDictValue.getDictItemCode()).concat("'")));
        result = CollectionUtils.isNotEmpty(dataDictValues) ? SysVarEnum.YES_STATICVAR.getCode() : SysVarEnum
                .NO_STATICVAR.getCode();

        return getSuccessResult(result);

    }

    /**
     * 根据数据字典项code加载数据项树. add by limiao 20170721
     */
    @LogContent("根据数据字典项code加载数据项树")
    @RequestMapping(value = "/getSubDataDictTree", method = RequestMethod.GET)
    public HttpResultEntity<?> getSubDataDictTree() {
        String dataDictItemCode = request.getParameter("dataDictItemCode");
        if (StringUtils.isEmpty(dataDictItemCode)) {
            return getSuccessResult(dataDictItemService.findAll(new DataDictItem().setDeleteFlag(false), new QueryHandle()));
        } else {
            return getSuccessResult(dataDictItemService.findSubDataDictItemByDictItemCode(dataDictItemCode));
        }
    }

    /**
     * 添加数据项
     * add by limiao 20160202
     */
    @LogContent("添加数据项")
    @RequestMapping(value = "/addDataDictItem", method = RequestMethod.POST)
    public HttpResultEntity<?> addDataDictItem(@RequestBody DataDictItem dataDictItem) {
        if (dataDictItemService.checkUniqueDataDictItem(dataDictItem.getDictItemCode())) {
            if (dataDictItem.getSortNum() == null) {
                Integer sortNum = dataDictItemService.getDictItemMaxSortNum(dataDictItem.getParentDictItemCode());
                dataDictItem.setSortNum(sortNum + 1);
            }
            dataDictItem.setUiConfigurable(true);
            dataDictItemService.save(dataDictItem);
            return getSuccessResult();
        }
        return new HttpResultEntity(ErrorCode.UNIQUE_DATA_9001, "字典项编码:[" + dataDictItem.getDictItemCode() + "]不能重复!");
    }

    /**
     * 更新数据项
     * modify by limiao 20160202 ,增加唯一性校验
     */
    @LogContent("更新数据项")
    @RequestMapping(value = "/updateDataDictItem", method = RequestMethod.PUT)
    public HttpResultEntity<?> updateDataDictItem(@RequestBody DataDictItem dataDictItem) {
        if (dataDictItemService.checkUniqueDataDictItem(dataDictItem.getDictItemCode(), dataDictItem.getDictItemId())) {
            dataDictItem.setSortNum(NumberUtils.nullToZero(dataDictItem.getSortNum()));
            dataDictItemService.updateForSelective(dataDictItem);
            return getSuccessResult();
        }
        return new HttpResultEntity(ErrorCode.UNIQUE_DATA_9001, "字典项编码:[" + dataDictItem.getDictItemCode() + "]不能重复!");
    }


    /**
     * 获取
     *
     * @param dictItemCode
     * @return
     * @
     */
    @RequestMapping(value = "/getItemPath/{dictItemCode}", method = RequestMethod.GET)
    public HttpResultEntity<?> getItemPath(@PathVariable String dictItemCode) {
        return getSuccessResult(dataDictItemService.getItemPath(dictItemCode));
    }
}
