package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

public class FmfDetails {

	/**
	 * Type of filter.
	 */
	private String filterType;
	
	/**
	 * Filter Identifier.
	 */
	private String filterId;
	
	/**
	 * Name of the filter
	 */
	private String name;
	
	/**
	 * Description of the filter.
	 */
	private String description;
	
	
	public String getFilterType() {
		return filterType;
	}

	public FmfDetails setFilterType(String filterType) {
		this.filterType = filterType;
		return this;
	}

	public String getFilterId() {
		return filterId;
	}

	public FmfDetails setFilterId(String filterId) {
		this.filterId = filterId;
		return this;
	}

	public String getName() {
		return name;
	}

	public FmfDetails setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public FmfDetails setDescription(String description) {
		this.description = description;
		return this;
	}
}
