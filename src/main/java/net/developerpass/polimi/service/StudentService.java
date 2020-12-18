package net.developerpass.polimi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.developerpass.polimi.entity.Account;
import net.developerpass.polimi.entity.Discussion;
import net.developerpass.polimi.entity.FellowStudent;
import net.developerpass.polimi.entity.File;
import net.developerpass.polimi.entity.Group;
import net.developerpass.polimi.entity.Professor;
import net.developerpass.polimi.entity.Reservation;
import net.developerpass.polimi.entity.Student;
import net.developerpass.polimi.repository.AccountRepository;
import net.developerpass.polimi.repository.DiscussionRepository;
import net.developerpass.polimi.repository.FellowStudentRepository;
import net.developerpass.polimi.repository.FileRepository;
import net.developerpass.polimi.repository.GroupRepository;
import net.developerpass.polimi.repository.ProfessorRepository;
import net.developerpass.polimi.repository.ReservationRepository;
import net.developerpass.polimi.repository.StudentRepository;
import net.developerpass.polimi.service.jwt.JWTService;
import net.developerpass.polimi.utils.object.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class StudentService {
	private final FileRepository fileRepository;
	private final GroupRepository groupRepository;
	private final AccountRepository accountRepository;
	private final StudentRepository studentRepository;
	private final ProfessorRepository professorRepository;
	private final DiscussionRepository discussionRepository;
	private final ReservationRepository reservationRepository;
	private final FellowStudentRepository fellowStudentRepository;

	@Value("${file.upload-dir}")
	private String uploadDir;

	private Student getStudent() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			User user = ((User) authentication.getPrincipal());

			if (!user.getAuthorities().contains(new SimpleGrantedAuthority(Role.Student.name()))) {
				throw new JWTService.TokenVerificationException();
			}

			Account account = accountRepository.findByRoleAndUsername(Role.Student, user.getUsername())
					.orElseThrow(JWTService.TokenVerificationException::new);
			return studentRepository.findByAccountId(account.getId())
					.orElseThrow(JWTService.TokenVerificationException::new);
		}
		throw new JWTService.TokenVerificationException();
	}

	public Student getProfile() {
		return getStudent();
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

	public Page<Group> getGroups(Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		Student student = getStudent();
		return groupRepository.findAllByStudentIdAndDeletedFalse(student.getId(), PageRequest.of(page, pageSize));
	}

	public Group getGroup(Long groupId) {
		return groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));
	}

	public Group createGroup(Long professorId, String name) {
		Student student = getStudent();

		professorRepository.findById(professorId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid professorId"));

		Long studentGroupsForProfessor = groupRepository.countAllByStudentIdAndProfessorIdAndDeletedFalse(student.getId(), professorId);
		if (studentGroupsForProfessor > 0) {
			throw new IllegalArgumentException("Already in a group width this professorId");
		}

		return groupRepository.save(new Group(professorId, student.getId(), name));
	}

	public Group updateGroup(Long groupId, String name) {
		Student student = getStudent();

		Group group = groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));

		if (!group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("You are not the admin of this group");
		}

		group.setName(name);
		return groupRepository.save(group);
	}

	public void deleteGroup(Long groupId) {
		Student student = getStudent();

		Group group = groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));

		if (!group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("You are not the admin of this group");
		}

		group.setDeleted(true);
		groupRepository.save(group);
	}

	public Group joinGroup(Long groupId) {
		Student student = getStudent();

		Group group = groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));

		List<FellowStudent> fellowStudentList = fellowStudentRepository.findAllByGroupIdAndDeletedFalse(groupId);

		if (group.getAdminId().equals(student.getId())
				|| fellowStudentList.stream().anyMatch(fStudent -> fStudent.getStudentId().equals(student.getId())
		)) {
			throw new IllegalArgumentException("You are already in this group");
		}

		if (fellowStudentList.size() >= 2) {
			throw new IllegalArgumentException("The group is full");
		}


		fellowStudentRepository.save(new FellowStudent(student.getId(), groupId));
		return groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));
	}

	public void leaveGroup(Long groupId) {
		Student student = getStudent();

		Group group = groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));

		if (group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("Unable to leave, but you can delete this group");
		}

		FellowStudent fellowStudent = fellowStudentRepository.findByStudentIdAndGroupIdAndDeletedFalse(student.getId(), group.getId())
				.orElseThrow(() -> new IllegalArgumentException("You are not in this group"));

		fellowStudent.setDeleted(true);
		fellowStudentRepository.save(fellowStudent);
	}

	public void removeStudentFromGroup(Long groupId, Long studentId) {
		Student student = getStudent();

		Group group = groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));

		if (!group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("You are not the admin of this group");
		}

		FellowStudent fellowStudent = fellowStudentRepository.findByStudentIdAndGroupIdAndDeletedFalse(studentId, groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid studentId"));

		fellowStudent.setDeleted(true);
		fellowStudentRepository.save(fellowStudent);
	}

	public Page<File> getFiles(Long groupId, Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		return fileRepository.findAllByGroupIdAndDeletedFalseOrderByCreatedOnDesc(groupId, PageRequest.of(page, pageSize));
	}

	public File getFile(Long fileId) {
		return fileRepository.findByIdAndDeletedFalse(fileId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid fileId"));
	}

	public File putFile(Long groupId, MultipartFile doc) {
		Student student = getStudent();

		Group group = groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid groupId"));

		if (!group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("You are not the admin of this group");
		}

		if (doc.getOriginalFilename() == null) {
			throw new IllegalArgumentException("Invalid file name");
		}

		try {
			String name = StringUtils.cleanPath(doc.getOriginalFilename());

			int dotIndex = name.lastIndexOf(46);
			String fileExtension = dotIndex == -1 ? "" : name.substring(dotIndex + 1);

			String fileName = UUID.randomUUID().toString() + "." + fileExtension;
			Path targetLocation;
			do {
				targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
			} while (Files.exists(targetLocation));

			System.out.println(targetLocation);
			Files.copy(doc.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileRepository.save(new File(groupId, name, fileName));
		} catch (IOException ex) {
			throw new IllegalArgumentException("Unable to upload file");
		}
	}

	public void deleteFile(Long fileId) {
		Student student = getStudent();

		File file = fileRepository.findByIdAndDeletedFalse(fileId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid fileId"));

		Group group = groupRepository.findByIdAndDeletedFalse(file.getGroupId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid file groupId"));

		if (!group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("You are not the admin of this group");
		}

		file.setDeleted(true);
		fileRepository.save(file);
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

	public Page<Discussion> getDiscussions(Long professorId, Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		return discussionRepository.findAllByProfessorIdAndDeletedFalse(professorId, PageRequest.of(page, pageSize));
	}

	public Discussion getDiscussion(Long discussionId) {
		return discussionRepository.findByIdAndDeletedFalse(discussionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid discussionId"));
	}

	public Page<Reservation> getReservations(Long groupId, Integer page, Integer pageSize) {
		if (page == null || page < 0) page = 0;
		if (pageSize == null || pageSize < 1) pageSize = 1;
		else if (pageSize > 50) pageSize = 50;
		return reservationRepository.findAllByGroupIdAndDeletedFalse(groupId, PageRequest.of(page, pageSize));
	}

	public Reservation getReservation(Long reservationId) {
		return reservationRepository.findByIdAndDeletedFalse(reservationId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservationId"));
	}

	public Reservation createReservation(Long groupId, Long discussionId) {
		Student student = getStudent();

		Group group = groupRepository.findByIdAndDeletedFalse(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservationId"));

		if (!group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("You are not the admin of this group");
		}

		Discussion discussion = discussionRepository.findByIdAndDeletedFalse(discussionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid discussionId"));

		if (LocalDateTime.now().isAfter(discussion.getDate())) {
			throw new IllegalArgumentException("Unable to create a reservation for a past discussion");
		}

		if (reservationRepository.countAllByGroupIdAndDateAndDeletedFalse(groupId, LocalDateTime.now()) > 0) {
			throw new IllegalArgumentException("Already exist a reservation");
		}

		return reservationRepository.save(new Reservation(groupId, discussionId));
	}

	public Reservation updateReservation(Long reservationId, Long discussionId) {
		Student student = getStudent();

		Reservation reservation = reservationRepository.findByIdAndDeletedFalse(reservationId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservationId"));

		Group group = groupRepository.findByIdAndDeletedFalse(reservation.getGroupId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservation groupId"));

		if (!group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("You are not the admin of this group");
		}

		Discussion discussion = discussionRepository.findByIdAndDeletedFalse(reservation.getDiscussionId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservation discussionId"));

		if (LocalDateTime.now().isAfter(discussion.getDate())) {
			throw new IllegalArgumentException("Unable to update to a past discussion");
		}

		reservation.setDiscussionId(discussionId);
		return reservationRepository.save(reservation);
	}

	public void deleteReservation(Long reservationId) {
		Student student = getStudent();

		Reservation reservation = reservationRepository.findByIdAndDeletedFalse(reservationId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservationId"));

		Group group = groupRepository.findByIdAndDeletedFalse(reservation.getGroupId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservation groupId"));

		if (!group.getAdminId().equals(student.getId())) {
			throw new IllegalArgumentException("You are not the admin of this group");
		}

		Discussion discussion = discussionRepository.findByIdAndDeletedFalse(reservation.getDiscussionId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid reservation discussionId"));

		if (LocalDateTime.now().isAfter(discussion.getDate())) {
			throw new IllegalArgumentException("Unable to delete a past reservation");
		}

		reservation.setDeleted(true);
		reservationRepository.save(reservation);
	}
}
