package ch.fhnw.ip6.osfpp.persistence;

import ch.fhnw.ip6.osfpp.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface MemberRepository extends JpaRepository<Member, Long> {
}
