package com.bhas.entity;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Branch{
    public String name;
    public Commit commit;
    @JsonProperty("protected")
    public boolean protectedFlag;
}
