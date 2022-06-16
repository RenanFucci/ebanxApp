package com.rrf.ebanxapi.ebanxapp.model.form;

public class EventForm {

	private String type;
	private String destination;
	private String origin;
	private int amount;

	public String getType() {
		return type;
	}

	public String getDestination() {
		return destination;
	}

	public String getOrigin() {
		return origin;
	}
	
	public String getOriginName() {
		return "origin";
	}

	public String getDestinationName() {
		return "destination";		
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "{ type: "+type+", destination:"+destination+", amount:"+amount+", origin:"+origin
				+ "}";
	}
	


}
