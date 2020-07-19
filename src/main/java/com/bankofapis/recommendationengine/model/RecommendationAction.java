package com.bankofapis.recommendationengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;



public class RecommendationAction {


    @JsonProperty("Action")
    private String action = null;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}