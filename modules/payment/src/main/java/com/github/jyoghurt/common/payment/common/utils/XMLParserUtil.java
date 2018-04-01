package com.github.jyoghurt.common.payment.common.utils;


import com.github.jyoghurt.core.exception.UtilException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rizenguo
 * Date: 2014/11/1
 * Time: 14:06
 */
public class XMLParserUtil {
    /**
     * 安全的解析xmlString  用Dom方式防止API新增字段
     *
     * @param xmlString
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Map<String, Object> getMapFromXML(String xmlString) throws UtilException {
        try {
            //这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = CommonUtil.getStringStream(xmlString);
            Document document = builder.parse(is);
            //获取到document里面的全部结点
            NodeList allNodes = document.getFirstChild().getChildNodes();
            Node node;
            Map<String, Object> map = new HashMap<>();
            int i = 0;
            while (i < allNodes.getLength()) {
                node = allNodes.item(i);
                if (node instanceof Element) {
                    map.put(node.getNodeName(), node.getTextContent());
                }
                i++;
            }
            return map;
        } catch (ParserConfigurationException e) {
            throw new UtilException("解析xml工具类异常",e);
        } catch (IOException e) {
            throw new UtilException("解析xml工具类异常",e);
        } catch (SAXException e) {
            throw new UtilException("解析xml工具类异常",e);
        } catch (Exception e) {
            throw new UtilException("解析xml工具类异常",e);
        }
    }
}
