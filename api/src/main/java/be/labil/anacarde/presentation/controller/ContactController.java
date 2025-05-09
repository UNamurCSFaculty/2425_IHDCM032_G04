package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.write.ContactRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContactController implements ContactApi {

	private final JavaMailSender mailSender;

	/**
	 * Adresse de destination des messages de contact, à paramétrer dans application.yml.
	 */
	@Value("${spring.mail.contact.to}")
	private String contactTo;

	@Override
	public ResponseEntity<Void> sendContactMessage(ContactRequestDto dto) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(contactTo);
		msg.setReplyTo(dto.getEmail());
		msg.setSubject("Nouveau message de contact de " + dto.getName());
		msg.setText(dto.getMessage());
		mailSender.send(msg);

		return ResponseEntity.ok().build();
	}
}
