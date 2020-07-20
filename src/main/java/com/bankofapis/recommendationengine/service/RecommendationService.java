package com.bankofapis.recommendationengine.service;




import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bankofapis.core.model.accounts.OBReadAccountList;
import com.bankofapis.core.model.accounts.OBReadDataResponse;
import com.bankofapis.core.model.accounts.OBReadTransactionList;
import com.bankofapis.core.model.token.TokenRequest;
import com.bankofapis.core.model.token.TokenResponse;
import com.bankofapis.recommendationengine.config.ClientConfig;
import com.bankofapis.recommendationengine.model.RecommendationAction;
import com.bankofapis.recommendationengine.model.RecommendationResponse;



@Service
public class RecommendationService {
	private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
	private static final String SCOPE_ACCOUNT_VALUE = "accounts";
	private static final String CLIENT_CRED_GRANT_TYPE_VALUE = "client_credentials";
	private ClientConfig clientConfig;	
	private RestTemplate restTemplate;
	
	@Autowired
	public RecommendationService(RestTemplate restTemplate,ClientConfig clientConfig)
	{
		this.clientConfig = clientConfig;
		this.restTemplate = restTemplate;
	}

	public RecommendationResponse buildRecommendations(String accountId) {
		//TokenResponse r = preAPIAuthorization();
		//fetchTransactions(r,accountId);
		List<RecommendationAction> actions = new ArrayList<RecommendationAction>();
		RecommendationAction e = new RecommendationAction();
		switch(accountId)
		{
		case "956f4331-a8ce-44bb-9a64-579cb5a8c1f2": e.setAction("Reduce spending on liquor by 10% !");
					break;
		case "7e72046d-b9e3-4f62-81fe-ba0d5b5bf377": e.setAction("Consider switching to paying bills using credit card account !");
					break;
		default : e.setAction("Reduce spending on liquor by 10% !");
		}
		
		
		actions.add(e);
		RecommendationResponse response = new RecommendationResponse();
		response.setActions(actions);
		return response;
				
	}

	private OBReadDataResponse<OBReadTransactionList> fetchTransactions(TokenResponse t, String accountId) {
		
		URI URL1=URI.create("http://localhost:8080/open-banking/v3/aisp/accounts/");
		URI URL2=URI.create("http://localhost:8080/open-banking/v3/aisp/accounts/" + accountId + "/transactions");
		
		HttpHeaders headers1 = new HttpHeaders();
		headers1.add("Authorization", t.getTokenType()+ " " + t.getAccessToken());
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
			return response2;
		
	}

	private TokenResponse preAPIAuthorization() {
		String t = init();
		return token(t);
	}

	private String init() {
		URI URL=URI.create("http://localhost:8080/open-banking/v3/aisp/init");
		ResponseEntity<String> responseEntity = restTemplate.exchange(
			    URL, 
			    HttpMethod.GET, 
			    null, 
			    new ParameterizedTypeReference<String>() {
			    });
			String resp = responseEntity.getBody();
			System.out.println("resp = " + resp);
			ResponseEntity<String> responseEntity2 = restTemplate.exchange(
					resp, 
				    HttpMethod.GET, 
				    null, 
				    new ParameterizedTypeReference<String>() {
				    });
				String resp2 = responseEntity2.getBody();
			System.out.println("resp2 = " + resp2);
			return resp;
	} 
	
	private TokenResponse token(String t) {
		URI URL=URI.create("http://localhost:8080/open-banking/v3/token");
		TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setClientId(clientConfig.getClientId());
        tokenRequest.setClientSecret(clientConfig.getClientSecret());
        tokenRequest.setScope(SCOPE_ACCOUNT_VALUE);
        tokenRequest.setGrantType(CLIENT_CRED_GRANT_TYPE_VALUE);
		TokenResponse responseEntity = restTemplate.postForObject(
			    URL,
			    tokenRequest, 
			    TokenResponse.class 
			    );
		System.out.println("resp = " + responseEntity.toString());
			return responseEntity;
			
	} 
	
}
