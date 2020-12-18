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
public class Reservation extends HibernateEntity implements Serializable {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Basic
	@Column(name = "groupId", nullable = false)
	private Long groupId;
	@Basic
	@Column(name = "discussionId", nullable = false)
	private Long discussionId;
	@Basic
	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	public Reservation(Long groupId, Long discussionId) {
		this.groupId = groupId;
		this.discussionId = discussionId;
	}

	@ManyToOne
	@OnDelete(action = CASCADE)
	@JoinColumn(name = "groupId", insertable = false, updatable = false)
	private Group group;

	@ManyToOne
	@OnDelete(action = CASCADE)
	@JoinColumn(name = "discussionId", insertable = false, updatable = false)
	private Discussion discussion;

}
