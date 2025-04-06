package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "notification", indexes = {@Index(columnList = "user_id")})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_notification")
	private Integer id;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private LocalDateTime sendDate;

	@Column(nullable = false)
	private String readStatus;

	@ManyToOne(optional = false)
	private User user;
}
