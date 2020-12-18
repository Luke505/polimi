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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"Group\"")
@EqualsAndHashCode(callSuper = false)
public class Group extends HibernateEntity implements Serializable {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Basic
	@Column(name = "professorId", nullable = false)
	private Long professorId;
	@Basic
	@Column(name = "adminId", nullable = false)
	private Long adminId;
	@Basic
	@Column(nullable = false)
	private String name;
	@Basic
	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	public Group(Long professorId, Long adminId, String name) {
		this.professorId = professorId;
		this.adminId = adminId;
		this.name = name;
	}

	@OneToOne
	@OnDelete(action = CASCADE)
	@JoinColumn(name = "professorId", referencedColumnName = "id", insertable = false, updatable = false)
	private Professor professor;

	@OneToOne
	@OnDelete(action = CASCADE)
	@JoinColumn(name = "adminId", referencedColumnName = "id", insertable = false, updatable = false)
	private Student admin;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "groupId", referencedColumnName = "id", insertable = false, updatable = false)
	private List<FellowStudent> fellowStudent = new ArrayList<>();

}
