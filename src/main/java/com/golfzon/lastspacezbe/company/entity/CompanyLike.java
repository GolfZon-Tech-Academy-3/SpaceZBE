package com.golfzon.lastspacezbe.company.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(sequenceName = "seq_company_like", allocationSize = 1, name="seq_company_like")
public class CompanyLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "company_like_id")
    Long companyLikeId; // 좋아요 번호

    @Column(name = "member_id")
    Long memberId;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    public CompanyLike(Company company, Long memberId) {
        this.company = company;
        this.memberId = memberId;
    }
}
