package com.bankofapis.recommendationengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecommendationResponse {


    @JsonProperty("Actions")
    private List<RecommendationAction> actions = null;

	public List<RecommendationAction> getActions() {
		return actions;
	}

	public void setActions(List<RecommendationAction> actions) {
		this.actions = actions;
	}

}