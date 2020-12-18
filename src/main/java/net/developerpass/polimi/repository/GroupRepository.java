package net.developerpass.polimi.repository;

import net.developerpass.polimi.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

	Optional<Group> findByIdAndDeletedFalse(Long id);

	@Query("SELECT g FROM Group g WHERE g.deleted = FALSE AND (g.adminId = :studentId OR ( SELECT COUNT(fs.id) FROM FellowStudent fs WHERE fs.studentId = :studentId AND fs.deleted = FALSE ) = 1)")
	Page<Group> findAllByStudentIdAndDeletedFalse(Long studentId, Pageable pageable);

	Page<Group> findAllByProfessorIdAndDeletedFalse(Long professorId, Pageable pageable);

	@Query("SELECT COUNT(g) FROM Group g WHERE g.deleted = FALSE AND g.professorId = :professorId AND (g.adminId = :studentId OR ( SELECT COUNT(fs.id) FROM FellowStudent fs WHERE fs.studentId = :studentId AND fs.deleted = FALSE ) = 1)")
	Long countAllByStudentIdAndProfessorIdAndDeletedFalse(Long studentId, Long professorId);

}
