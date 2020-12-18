package net.developerpass.polimi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.developerpass.polimi.entity.Account;
import net.developerpass.polimi.entity.Discussion;
import net.developerpass.polimi.entity.File;
import net.developerpass.polimi.entity.Group;
import net.developerpass.polimi.entity.Professor;
import net.developerpass.polimi.entity.Reservation;
import net.developerpass.polimi.entity.Student;
import net.developerpass.polimi.repository.AccountRepository;
import net.developerpass.polimi.repository.DiscussionRepository;
import net.developerpass.polimi.repository.FileRepository;
import net.developerpass.polimi.repository.GroupRepository;
import net.developerpass.polimi.repository.ProfessorRepository;
import net.developerpass.polimi.repository.ReservationRepository;
import net.developerpass.polimi.repository.StudentRepository;
import net.developerpass.polimi.service.jwt.JWTService;
import net.developerpass.polimi.utils.object.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProfessorService {
	private final FileRepository fileRepository;
	private final GroupRepository groupRepository;
	private final StudentRepository studentRepository;
	private final AccountRepository accountRepository;
	private final ProfessorRepository professorRepository;
	private final DiscussionRepository discussionRepository;
	private final ReservationRepository reservationRepository;

	private Professor getProfessor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			User user = ((User) authentication.getPrincipal());

			if (!user.getAuthorities().contains(new SimpleGrantedAuthority(Role.Professor.name()))) {
				throw new JWTService.TokenVerificationException();
			}

			Account account = accountRepository.findByRoleAndUsernameAndPassword(Role.Professor, user.getUsername(), user.getPassword())
					.orElseThrow(JWTService.TokenVerificationException::new);
			return professorRepository.findByAccountId(account.getId())
					.orElseThrow(JWTService.TokenVerificationException::new);
		}
		throw new JWTService.TokenVerificationException();
	}

	public Professor getProfile() {
		return getProfessor();
	}

	public Page<Professor> getProfessors(Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		return professorRepository.findAll(PageRequest.of(page, pageSize));
	}

	public Professor getProfessor(Long professorId) {
		return professorRepository.findById(professorId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid professorId"));
	}

	public Page<Group> getGroups(Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		Professor professor = getProfessor();
		return groupRepository.findAllByProfessorIdAndDeletedFalse(professor.getId(), PageRequest.of(page, pageSize));
	}

	public Group getGroup(Long groupId) {
		return groupRepository.findById(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));
	}

	public Page<Student> getStudents(Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		return studentRepository.findAll(PageRequest.of(page, pageSize));
	}

	public Student getStudent(Long studentId) {
		return studentRepository.findById(studentId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid studentId"));
	}

	public Page<File> getFiles(Long groupId, Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		return fileRepository.findAllByGroupIdAndDeletedFalseOrderByCreatedOnDesc(groupId, PageRequest.of(page, pageSize));
	}

	public File getFile(Long fileId) {
		return fileRepository.findById(fileId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid fileId"));
	}

	public Page<Discussion> getDiscussions(Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		Professor professor = getProfessor();
		return discussionRepository.findAllByProfessorIdAndDeletedFalse(professor.getId(), PageRequest.of(page, pageSize));
	}

	public Discussion getDiscussion(Long discussionId) {
		return discussionRepository.findById(discussionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid discussionId"));
	}

	public Discussion createDiscussion(String name, LocalDateTime date) {
		Professor professor = getProfessor();

		if (LocalDateTime.now().isAfter(date)) {
			throw new IllegalArgumentException("Unable to create a discussion with an earlier date than the current one");
		}

		return discussionRepository.save(new Discussion(professor.getId(), name, date));
	}

	public Discussion updateDiscussion(Long discussionId, String name, LocalDateTime date) {
		Professor professor = getProfessor();

		Discussion discussion = discussionRepository.findByIdAndDeletedFalse(discussionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid discussionId"));

		if (!discussion.getProfessorId().equals(professor.getId())) {
			throw new IllegalArgumentException("You are not the professor of this discussion");
		}

		if (LocalDateTime.now().isAfter(discussion.getDate())) {
			throw new IllegalArgumentException("Unable to update a past discussion");
		}

		if (LocalDateTime.now().isAfter(date)) {
			throw new IllegalArgumentException("Unable to update the date with an earlier than the current one");
		}

		discussion.setName(name);
		discussion.setDate(date);
		return discussionRepository.save(discussion);
	}

	public void deleteDiscussion(Long discussionId) {
		Professor professor = getProfessor();

		Discussion discussion = discussionRepository.findByIdAndDeletedFalse(discussionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservationId"));

		if (!discussion.getProfessorId().equals(professor.getId())) {
			throw new IllegalArgumentException("You are not the professor of this discussion");
		}

		if (LocalDateTime.now().isAfter(discussion.getDate())) {
			throw new IllegalArgumentException("Unable to delete a past discussion");
		}

		discussion.setDeleted(true);
		discussionRepository.save(discussion);
		List<Reservation> reservations = reservationRepository.findAllByDiscussionIdAndDeletedFalse(discussionId);
		reservations.forEach(reservation -> reservation.setDeleted(true));
		reservationRepository.saveAll(reservations);
	}

	public Page<Reservation> getReservations(Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		Professor professor = getProfessor();
		return reservationRepository.findAllByDiscussion_ProfessorIdAndDeletedFalse(professor.getId(), PageRequest.of(page, pageSize));
	}

	public Reservation getReservation(Long reservationId) {
		return reservationRepository.findById(reservationId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservationId"));
	}

	public void deleteReservation(Long reservationId) {
		Professor professor = getProfessor();

		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservationId"));

		Discussion discussion = discussionRepository.findById(reservation.getDiscussionId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservationId"));

		if (!discussion.getProfessorId().equals(professor.getId())) {
			throw new IllegalArgumentException("You are not the professor of this discussion");
		}

		reservation.setDeleted(true);
		reservationRepository.save(reservation);
	}
}
