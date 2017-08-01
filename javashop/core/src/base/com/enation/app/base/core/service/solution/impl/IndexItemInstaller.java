package com.enation.app.base.core.service.solution.impl;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.eop.resource.IIndexItemManager;
import com.enation.eop.resource.model.IndexItem;


/**
 * 后台首页项安装器
 * @author kingapex
 * 2010-10-12下午03:09:57
 */
@Service
public class IndexItemInstaller implements IInstaller {

	@Autowired
	private IIndexItemManager indexItemManager;
	 
	
	/**
	 * 根据后台首页片段安装首页项
	 */
	public void install(String productId, Node fragment) {
		//this.indexItemManager.clean();
		NodeList nodeList = fragment.getChildNodes();
		int sort =1;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				 this.install((Element)node, sort);
				 sort++;
				 
			}
		}		
	}
	
	/**
	 * 根据首页项结点安装 首页项
	 * @param ele 首页项结点
	 */
	public void install(Element ele,int sort){
		 Element titleEl =this.getChild(ele, "title");
		 Element urlEl =this.getChild(ele, "url");
		 IndexItem item = new IndexItem();
		 item.setTitle( titleEl.getFirstChild().getNodeValue());
		 item.setUrl(urlEl.getFirstChild().getNodeValue());
		 item.setSort(sort);
		 
		 this.indexItemManager.add(item);
	}
	
	
	/**
	 * 根据节点名称 获取某个节点的子
	 * @param ele
	 * @param name
	 * @return
	 */
	private Element getChild(Element ele,String name){
		 NodeList childList  =  ele.getElementsByTagName(name);
		 Element child =(Element) childList.item(0);
		 return child;
	}

}
