package com.enation.app.shop.core.goods.service.batchimport.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.ImportDataSource;
import com.enation.app.shop.core.goods.model.support.GoodsTypeDTO;
import com.enation.app.shop.core.goods.plugin.goodsimp.GoodsImportPluginBundle;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.batchimport.IGoodsDataBatchManager;
import com.enation.app.shop.core.goods.service.batchimport.IGoodsDataImporter;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.XMLUtil;

/**
 * 默认的商品数据批量导入器实现
 * @author kingapex
 *
 */
public class GoodsDataBatchManager implements IGoodsDataBatchManager {
	protected final Logger logger = Logger.getLogger(getClass());
	private IBrandManager brandManager;
	private IGoodsTypeManager goodsTypeManager;
	private IGoodsCatManager goodsCatManager;
	private GoodsImportPluginBundle goodsImportPluginBundle;

	private IDaoSupport  daoSupport;
 
	
	@Transactional(propagation = Propagation.REQUIRED)  
	public void batchImport(String path,int imptype,int impcatid,Integer startNum,Integer endNum ) {
		
		
		if(this.logger.isDebugEnabled()){
			logger.debug("开始导入商品数据...");
		}
		
	 
		
		Document configDoc  = this.load(path);
		
		goodsImportPluginBundle.onBeforeImport(configDoc);
		
		Element configEl  = XMLUtil.getChildByTagName(configDoc, "config");
		
		//商品批量数据文件夹位置
		String datafolder = configEl.getAttribute("datafolder");
		
		//打开商品数据excel
		String excel = configEl.getAttribute("excel");
		Workbook goodsWb = this.openExcel(excel);
		

		
		//获取所有分类节点列表
		NodeList catNodeList = configEl.getElementsByTagName("cat");
		for(int i=0,len=catNodeList.getLength();i<len;i++){
			Node catNode  = catNodeList.item(i);
			
			if(imptype==2){
				int catid = Integer.valueOf( (XMLUtil.getChildByTagName(catNode, "id" )).getNodeValue() ) ;
				if(catid!= impcatid){
					continue;
				}
			}
			this.processSheet(datafolder, goodsWb, catNode,startNum,endNum);
			
		}
	}
	
	
	private void processSheet(String datafolder,Workbook goodsWb,Node catNode  ,Integer startNum,Integer endNum ){
		
		//找到sheet节点
		Element beforeSheetNode = XMLUtil.getChildByTagName(catNode, "beforesheet");
		
		//此类别数据的sheet位置
		int sheetIndex = Integer.valueOf( XMLUtil.getChildByTagName(catNode, "sheet_index" ).getNodeValue()  ) ; 
		
		//数据记录开始行位置
		int rowStartNum =0;
		if(startNum!=null) {
			rowStartNum = startNum;
		}else{
			rowStartNum =Integer.valueOf( XMLUtil.getChildByTagName(catNode, "start_rouwnum" ).getNodeValue() ) ; 
		}
		
		//此类别对应的类别id
		int catid = Integer.valueOf( XMLUtil.getChildByTagName(catNode, "id" ).getNodeValue() ) ; 
		
		//商品编号所在列位置
		int  goodsIdCluNum = Integer.valueOf( XMLUtil.getChildByTagName(catNode, "goodsid_column" ).getNodeValue()  ) ; 

		
		/**
		 * ======================
		 * 生成此类别的导入数据源
		 * =======================
		 */
		ImportDataSource importDs = new ImportDataSource();
		
		Cat cat =this.goodsCatManager.getById(catid);
		
		
		//获取此类别商品的类型
		GoodsTypeDTO typeDTO = this.goodsTypeManager.get(cat.getType_id());
		importDs.setBrandList(brandManager.list());
		importDs.setPropList(typeDTO.getPropList());
		
		
		//获取此类别的sheet 
		Sheet sheet = goodsWb.getSheetAt(sheetIndex);
		int lastRowNum =0;
		if(endNum!=null){
			lastRowNum = endNum;
		}else{
			lastRowNum = sheet.getLastRowNum();
		}
		
		
		//此类别节点的行配置列表
		NodeList rowList  = beforeSheetNode.getElementsByTagName("column");
		Element processorNode = XMLUtil.getChildByTagName(catNode, "processors"); //处理器
		NodeList importerNodeList =  processorNode.getElementsByTagName("importer");
		
 
		if(this.logger.isDebugEnabled()){
			logger.debug("开始导入类别["+cat.getName()+"]rowStartNum["+rowStartNum+"]lastRowNum["+lastRowNum+"]...");
		}
		
		
		//声明要导入的goods数据
		Map goods =null;
		
		for(int i=rowStartNum;i<lastRowNum+1;i++){
			
			Row row  = sheet.getRow(i);
			importDs.setRowData(row);
			/**
			 * ----------------
			 * 计算商品的编号
			 * ----------------
			 */
			int goodsNum=0;
			if(goodsIdCluNum==-1){
				goodsNum = row.getRowNum()+1;
			}else{
				goodsNum = Double.valueOf(row.getCell(goodsIdCluNum).getNumericCellValue()).intValue();
			}
			
			if(this.logger.isDebugEnabled()){
				logger.debug("开始行号["+goodsNum+"]...");
			}
			
			
			if(goodsNum!=0){//如果是一个新商品，生成一个新goods
				
				importDs.setDatafolder(  datafolder +"/"+ XMLUtil.getChildByTagName(catNode, "name" ).getNodeValue() +"/"+goodsNum);
				importDs.setNewGoods(true);
				importDs.setGoodsNum(goodsNum);
				goods = new HashMap();
				goods.put("market_enable", 1 );
				goods.put("cat_id", catid);
				goods.put("type_id", cat.getType_id());
				goods.put("have_spec", 0);
				goods.put("cost", 0  ); 
				goods.put("store", 0 );
				goods.put("weight",0 );
				goods.put("disabled", 0);
				goods.put("create_time", System.currentTimeMillis());
				goods.put("view_count",0);
				goods.put("buy_count",0);
				goods.put("last_modify",System.currentTimeMillis());
//				String sn = "G" + DateUtil.toString( new Date(System.currentTimeMillis()) ,"yyyyMMddhhmmss" )+StringUtil.getRandStr(4);
//				goods.put("sn",sn);
			
			}else{
				importDs.setNewGoods(false);
			}
			
			
			/**
			 * =================
			 * 处理列数据
			 * =================
			 */
			for(int j=0;j<rowList.getLength();j++){
				Element rowNode = (Element)rowList.item(j);
				String index = rowNode.getAttribute("index");
				String importer =  rowNode.getAttribute("importer");
			 
				Object value =  ExcelUtil.getCellValue( row.getCell(Integer.valueOf(index)) );
				IGoodsDataImporter goodsDataImporter = SpringContextHolder.getBean(importer);
				goodsDataImporter.imported(value, rowNode, importDs, goods);
				 
			}
			
			if(goodsNum!=0){//如果是新商品插入数据库
				this.daoSupport.insert("es_goods", goods);
				int goodsid = this.daoSupport.getLastId("es_goods");
				goods.put("goods_id", goodsid);
			}
			
			
			
			/**
			 * 执行后期处理
			 */
			Element afterSheetNode = XMLUtil.getChildByTagName(catNode, "aftersheet");
			
			if(afterSheetNode!=null){
				
			
				//此类别节点的行配置列表
				NodeList afterRowList  = afterSheetNode.getElementsByTagName("column");
				/**
				 * =================
				 * 处理后期列数据
				 * =================
				 */
				for(int j=0;j<afterRowList.getLength();j++){
					Element rowNode = (Element)afterRowList.item(j);
					String index = rowNode.getAttribute("index");
					String importer =  rowNode.getAttribute("importer");
				 
					Object value =  ExcelUtil.getCellValue( row.getCell(Integer.valueOf(index)) );
					IGoodsDataImporter goodsDataImporter = SpringContextHolder.getBean(importer);
					goodsDataImporter.imported(value, rowNode, importDs, goods);
					 
				}
			}
			
			/**
			 * =======================
			 * 执行各个导入器
			 * =======================
			 */
			for( int j=0;j<importerNodeList.getLength();j++){
				Element node =(Element)importerNodeList.item(j);
				String importer = node.getNodeValue();
				IGoodsDataImporter goodsDataImporter = SpringContextHolder.getBean(importer);
			 
				goodsDataImporter.imported(null, node, importDs, goods);
			}
			
			if(this.logger.isDebugEnabled()){
				logger.debug("行号["+goodsNum+"]导入完成");
			}
		}
		
		 
		if(this.logger.isDebugEnabled()){
			logger.debug("导入类别["+cat.getName()+"]完成...");
		}
		

	}
	
	/**
	 * 打开excel
	 * @param excelPath
	 * @return
	 */
	private Workbook openExcel(String excelPath){
 
        try {
        	POIFSFileSystem  fs = new POIFSFileSystem(new FileInputStream(excelPath)); 
        	Workbook wb = new HSSFWorkbook(fs);
        	return wb;
        } catch (IOException e) {
        	e.printStackTrace();
        	return null;
        }
		 
	}
	
	
	/**
	 * 加载导入配置xml文件
	 * @param path
	 * @return
	 */
	private Document load(String path){
		try {
		    DocumentBuilderFactory factory = 
		    DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document document = builder.parse(path);
		    return document;
		}
		catch (Exception e) {
		 
			e.printStackTrace();
			throw new RuntimeException("load ["+path+"]    error" );
		} 	 
	}

	
	

	

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}


	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

 

	public IGoodsTypeManager getGoodsTypeManager() {
		return goodsTypeManager;
	}


	public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
		this.goodsTypeManager = goodsTypeManager;
	}


	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}


	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}


	public IBrandManager getBrandManager() {
		return brandManager;
	}


	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}


	public GoodsImportPluginBundle getGoodsImportPluginBundle() {
		return goodsImportPluginBundle;
	}


	public void setGoodsImportPluginBundle(
			GoodsImportPluginBundle goodsImportPluginBundle) {
		this.goodsImportPluginBundle = goodsImportPluginBundle;
	}



}
