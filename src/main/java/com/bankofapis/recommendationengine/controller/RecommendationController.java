package com.bankofapis.recommendationengine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankofapis.recommendationengine.model.RecommendationResponse;
import com.bankofapis.recommendationengine.service.RecommendationService;

@RestController
@RequestMapping("/open-banking/recommendation")
public class RecommendationController {

    private static final String OB_JOURNEY_REC = "/getRecs";
	
	private RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping(value = OB_JOURNEY_REC)
    public RecommendationResponse recommendation( @RequestParam(value = "accountId") String accountId) {
        return recommendationService.buildRecommendations(accountId);
    }
    
   
}