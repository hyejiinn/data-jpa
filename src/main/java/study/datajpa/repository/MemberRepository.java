package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// @Repository 어노테이션 생략해도 됨 -> 그리고 알아서 Jpa 관련 예외를 Spring 예외로 변환해줌
public interface MemberRepository extends JpaRepository<Member, Long> , MemberRepositoryCustom, JpaSpecificationExecutor{

    // 메소드 이름으로 쿼리 생성
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

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // @Query 값 DTO 조회하기
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 반환 타입
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    @Query(value = "select m from Member m left join m.team t",
        countQuery = "select count(m) from Member m"
    )
    Page<Member> findByAge(int age, Pageable pageable); // 스프링 데이터 jpa 페이징과 정렬

    // 이 어노테이션을 꼭 넣어야 데이터 변경이 된다!! (안쓰면 에러남..ㅎㅎ)
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // N + 1 문제 해결 : 페치 조인
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // N + 1 문제 해결 : @EntityGraph
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //  N + 1 문제 해결 : jpql + @EntityGraph 이 방법도 가능하다.
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 회원 데이터를 쓸 때 Team 객체도 쓸일이 많다 하면 이런식으로 가져올 수 있다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value= @QueryHint( name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
