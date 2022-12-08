package com.golfzon.lastspacezbe.security.provider;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import com.golfzon.lastspacezbe.security.jwt.JwtDecoder;
import com.golfzon.lastspacezbe.security.jwt.JwtPreProcessingToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JWTAuthProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;

    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        String username = jwtDecoder.decodeUsername(token);

        // TODO: API 사용시마다 매번 User DB 조회 필요
        //  -> 해결을 위해서는 UserDetailsImpl 에 User 객체를 저장하지 않도록 수정
        //  ex) UserDetailsImpl 에 userId, username, role 만 저장
        //    -> JWT 에 userId, username, role 정보를 암호화/복호화하여 사용
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + username));
        Company company = companyRepository.findByMember(member);
        if(company == null){
            company = new Company(0L);
        }
        UserDetailsImpl userDetails = new UserDetailsImpl(member, company);
        System.out.println("JWTAuthProvider: "+userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }
}
