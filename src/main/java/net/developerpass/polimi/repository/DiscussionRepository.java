package net.developerpass.polimi.repository;

import net.developerpass.polimi.entity.Discussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

	Optional<Discussion> findByIdAndDeletedFalse(Long id);

	Page<Discussion> findAllByProfessorIdAndDeletedFalse(Long professorId, Pageable pageable);

}
