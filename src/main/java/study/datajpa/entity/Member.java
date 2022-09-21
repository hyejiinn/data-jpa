package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;

    // jpa의 proxy기술등을 사용할 때 기본 생성자가 필요하고,
    // access level은 protected까지 열어둬야 한다.
    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }
}
