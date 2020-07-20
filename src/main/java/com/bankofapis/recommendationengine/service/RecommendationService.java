package com.bankofapis.recommendationengine.service;




import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bankofapis.core.model.accounts.OBReadAccountList;
import com.bankofapis.core.model.accounts.OBReadDataResponse;
import com.bankofapis.core.model.accounts.OBReadTransaction;
import com.bankofapis.core.model.accounts.OBReadTransactionList;
import com.bankofapis.core.model.token.TokenRequest;
import com.bankofapis.core.model.token.TokenResponse;
import com.bankofapis.recommendationengine.config.ClientConfig;
import com.bankofapis.recommendationengine.model.RecommendationAction;
import com.bankofapis.recommendationengine.model.RecommendationResponse;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;



@Service
public class RecommendationService {
	private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
	private static final String SCOPE_ACCOUNT_VALUE = "accounts";
	private static final String CLIENT_CRED_GRANT_TYPE_VALUE = "client_credentials";
	private static final Path path = Paths.get("c:\\\\hackathon\\\\token.txt");
	private ClientConfig clientConfig;	
	private RestTemplate restTemplate;
	
	@Autowired
	public RecommendationService(RestTemplate restTemplate,ClientConfig clientConfig)
	{
		this.clientConfig = clientConfig;
		this.restTemplate = restTemplate;
	}

	
	public RecommendationResponse buildRecommendations(String accountId) {
		List<RecommendationAction> actions = new ArrayList<RecommendationAction>();
		List<OBReadTransaction> transactions = fetchTransactions(accountId);
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

		
	
		RecommendationResponse response = new RecommendationResponse();
		response.setActions(actions);
		return response;
				
	}

	private List<OBReadTransaction> fetchTransactions(String accountId) {
		URI URL0=URI.create("http://localhost:8080/open-banking/v3/passToken/");
		URI URL1=URI.create("http://localhost:8080/open-banking/v3/aisp/accounts/");
		URI URL2=URI.create("http://localhost:8080/open-banking/v3/aisp/accounts/" + accountId + "/transactions");
		
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
