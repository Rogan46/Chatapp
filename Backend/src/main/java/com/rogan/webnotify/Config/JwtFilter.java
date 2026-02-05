package com.rogan.webnotify.Config;

import com.rogan.webnotify.Service.CustomUserDetailsService;
import com.rogan.webnotify.Service.JWTservice;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Configuration
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JWTservice jwTservice;
    @Autowired
    ApplicationContext context;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");
        String token=null;
        String username=null;


        if(authHeader!=null&&authHeader.startsWith("Bearer ")){
            token=authHeader.substring(7);
            username=jwTservice.extractUserName(token);
        }

        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);
            if(jwTservice.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken token1=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                token1.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token1);

            }
        }
        filterChain.doFilter(request,response);
    }
}
