package com.enation.app.shop.core.goods.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.framework.util.StringUtil;

/**
 * 商品属性
 * @author apexking
 *
 */
public class Attribute {
	
	
	private String name;
	private String options;
	/**属性类型 <br> @see com.enation.app.shop.core.service.AttributeType */
	private int type;
	private String value;
	private List valueList;
	private int[] nums=null;
	private int hidden; //过滤时是否要显示这个属性
	private int required; //是否必填
	private String datatype; //校验器
	private String unit; //单位
	
	public Attribute(){
		valueList = new ArrayList();
		hidden=0; 
	}
	
	public void addValue(String _value){
		valueList.add(_value);
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValStr(){
	 
		if(type<3){
			 
		}else if(type>=3 && type<=5){
			if(value!=null && !value.equals("") && !value.equals("null")){
				int index1 = StringUtil.toInt(value,false);
				if(getOptionAr().length>index1)
				 return  getOptionAr()[ StringUtil.toInt( value,false)  ];
			}else return null;
			
		}else if(type==6){//处理多项选择
			if(value!=null && !value.equals("") && !value.equals("null")){
				String[] ids = value.substring(1).split("#");
				String result = "";
				for(String id:ids){
					if(!StringUtil.isEmpty(result)) result += ",";
					result += getOptionAr()[StringUtil.toInt(id, false)];
				}
				return result;
			}else return null;
		}
		 
		return value;
	}
	
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	
	
	public String[] getOptionAr(){
		if(options==null || options.equals("")){
			return new String[]{};
		}
		
		String[] ar_options = options.split(",");
		
		return ar_options;
	}
	
	
	private Map[] maps ;
	/**
	 * 显示类别下的商品列表时过滤所用。
	 * @return
	 */
	public Map[] getOptionMap(){
		
		
		Map[] maps = new HashMap[]{};
		return maps;
	}
	
	
	
	
	public List getValueList() {
		return valueList;
	}
	public void setValueList(List valueList) {
		this.valueList = valueList;
	}
	
	
	public int[] getNums() {
		if(nums==null) nums= new int[this.getOptionAr().length];
		
		return nums;
	}

	public void setNums(int[] nums) {
		this.nums = nums;
	}
	
	

	public int getHidden() {
		return hidden;
	}

	public void setHidden(int hidden) {
		this.hidden = hidden;
	}
	
	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
		this.required = required;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public static void main(String[] args){
		
	 
		 
		
//		String json = "{\"name\":\"kingapex\",\"type:1,valueList:['诺',2,3]}";
//		JSONObject jsonObject = JSONObject.fromObject(json);
//		Attribute a = (Attribute)JSONObject.toBean(jsonObject, Attribute.class);
//		//System.out.println(a.name + " _- " + a.getType() + "--" + a.getValueList().get(0)); \
//		List values  = new ArrayList();
//		values.add("1111111111111");
//		values.add("22222222222");
//		values.add("33333333333");
//		Attribute attr  = new Attribute();
//		attr.setName("kingapex");
//		attr.setType(1);
//		attr.setValue("abc");
//		attr.setValueList(values);
		
//		JSONObject jsonObject = JSONObject.fromObject(attr);
//		//System.out.println(jsonObject);
		
		
//		String json = jsonObject.toString();
//		jsonObject = JSONObject.fromObject(json);
//		Attribute a = (Attribute)JSONObject.toBean(jsonObject, Attribute.class);
//		//System.out.println(a.name + " _- " + a.getType() + "--" + a.getValue() );
		
		
//		List<String> values  = new ArrayList<String>();
//		values.add("1111111111111");
//		values.add("22222222222");
//		values.add("33333333333");
//		Attribute attr  = new Attribute();
//		attr.setName("kingapex");
//		attr.setType(1);
//		attr.setValue("abc");
//		attr.setValueList(values);
//		
//		Attribute attr1  = new Attribute();
//		attr1.setName("kingapex");
//		attr1.setType(1);
//		attr1.setValue("abc");
//		attr1.setValueList(values);
//		
//		List jlist =  new ArrayList();
//		jlist.add(attr);
//		jlist.add(attr1);
//		
//		JSONArray jsonArray = JSONArray.fromObject( jlist );  
//		//System.out.println(jsonArray);
//		String temp = jsonArray.toString();
//		JSONArray j1 = JSONArray.fromObject(temp);
//		
//		List<Attribute> list =(List) JSONArray.toCollection(j1, Attribute.class);
//		//System.out.println(list.get(0).getName());
	}

 
	
	
}
