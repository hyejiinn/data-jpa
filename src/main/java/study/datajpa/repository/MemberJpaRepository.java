package study.datajpa.repository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    @PersistenceContext
    private final EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member); // member가 null일수도 있고 아닐수도 있다.
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult(); // 단건 반환
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery("select m from Member  m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    /**
     * NamedQuery 사용
     */
    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username).getResultList();
    }


    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age= :age order by m.username desc ")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    // 순수 JPA 페이징과 정렬
    // 보통 페이징할 때 totalCount도 포함되기 때문에 쿼리를 하나 더 만들어야 한다.
    // 근데 이때 totalcount 쿼리에는 정렬이 필요없기 때문에 성능 최적화를 위해서 정렬은 생략해줬다.
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age= :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }
}
