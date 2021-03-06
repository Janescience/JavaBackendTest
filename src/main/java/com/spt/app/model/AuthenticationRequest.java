package com.spt.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class AuthenticationRequest {

    private String username;
    private String password;
}