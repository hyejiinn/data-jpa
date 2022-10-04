package study.datajpa.repository;

/**
 * Projections 중첩 구조 처리
 * 프로젝션 대상이 root 엔티티이면 유용
 * 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화 지원하지 않음!!
 * 실무의 복잡한 쿼리를 해결하기에는 한계가 있다 ㅠㅠ
 * -> QueryDSL을 사용하자
 */
public interface NestedClosedProjections {

    String getUsername();

    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

}
