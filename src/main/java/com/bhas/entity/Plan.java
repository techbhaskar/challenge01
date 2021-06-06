package com.bhas.entity;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Plan{
    public String name;
    public int space;
    public int collaborators;
    public int private_repos;
}
