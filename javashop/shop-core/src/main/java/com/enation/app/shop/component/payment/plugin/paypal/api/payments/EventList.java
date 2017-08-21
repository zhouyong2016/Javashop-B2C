package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.ArrayList;
import java.util.List;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class EventList  extends PayPalModel {

	/**
	 * A list of Webhooks event resources
	 */
	private List<Event> events;

	/**
	 * Number of items returned in each range of results. Note that the last results range could have fewer items than the requested number of items.
	 */
	private int count;

	/**
	 * 
	 */
	private List<Links> links;

	/**
	 * Default Constructor
	 */
	public EventList() {
		events = new ArrayList<Event>();
	}


	/**
	 * Setter for events
	 */
	public EventList setEvents(List<Event> events) {
		this.events = events;
		return this;
	}

	/**
	 * Getter for events
	 */
	public List<Event> getEvents() {
		return this.events;
	}


	/**
	 * Setter for count
	 */
	public EventList setCount(int count) {
		this.count = count;
		return this;
	}

	/**
	 * Getter for count
	 */
	public int getCount() {
		return this.count;
	}


	/**
	 * Setter for links
	 */
	public EventList setLinks(List<Links> links) {
		this.links = links;
		return this;
	}

	/**
	 * Getter for links
	 */
	public List<Links> getLinks() {
		return this.links;
	}


}

