package net.developerpass.polimi.repository;

import net.developerpass.polimi.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	Optional<Reservation> findByIdAndDeletedFalse(Long id);

	@Query("SELECT COUNT(r) FROM Reservation r WHERE r.groupId = :groupId AND r.discussion.date > :now AND r.discussion.deleted = FALSE AND r.deleted = FALSE")
	Long countAllByGroupIdAndDateAndDeletedFalse(Long groupId, LocalDateTime now);

	List<Reservation> findAllByDiscussionIdAndDeletedFalse(Long discussionId);

	Page<Reservation> findAllByGroupIdAndDeletedFalse(Long groupId, Pageable pageable);

	Page<Reservation> findAllByDiscussion_ProfessorIdAndDeletedFalse(Long professorId, Pageable pageable);

}
