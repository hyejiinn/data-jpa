package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;



@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
/**
 * NamedQuery 장점
 * :애플리케이션 로딩 시점에 먼저 파싱을 통해 쿼리를 한번 돌려보기 때문에
 * 애플리케이션 로딩 시점에 바로 에러를 찾을 수 있다는 장점!
 */
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;


    // jpa의 proxy기술등을 사용할 때 기본 생성자가 필요하고,
    // access level은 protected까지 열어둬야 한다.
//    protected Member() {
//    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }

    }

    //==연관관계 메서드==//
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
