package com.overcomingroom.bellbell.member.repository;

import com.overcomingroom.bellbell.member.domain.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmail(String email);
  Optional<Member> findByNickname(String nickname);
}
