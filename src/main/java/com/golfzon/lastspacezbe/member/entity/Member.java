package com.golfzon.lastspacezbe.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id //pk설정
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_member")
    @SequenceGenerator(sequenceName = "seq_member", allocationSize = 1, name="seq_member")
    @Column
    private long member_id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String member_name;

    @Column
    private String authority;

    @Column
    private String img_name;


//    @Column
//    private MultipartFile multipartFile;


}
