package kosta.gansikshop.domain;

import jakarta.persistence.*;
import kosta.gansikshop.domain.baseentity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    @Embedded
    private Address address;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Builder
    private Member(String name, String email, String password, String phone, Address address, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public void updateMember(String name, String password, String phone, Address address) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    /**
     * Member 등급은 이메일 도메인으로 판단
     * ex) 사내 이메일 도메인: @company.com
     * @company.com이 이메일 주소면 ADMIN, 그렇지 않으면 CUSTOMER
     * ex) test@test.com -> CUSTOMER
     * ex) test@company.com -> ADMIN
     */
    public static Member createMember(String name, String email, String password, String phone, Address address) {
        Role grade = email.contains("@company.com") ? Role.ADMIN : Role.CUSTOMER;
        return Member.builder().name(name).email(email).password(password).phone(phone).address(address).role(grade).build();
    }
}
