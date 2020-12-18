package net.developerpass.polimi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.LocalDateTime;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Discussion extends HibernateEntity implements Serializable {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Basic
	@Column(name = "professorId", nullable = false)
	private Long professorId;
	@Basic
	@Column(nullable = false)
	private String name;
	@Basic
	@Column(nullable = false, columnDefinition="DATETIME DEFAULT CURRENT_TIMESTAMP")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime date;
	@Basic
	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	public Discussion(Long professorId, String name, LocalDateTime date) {
		this.professorId = professorId;
		this.name = name;
		this.date = date;
	}

	@ManyToOne
	@OnDelete(action = CASCADE)
	@JoinColumn(name = "professorId", referencedColumnName = "id", insertable = false, updatable = false)
	private Professor professor;

}
