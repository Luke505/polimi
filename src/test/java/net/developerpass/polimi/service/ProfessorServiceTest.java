package net.developerpass.polimi.service;

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
import net.developerpass.polimi.utils.object.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@Transactional
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ComponentScan({"net.developerpass.polimi.service", "net.developerpass.polimi.configuration.mail"})
public class ProfessorServiceTest {

	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private ProfessorService professorService;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private ProfessorRepository professorRepository;
	@Autowired
	private DiscussionRepository discussionRepository;
	@Autowired
	private ReservationRepository reservationRepository;

	private Professor professor;
	private Student student1;
	private final String professorUsername = "student@mail.com";
	private final String defaultPasswordSha3 = "363999f7918bb84260f481cceaed396fb046e8dc25750c5c3ae0e8088ae17b22";
	private final LocalDateTime now = LocalDateTime.now().withNano(0);

	@BeforeEach
	public void beforeEach() {
		studentRepository.deleteAll();
		accountRepository.deleteAll();

		String student1Mail = "student1@mail.com";
		String student2Mail = "student2@mail.com";
		String student3Mail = "student3@mail.com";
		String professor1Mail = "professor1@mail.com";
		String professor2Mail = "professor2@mail.com";

		Account account1 = accountRepository.save(new Account(professor1Mail, defaultPasswordSha3, Role.Professor));
		professorRepository.save(new Professor(account1.getId(), "Professor 1 name", "Professor 1 surname"));
		Account account2 = accountRepository.save(new Account(professor2Mail, defaultPasswordSha3, Role.Professor));
		professorRepository.save(new Professor(account2.getId(), "Professor 2 name", "Professor 2 surname"));
		Account account3 = accountRepository.save(new Account(student1Mail, defaultPasswordSha3, Role.Student));
		student1 = studentRepository.save(new Student(account3.getId(), "Student 2 name", "Student 2 surname"));
		Account account4 = accountRepository.save(new Account(student2Mail, defaultPasswordSha3, Role.Student));
		studentRepository.save(new Student(account4.getId(), "Student 3 name", "Student 3 surname"));
		Account account5 = accountRepository.save(new Account(student3Mail, defaultPasswordSha3, Role.Student));
		studentRepository.save(new Student(account5.getId(), "Student 4 name", "Student 4 surname"));

		Account account = accountRepository.save(new Account(professorUsername, defaultPasswordSha3, Role.Professor));
		professor = professorRepository.save(new Professor(account.getId(), "My name", "My surname"));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getProfile() {
		Professor result = professorService.getProfile();
		assertTrue(professorRepository.findById(professor.getId()).isPresent());
		assertEquals(professorRepository.findById(professor.getId()).get(), result);
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getProfessors() {
		Page<Professor> professorPage = professorRepository.findAll(PageRequest.of(0, 10));
		assertEquals(professorPage, professorService.getProfessors(0, 10));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getProfessor() {
		assertEquals(professor, professorService.getProfessor(professor.getId()));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getGroups() {
		groupRepository.save(new Group(professor.getId(), student1.getId(), "Test Group"));
		assertEquals(1, groupRepository.count());
		Page<Group> groupPage = groupRepository.findAllByStudentIdAndDeletedFalse(student1.getId(), PageRequest.of(0, 10));
		log.warn(String.valueOf(groupPage));
		assertEquals(1, groupPage.getNumberOfElements());
		assertEquals(groupPage, professorService.getGroups(0, 10));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getGroup() {
		Group group = groupRepository.save(new Group(professor.getId(), student1.getId(), "Test Group"));
		assertEquals(1, groupRepository.count());
		assertEquals(group, professorService.getGroup(group.getId()));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getStudents() {
		Page<Student> studentPage = studentRepository.findAll(PageRequest.of(0, 10));
		assertEquals(studentPage, professorService.getStudents(0, 10));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getStudent() {
		assertEquals(student1, professorService.getStudent(student1.getId()));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getFiles() {
		Group group = groupRepository.save(new Group(professor.getId(), student1.getId(), "Test Group"));
		fileRepository.saveAll(List.of(
				new File(group.getId(), "Exam 03/10 - 1", UUID.randomUUID().toString()),
				new File(group.getId(), "Exam 03/10 - 2", UUID.randomUUID().toString()),
				new File(group.getId(), "Exam 03/10 - 3", UUID.randomUUID().toString()),
				new File(group.getId(), "Exam 03/10 - 4", UUID.randomUUID().toString()),
				new File(group.getId(), "Exam 03/10 - 5", UUID.randomUUID().toString())
		));
		Page<File> filePage = fileRepository.findAllByGroupIdAndDeletedFalseOrderByCreatedOnDesc(group.getId(), PageRequest.of(0, 10));
		assertEquals(filePage, professorService.getFiles(group.getId(), 0, 10));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getFile() {
		Group group = groupRepository.save(new Group(professor.getId(), student1.getId(), "Test Group"));
		File file = fileRepository.save(new File(group.getId(), "Exam 03/10", UUID.randomUUID().toString()));
		assertEquals(1, fileRepository.count());
		assertEquals(file, professorService.getFile(file.getId()));
		assertEquals(1, fileRepository.count());
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getDiscussions() {
		discussionRepository.saveAll(List.of(
				new Discussion(professor.getId(), "Exam 03/10", now.plusDays(1)),
				new Discussion(professor.getId(), "Exam 04/10", now.plusDays(2)),
				new Discussion(professor.getId(), "Exam 05/10", now.plusDays(3)),
				new Discussion(professor.getId(), "Exam 06/10", now.plusDays(4)),
				new Discussion(professor.getId(), "Exam 07/10", now.plusDays(5))
		));
		Page<Discussion> discussionPage = discussionRepository.findAllByProfessorIdAndDeletedFalse(professor.getId(), PageRequest.of(0, 10));
		assertEquals(discussionPage, professorService.getDiscussions(0, 10));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getDiscussion() {
		Discussion discussion = discussionRepository.save(new Discussion(professor.getId(), "Exam 03/10", now.plusDays(1)));
		assertEquals(1, discussionRepository.count());
		assertEquals(discussion, professorService.getDiscussion(discussion.getId()));
		assertEquals(1, discussionRepository.count());
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void createDiscussion() {
		Discussion discussion = professorService.createDiscussion("Exam 03/10", now.plusDays(1));
		assertTrue(discussionRepository.findById(discussion.getId()).isPresent());
		assertEquals(discussionRepository.findById(discussion.getId()).get(), discussion);
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void updateDiscussion() {
		Discussion discussion = discussionRepository.save(new Discussion(professor.getId(), "Exam 03/10", now.plusDays(1)));
		assertEquals(1, discussionRepository.count());
		Discussion updatedDiscussion = professorService.updateDiscussion(discussion.getId(), "Exam 04/10", now.plusDays(2));
		assertEquals(1, discussionRepository.count());
		assertTrue(discussionRepository.findById(updatedDiscussion.getId()).isPresent());
		assertEquals("Exam 04/10", discussionRepository.findById(updatedDiscussion.getId()).get().getName());
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void deleteDiscussion() {
		Discussion discussion = discussionRepository.save(new Discussion(professor.getId(), "Exam 03/10", now.plusDays(1)));
		assertEquals(1, discussionRepository.count());
		assertTrue(discussionRepository.findById(discussion.getId()).isPresent());
		assertFalse(discussionRepository.findById(discussion.getId()).get().getDeleted());
		professorService.deleteDiscussion(discussion.getId());
		assertEquals(1, discussionRepository.count());
		assertTrue(discussionRepository.findById(discussion.getId()).isPresent());
		assertTrue(discussionRepository.findById(discussion.getId()).get().getDeleted());
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getReservations() {
		Group group = groupRepository.save(new Group(professor.getId(), student1.getId(), "Test Group"));
		discussionRepository.saveAll(List.of(
				new Discussion(professor.getId(), "Exam 03/10", now.plusDays(1)),
				new Discussion(professor.getId(), "Exam 04/10", now.plusDays(2)),
				new Discussion(professor.getId(), "Exam 05/10", now.plusDays(3)),
				new Discussion(professor.getId(), "Exam 06/10", now.plusDays(4)),
				new Discussion(professor.getId(), "Exam 07/10", now.plusDays(5))
		)).forEach(discussion -> reservationRepository.save(new Reservation(group.getId(), discussion.getId())));
		Page<Reservation> reservationPage = reservationRepository.findAllByGroupIdAndDeletedFalse(group.getId(), PageRequest.of(0, 10));
		assertEquals(reservationPage, professorService.getReservations(0, 10));
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void getReservation() {
		Group group = groupRepository.save(new Group(professor.getId(), student1.getId(), "Test Group"));
		discussionRepository.saveAll(List.of(
				new Discussion(professor.getId(), "Exam 03/10", now.plusDays(1)),
				new Discussion(professor.getId(), "Exam 04/10", now.plusDays(2)),
				new Discussion(professor.getId(), "Exam 05/10", now.plusDays(3)),
				new Discussion(professor.getId(), "Exam 06/10", now.plusDays(4)),
				new Discussion(professor.getId(), "Exam 07/10", now.plusDays(5))
		));
		Discussion discussion = discussionRepository.findAll().get(0);
		Reservation reservation = reservationRepository.save(new Reservation(group.getId(), discussion.getId()));
		assertEquals(1, reservationRepository.count());
		assertEquals(reservation, professorService.getReservation(reservation.getId()));
		assertEquals(1, reservationRepository.count());
	}

	@Test
	@WithMockUser(username = professorUsername, password = defaultPasswordSha3, authorities = {"Professor"})
	public void deleteReservation() {
		Group group = groupRepository.save(new Group(professor.getId(), student1.getId(), "Test Group"));
		Discussion discussion = discussionRepository.save(new Discussion(professor.getId(), "Exam 03/10", now.plusDays(1)));
		Reservation reservation = reservationRepository.save(new Reservation(group.getId(), discussion.getId()));
		assertEquals(1, reservationRepository.count());
		professorService.deleteReservation(reservation.getId());
		assertEquals(1, reservationRepository.count());
		assertTrue(reservationRepository.findById(reservation.getId()).isPresent());
		assertTrue(reservationRepository.findById(reservation.getId()).get().getDeleted());
	}

}
