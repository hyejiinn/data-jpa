package study.datajpa.repository;

// 네이티브 쿼리
public interface MemberProjection {

    Long getId();

    String getUsername();

    String getTeamName();
}
