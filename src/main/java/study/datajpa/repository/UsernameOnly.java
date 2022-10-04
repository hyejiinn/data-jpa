package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    // Open Projections - DB에서 엔티티 필드를 다 조회해온 다음에 계산
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
