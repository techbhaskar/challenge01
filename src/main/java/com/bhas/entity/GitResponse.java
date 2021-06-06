package com.bhas.entity;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class GitResponse {
	private String repoName;
	private String repoOwner;
	private BranchResponse braches;
}
