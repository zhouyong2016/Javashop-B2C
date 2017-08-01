package com.enation.app.shop.core.statistics.model;

public class Collect { 
	
	private String name;
	private double avgPrice; 
	/**
	 * 有销量商品数
	 */
	private int salesGoodsNum;
	/**
	 * 销量
	 */
	private int sales;
	/**
	 * 销售额
	 */
	private double saleroom;
	private int countGoods;
	/**
	 * 无销量商品数
	 */
	private int residue;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	} 
	public int getSales() {
		return sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}
	public double getSaleroom() {
		return saleroom;
	}
	public void setSaleroom(double saleroom) {
		this.saleroom = saleroom;
	} 
	public double getAvgPrice() {
		return avgPrice;
	}
	public void setAvgPrice(double avgPrice) {
		this.avgPrice = avgPrice;
	}
	public int getSalesGoodsNum() {
		return salesGoodsNum;
	}
	public void setSalesGoodsNum(int salesGoodsNum) {
		this.salesGoodsNum = salesGoodsNum;
	}
	public int getCountGoods() {
		return countGoods;
	}
	public void setCountGoods(int countGoods) {
		this.countGoods = countGoods;
	}
	public int getResidue() {
		return residue;
	}
	public void setResidue(int residue) {
		this.residue = residue;
	}
	 
	
}
