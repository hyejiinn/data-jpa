package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.entity.UsernameOnlyDto;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback(value = false)
@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get(); // 원래 get()을 쓰면 안됨..!!

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @DisplayName("단건 조회 검증")
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void namedQuery() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

//        List<Member> result = memberRepository.findByUsername("memberA");
        List<Member> result = memberRepository.findByUsername("memberA");

        assertThat(result.get(0)).isEqualTo(memberA);
    }


    @Test
    public void testQuery() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findUser("memberA", 10);
        assertThat(result.get(0)).isEqualTo(memberA);
    }

    @Test
    public void findUsernameList() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member memberA = new Member("memberA", 10);
        memberA.setTeam(team);
        memberRepository.save(memberA);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findByNames(Arrays.asList("memberA", "memberB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        /** 컬렉션 조회
         *     select
         *         member0_.member_id as member_i1_0_,
         *         member0_.age as age2_0_,
         *         member0_.team_id as team_id4_0_,
         *         member0_.username as username3_0_ 
         *     from
         *         member member0_ 
         *     where
         *         member0_.username=?
         */
        List<Member> memberList = memberRepository.findListByUsername("memberA");

        /** 단건 조회
         */
        Member findMember = memberRepository.findMemberByUsername("memberA");

        /**
         * Optional 단건 조회
         */
        Member findMemberA = memberRepository.findOptionalByUsername("memberA").get();


    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // page<Member> 자체를 반환하는건 좋지 않기 때문에
        // MemberDto로 변환해서 반환하는게 좋다!! (엔티티 직접 노출 x)
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(6); // Slice에서는 제공 x
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);  // Slice에서는 제공 x
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    // 벌크성 수정 쿼리
    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));
        memberRepository.save(new Member("member6", 15));

        // when
        // 벌크 연산을 수행한 다음에는 영속성 컨텍스트를 flush 해줘야 한다...!!
        // 안그러면 만약 같은 트랜잭션안에서 다른 로직이 일어나면 데이터 변경한게 반영이 안되어 있기 때문이다.
        int resultCount = memberRepository.bulkAgePlus(20);
//        entityManager.flush(); 근데 스프링 데이터 jpa에서는 @Modifying 어노테이션에서 옵션을 true로 설정해주면 따로 flush 안해도 된다 ㅎㅎ
//        entityManager.clear();


        // then
        assertThat(resultCount).isEqualTo(3);
    }

    // N + 1 문제
    //
    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin(); // 페치 조인 사용
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            // Member와 Team은 지연로딩 관계로 이루어져있기 때문에 Member가져올때 Team은 가짜객체 즉 프록시를 의미한다.
            // 그리고 이제 실제 Team을 사용하는 순간인 getTeam().getName()을 하는 순간이 진짜 사용하는 순간이기 때문에
            // 이때 영속성 컨텍스트에서 조회를 해오게 된다.
            // => N + 1 문제 발생 ..!
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName()); // 프록시 초기화
        }
    }

    // JPA Hint & Lock
    @Test
    public void queryHint() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        entityManager.flush();
        entityManager.clear();

        // when
//        Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); // readOnly 인 경우에는 변경감지 체크를 하지 않기 때문에 update sql 전송하지 않음

        entityManager.flush();
    }

    @Test
    public void lock() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        entityManager.flush();
        entityManager.clear();

        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    /**
     * Specifications
     * AND, OR 같은 연산자로 조합해서 다양한 검색 조건을 쉽게 생성 가능
     * 장점: 자바 코드로 작성할 수 있음
     * 단점: Jpa Criteria 가 너무 복잡함..!!!! (직관적이지 않음)
     * 사용하지 않는 것을 권장... 사용했다간 어마어마하게 후회할 예정이라고 함ㅋㅋㅋㅋ
     * -> QueryDSL을 사용하자
     */
    @Test
    public void specBasic() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

        // when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    /**
     * Query By Example
     * 장점: 동적 쿼리를 편리하게 처리할 수 있음, 도메인 객체를 그대로 사용
     * 단점: join이 inner join은 되는데 outer join이 되지 않는다.
     * -> QueryDSL을 사용하자
     */
    @Test
    public void queryByExample() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();


        // when
        // Probe : 필드에 데이터가 있는 실제 도메인 객체
        // memberRepository.findByUsername("m1"); 이건 정적일 때만 가능 !
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team); // 연관관계를 이렇게 세팅해주고 넘겨주면 알아서 inner join 까지는 해줌

        // ExampleMatcher : 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        // Example : Probe와 ExampleMatcher로 구성, 쿼리를 생성하는데 사용
        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");


    }

    /**
     * Projections
     * : 엔티티 대신에 DTO를 편리하게 조회할 때 사용
     */
    @Test
    public void projections() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

        // Closed Projections -> 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 jpa 가 제공
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");


        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }

        // 클래스 기반 Projections - 생성자의 파라미터 이름으로 매칭
        List<UsernameOnlyDto> result2 = memberRepository.findProjectionsDtoByUsername("m1");

        for (UsernameOnlyDto usernameOnlyDto : result2) {
            System.out.println("usernameOnlyDto.getUsername() = " + usernameOnlyDto.getUsername());
        }

        // 동적 Projections - Generic type을 주어 동적으로 프로젝션 데이터 변경 가능
        List<UsernameOnlyDto> result3 = memberRepository.findProjectionsDtoByUsername("m1", UsernameOnlyDto.class);

        for (UsernameOnlyDto usernameOnlyDto : result3) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
        }

        // 중첩 구조 처리
        List<NestedClosedProjections> result4 = memberRepository.findProjectionsDtoByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result4) {
            System.out.println("nestedClosedProjections = " + nestedClosedProjections.getUsername());
        }


    }

    /**
     * 네이티브 쿼리
     * -> 가급적 네이티브 쿼리는 사용하지 않는 것이 좋고,
     * 스프링 JdbcTemplate나 myBatis, jooq 같은 외부 라이브러리를 사용하는게 좋다.
     */
    @Test
    public void nativeQuery() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

        // when
        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("result = " + result);

        Page<MemberProjection> result2 = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result2.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
    }

}