package com.bankofapis.recommendationengine.service;




import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bankofapis.core.model.accounts.OBReadAccountList;
import com.bankofapis.core.model.accounts.OBReadDataResponse;
import com.bankofapis.core.model.accounts.OBReadTransaction;
import com.bankofapis.core.model.accounts.OBReadTransactionList;
import com.bankofapis.core.model.token.TokenResponse;
import com.bankofapis.recommendationengine.config.APIServiceConfig;
import com.bankofapis.recommendationengine.model.RecommendationAction;
import com.bankofapis.recommendationengine.model.RecommendationResponse;



@Service
public class RecommendationService {
	private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
	
		
	private RestTemplate restTemplate;
	private APIServiceConfig apiServiceConfig;
	
	@Autowired
	public RecommendationService(RestTemplate restTemplate,APIServiceConfig apiServiceConfig)
	{		
		this.restTemplate = restTemplate;
		this.apiServiceConfig=apiServiceConfig;
	}

	
	public RecommendationResponse buildRecommendations(String accountId) {
		List<RecommendationAction> actions = new ArrayList<RecommendationAction>();
		List<OBReadTransaction> transactions = fetchTransactions(accountId);
		if(transactions==null)
			return null;
		Optional<OBReadTransaction> t = transactions.stream().filter(z-> Double.parseDouble(z.getAmount().getAmount()) > 1000 && z.getCreditDebitIndicator().equalsIgnoreCase("Debit")).findAny();
		if(t.isPresent())
		{
			RecommendationAction e = new RecommendationAction();
			e.setAction("Spent 1000 bucks !! Reduce spending on liquor by 10% !");
			actions.add(e);
		}
		Optional<OBReadTransaction> x = transactions.stream().filter(z-> Double.parseDouble(z.getAmount().getAmount()) < 100 && z.getCreditDebitIndicator().equalsIgnoreCase("Debit")).findAny();
		if(x.isPresent())
		{
			RecommendationAction e = new RecommendationAction();
			e.setAction("Consider switching to paying bills using credit card account !");
			actions.add(e);
		}
		Optional<OBReadTransaction> y = transactions.stream().filter(z-> Double.parseDouble(z.getBalance().getAmount().getAmount()) < 100).findAny();
		if(y.isPresent())
		{
			RecommendationAction e = new RecommendationAction();
			e.setAction("Balance below 100 bucks!! We are offering you a personal loan option at discount !!");
			actions.add(e);
		}
		Optional<OBReadTransaction> k = transactions.stream().filter(z-> Double.parseDouble(z.getBalance().getAmount().getAmount()) > 1000).findAny();
		if(k.isPresent())
		{
			RecommendationAction e = new RecommendationAction();
			e.setAction("Balance above 1000 bucks!! Consider an investment into SIP !!");
			actions.add(e);
		}

//		Map<String, DoubleSummaryStatistics> stats = transactions.stream().				
//				collect(Collectors.groupingBy(OBReadTransaction::getTransactionInformation,
//						Collectors.groupingBy(OBReadTransaction::getCreditDebitIndicator),
//						Collectors.summarizingDouble(OBReadTransaction::getAmount::getAmount)));
//		if(k.isPresent())
//		{
//			RecommendationAction e = new RecommendationAction();
//			e.setAction("Balance above 1000 bucks!! Consider an investment into SIP !!");
//			actions.add(e);
//		}

				
		
	
		RecommendationResponse response = new RecommendationResponse();
		response.setActions(actions);
		return response;
				
	}

	private List<OBReadTransaction> fetchTransactions(String accountId) {
		URI URL0=URI.create(apiServiceConfig.getURLString()+"/open-banking/v3/passToken/");
		URI URL1=URI.create(apiServiceConfig.getURLString()+"/open-banking/v3/aisp/accounts/");
		URI URL2=URI.create(apiServiceConfig.getURLString()+"/open-banking/v3/aisp/accounts/" + accountId + "/transactions");
		
		TokenResponse response0 = restTemplate.exchange(URL0, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<TokenResponse>() {}).getBody();
		
		if(response0==null)
			return null;
		HttpHeaders headers1 = new HttpHeaders();
		headers1.add("Authorization", response0.getTokenType()+ " " + response0.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>(headers1);
		
		OBReadDataResponse<OBReadAccountList> response1 = restTemplate.exchange(URL1, 
                HttpMethod.GET, 
                entity, 
                new ParameterizedTypeReference<OBReadDataResponse<OBReadAccountList>>() {}).getBody();
		
		System.out.println("resp = " + response1.toString());
		
		OBReadDataResponse<OBReadTransactionList> response2 = restTemplate.exchange(URL2, 
                HttpMethod.GET, 
                entity, 
                new ParameterizedTypeReference<OBReadDataResponse<OBReadTransactionList>>() {}).getBody();
		
			
			System.out.println("resp = " + response2.toString());
			if(response2!=null && response2.getData()!=null)
				return response2.getData().getTransactionList();
			return null;
		
	}

	

	
	
	
	
}
