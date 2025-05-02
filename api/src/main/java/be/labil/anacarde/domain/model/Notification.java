package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification", indexes = {@Index(columnList = "user_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private LocalDateTime sendDate;

	@Column(nullable = false)
	private String readStatus;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
