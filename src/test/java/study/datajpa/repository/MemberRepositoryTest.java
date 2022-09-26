package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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
        Member findMember = memberRepository.findMemberByUsername("memberA");

        /**
         * Optional 단건 조회
         *    select
         *         member0_.member_id as member_i1_0_,
         *         member0_.age as age2_0_,
         *         member0_.team_id as team_id4_0_,
         *         member0_.username as username3_0_
         *     from
         *         member member0_
         *     where
         *         member0_.username=?
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


}