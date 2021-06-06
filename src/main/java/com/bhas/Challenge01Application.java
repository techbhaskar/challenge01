package com.bhas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.bhas.entity.Branch;
import com.bhas.entity.BranchData;
import com.bhas.entity.BranchResponse;
import com.bhas.entity.ChallengeResponse;
import com.bhas.entity.GitResponse;
import com.bhas.entity.GitUser;
import com.bhas.entity.Root;
import com.bhas.entity.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;





@RestController
@SpringBootApplication
@Slf4j
public class Challenge01Application {
	
	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value= "/{userName}", produces={"application/json","application/xml"})
	/*
	 * NOT_FOUND = 404
	 * NOT_ACCEPTABLE = 406
	 * */
	ResponseEntity<ChallengeResponse>  home(@PathVariable String userName, @RequestHeader("Accept") String accept) {
		ChallengeResponse response = new ChallengeResponse();
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("Challenge01Application.home()"+accept);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			if(!"application/xml".equalsIgnoreCase(accept)) {
					UserResponse user = validUser(userName);
					if(user.getStatus() == 200) {
						
					ResponseEntity<ArrayList> result = restTemplate.getForEntity("https://api.github.com/users/"+userName+"/repos", ArrayList.class, "");
					if(result.hasBody()) {
						List<LinkedHashMap<String, String>> roots = result.getBody();
					    
						List<GitResponse> rts = new ArrayList<GitResponse>();
						for(int i=0; i<roots.size();i++) {
							Root root = mapper.convertValue(roots.get(i), Root.class);
							GitResponse gitResponse = new GitResponse();
							gitResponse.setRepoOwner(root.getOwner().getLogin());
							gitResponse.setBraches(getBranchData(userName, root.getName()));
							gitResponse.setRepoName(root.getName());
							rts.add(gitResponse);
						}
						response.setGitResponses(rts);
						response.setStatus(200);
						response.setMessage("Success!!");
						
					}
				}else {
					response.setGitResponses(new ArrayList<GitResponse>() );
					response.setStatus(user.getStatus());
					response.setMessage(user.getMessage());
				}
			}else {
				response.setGitResponses(new ArrayList<GitResponse>() );
				response.setStatus(406);
				response.setMessage("Not Acceptable!");
				return new ResponseEntity<ChallengeResponse>(response, headers,HttpStatus.NOT_ACCEPTABLE);
			}
		}catch(RestClientResponseException e) {
			response.setGitResponses(new ArrayList<GitResponse>());
			response.setStatus(e.getRawStatusCode());
			response.setMessage(e.getResponseBodyAsString());
			return new ResponseEntity<ChallengeResponse>(response, HttpStatus.valueOf(e.getRawStatusCode()));
		}catch(Exception e) {
			response.setGitResponses(new ArrayList<GitResponse>());
			response.setStatus(500);
			response.setMessage(e.getMessage());
			return new ResponseEntity<ChallengeResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<ChallengeResponse>(response, HttpStatus.OK);
	}
	
	private BranchResponse getBranchData(String userName, String branchName){
		//https://api.github.com/repos/techbhaskar/Maven-to-Tomcat/branches
		BranchResponse response = new BranchResponse();
		ObjectMapper mapper = new ObjectMapper();
		ResponseEntity<ArrayList> result = restTemplate.getForEntity("https://api.github.com/repos/"+userName+"/"+branchName+"/branches", ArrayList.class, "");
		if(result.hasBody()) {
			List<LinkedHashMap<String, String>> braches = result.getBody();
		    
			List<BranchData> branches = new ArrayList<BranchData>();
			for(int i=0; i<braches.size();i++) {
				Branch branch = mapper.convertValue(braches.get(i), Branch.class);
				BranchData branchData = new BranchData();
				branchData.setBranchName(branch.getName());
				branchData.setBranchLatestCommit(branch.getCommit().getSha());
				branches.add(branchData);
			}
			response.setBranches(branches);
		}
		return response;
	}
	
	private UserResponse validUser(String userName) {
		UserResponse  res = new UserResponse();
		try {
		ResponseEntity<GitUser> result = restTemplate.getForEntity("https://api.github.com/users/"+userName, GitUser.class, "");
		if(result.hasBody()) {
			res.setStatus(200);
			res.setMessage("Valid User!");
		}
		}catch(RestClientResponseException e) {
			res.setStatus(e.getRawStatusCode());
			res.setMessage(e.getResponseBodyAsString());
		}catch(Exception e) {
			res.setStatus(500);
			res.setMessage(e.getMessage());
		}
		return res; 
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Challenge01Application.class, args);
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectionRequestTimeout(3000);
		log.info("BCI API service created the rest template!!");
		return new RestTemplate(clientHttpRequestFactory);
	}
}
