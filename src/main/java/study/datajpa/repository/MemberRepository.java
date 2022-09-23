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

    /**
     * NamedQuery 장점
     * :애플리케이션 로딩 시점에 먼저 파싱을 통해 쿼리를 한번 돌려보기 때문에
     * 애플리케이션 로딩 시점에 바로 에러를 찾을 수 있다는 장점!
     */
}
