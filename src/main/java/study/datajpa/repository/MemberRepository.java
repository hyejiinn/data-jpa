package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //@Query(name = "Member.findByUsername") 생략해도 됨..!
    // 1차: NamedQuery를 Member에서 우선 찾음 (메서드 이름을 가지고)
    // 2차: NamedQuery가 없을 시 그때 메서드 이름을 기준으로 쿼리를 생성함.
    List<Member> findByUsername(@Param("username") String username);

    // @Query 사용
    // 리포지토리 메서드에 쿼리 정의하기
    // 장점: 애플리케이션 로딩할 때 오류가 발생한다!! -> 엄청난 장점!!!
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

}
