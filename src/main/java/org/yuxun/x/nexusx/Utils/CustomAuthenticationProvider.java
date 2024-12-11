package org.yuxun.x.nexusx.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.yuxun.x.nexusx.Service.Impl.CustomUserDetailsService;


@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final AESUtil AesUtil;

    @Autowired
    public CustomAuthenticationProvider(CustomUserDetailsService customUserDetailsService, AESUtil AesUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.AesUtil = AesUtil;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String rawPassword = (String) authentication.getCredentials();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // 这里进行密码的校验
        if (AesUtil.matches(rawPassword, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, rawPassword, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
