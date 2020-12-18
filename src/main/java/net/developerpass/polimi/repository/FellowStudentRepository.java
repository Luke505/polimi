package net.developerpass.polimi.repository;

import net.developerpass.polimi.entity.FellowStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FellowStudentRepository extends JpaRepository<FellowStudent, Long> {

	List<FellowStudent> findAllByGroupIdAndDeletedFalse(Long groupId);

	Optional<FellowStudent> findByStudentIdAndGroupIdAndDeletedFalse(Long studentId, Long groupId);

}
