package com.golfzon.lastspacezbe.security;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public UserDetailsServiceImpl(MemberRepository memberRepository, CompanyRepository companyRepository) {
        this.memberRepository = memberRepository;
        this.companyRepository = companyRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + username));
        Company company = companyRepository.findByMember(member);
        if(company == null){
            company = new Company(0L);
        }
        return new UserDetailsImpl(member, company);
    }
}
