package net.developerpass.polimi.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FellowStudent extends HibernateEntity implements Serializable {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Basic
	@Column(name = "studentId", nullable = false)
	private Long studentId;
	@Basic
	@Column(name = "groupId", nullable = false)
	private Long groupId;
	@Basic
	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	public FellowStudent(Long studentId, Long groupId) {
		this.studentId = studentId;
		this.groupId = groupId;
	}

	@ManyToOne
	@OnDelete(action = CASCADE)
	@JoinColumn(name = "studentId", insertable = false, updatable = false)
	private Student student;

}
