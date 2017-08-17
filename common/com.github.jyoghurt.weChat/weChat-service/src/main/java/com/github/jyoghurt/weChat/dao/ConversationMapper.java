package com.github.jyoghurt.weChat.dao;

import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.weChat.domain.WeChatConversationT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Conversation Mapper
 */
public interface ConversationMapper extends BaseMapper<WeChatConversationT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatConversationT selectById(@Param(ENTITY_CLASS) Class<WeChatConversationT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatConversationT> pageData(@Param(ENTITY_CLASS) Class<WeChatConversationT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatConversationT> findAll(@Param(ENTITY_CLASS) Class<WeChatConversationT> entityClass, @Param(DATA) Map<String, Object> data);

    /*刷新未回复在线联系人*/
    @Select("select * from(select * from\n" +
            "(select n.createDateTime,n.founderId,n.fromUserId,n.fromUserName,n.ifreply,n.receive,n.context,n.img,\n" +
            "r.appId,r.appName,r.appsecret from WeChatConversationT n LEFT JOIN WeChatRelevanceT r on n.fromUserId=r.openId\n" +
            "where  r.appName like '%${appName}%' and n.ifreply='0'\n" +
            "and r.nickName like '%${nickName}%' ORDER BY\n" +
            " n.createDateTime DESC ) c\n" +
            " GROUP BY  c.fromUserId ORDER BY c.ifreply,c.createDateTime DESC) s\n " +
            "where (UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(s.createDateTime)<2*23*60*60)")
    List<JSONObject> refurbishChat(@Param("appName") String appName, @Param("nickName") String nickName);

    /*根据条件搜索查询在线联系人*/
    @Select("select * from(select * from\n" +
            "(select n.createDateTime,n.founderId,n.fromUserId,n.fromUserName,n.ifreply,n.receive,n.context,n.img,\n" +
            "r.appId,r.appName,r.appsecret from WeChatConversationT n LEFT JOIN WeChatRelevanceT r on n.fromUserId=r.openId\n" +
            "where r.appName like '%${appName}%'\n " +
            "and r.nickName like '%${nickName}%'\n" +
            "ORDER BY n.createDateTime DESC ) c\n" +
            " GROUP BY  c.fromUserId ORDER BY c.ifreply,c.createDateTime DESC) s\n " +
            "where (UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(s.createDateTime)<2*23*60*60)")
    List<JSONObject> initSearch(@Param("appName") String appName, @Param("nickName") String nickName);

    /*根据条件搜索查询历史联系人*/
    @Select("select * from(select * from\n" +
            "(select n.createDateTime,n.founderId,n.fromUserId,n.fromUserName,n.ifreply,n.receive,n.context,n.img,\n" +
            "r.appId,r.appName,r.appsecret from WeChatConversationT n LEFT JOIN WeChatRelevanceT r on \n" +
            "n.fromUserId=r.openId\n" +
            "where  r.appName like '%${appName}%'\n" +
            "and r.nickName like '%${nickName}%'\n" +
            "ORDER BY n.createDateTime DESC ) c\n" +
            "GROUP BY  c.fromUserId ORDER BY c.ifreply,c.createDateTime DESC) s\n " +
            "where (UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(s.createDateTime)>=2*23*60*60) order by s.createDateTime desc")
    List<JSONObject> initSearchHis(@Param("appName") String appName, @Param("nickName") String nickName);

    /*2天内在线联系人查询*/
    @Select("select * from(select * from\n" +
            "(select n.createDateTime,n.founderId,n.fromUserId,n.fromUserName,n.ifreply,n.receive,n.context,n.img,\n" +
            "r.appId,r.appName,r.appsecret from WeChatConversationT n LEFT JOIN WeChatRelevanceT r on n.fromUserId=r.openId\n" +
            "where (r.appId=#{appId} or\n" +
            " r.appId=#{appIdF} or r.appId=#{appIdQ}) ORDER BY n\n" +
            ".createDateTime DESC ) c\n" +
            " GROUP BY  c.fromUserId ORDER BY c.ifreply,c.createDateTime DESC LIMIT #{limitStart},#{limitEnd}) s\n" +
            "where (UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(s.createDateTime)<2*23*60*60) order by s.createDateTime desc")
    List<JSONObject> initConversation(@Param("limitStart") int limitStart,
                                      @Param("limitEnd") int limitEnd, @Param("appId") String appId, @Param("appIdF") String appIdF,
                                      @Param("appIdQ") String appIdQ);

    /*2天外历史联系人查询*/
    @Select("select * from(select * from\n" +
            "(select n.createDateTime,n.founderId,n.fromUserId,n.fromUserName,n.ifreply,n.receive,n.context,n.img,r.appId,r.appName,r.appsecret from WeChatConversationT n LEFT JOIN WeChatRelevanceT r on n.fromUserId=r.openId\n" +
            "where r.appId=#{appId}\n" +
            "ORDER BY n.createDateTime DESC ) c\n" +
            " GROUP BY  c.fromUserId ORDER BY c.ifreply,c.createDateTime DESC LIMIT #{limitStart},#{limitEnd}) s\n" +
            "where (UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(s.createDateTime)>=2*23*60*60) order by s.createDateTime desc")
    List<JSONObject> initHisConversation(@Param("limitStart") int limitStart,
                                         @Param("limitEnd") int limitEnd, @Param("appId") String appId);
}
