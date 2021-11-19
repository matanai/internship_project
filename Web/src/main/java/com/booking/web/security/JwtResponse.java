package com.booking.web.security;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class JwtResponse
{
    private String token;
    private String type;
    private String username;
    private List<String> roles;
}
