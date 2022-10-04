package study.datajpa.entity;

/**
 * 클래스 기반 Projections
 */
public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
