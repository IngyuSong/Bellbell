package com.overcomingroom.bellbell.fortune.repository;

import com.overcomingroom.bellbell.fortune.domain.entity.Fortune;
import com.overcomingroom.bellbell.member.domain.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FortuneRepository extends JpaRepository<Fortune, Long> {
  Optional<Fortune> findByMember(Member member);
}
