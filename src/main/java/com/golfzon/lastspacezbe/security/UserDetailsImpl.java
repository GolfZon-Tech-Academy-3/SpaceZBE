package com.golfzon.lastspacezbe.security;


import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public class UserDetailsImpl implements UserDetails{

    private final Member member;

    private final Company company;

    public UserDetailsImpl(Member member, Company company) {
        this.member = member;
        this.company = company;
    }

    public Member getMember() {
        return member;
    }

    public Company getcompany() {
        return company;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "member=" + member +
                '}';
    }
}