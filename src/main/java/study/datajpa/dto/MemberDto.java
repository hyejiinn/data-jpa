package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

// 엔티티에는 @Data를 보통 사용하면 안되지만, dto니까 우선 쓴다..!
// 쓰면 안되는 이유는 우선 @Getter, @Setter가 다 들어있기 때문이다.
@Data
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
