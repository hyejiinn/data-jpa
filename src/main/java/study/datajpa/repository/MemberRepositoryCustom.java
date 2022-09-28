package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

// 사용자 정의 리포지토리 구현
// QueryDsl를 주로 사용할 때 custom 해서 많이 사용한다.
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
