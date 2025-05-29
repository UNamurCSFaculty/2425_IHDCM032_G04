package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.storage.StorageService;
import be.labil.anacarde.domain.dto.db.DocumentDto;
import be.labil.anacarde.domain.mapper.DocumentMapper;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import be.labil.anacarde.infrastructure.persistence.QualityControlRepository;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
class DocumentServiceImpl implements DocumentService {

	private final DocumentRepository docRepo;
	private final DocumentMapper mapper;
	private final UserRepository userRepo;
	private final QualityControlRepository qualityControlRepo;
	private final StorageService storage;

	/* ---------- création ---------- */

	@Override
	public DocumentDto createDocumentUser(Integer userId, MultipartFile file) {
		User user = userRepo.findById(userId).orElseThrow(
				() -> new ResourceNotFoundException("Utilisateur non trouvé : " + userId));

		Document stored = storage.storeAll(user, List.of(file)).getFirst();
		stored.setUploadDate(LocalDateTime.now());

		return mapper.toDto(docRepo.save(stored));
	}

	@Override
	public DocumentDto createDocumentQualityControl(Integer qualityControlId, MultipartFile file) {
		QualityControl qualityControl = qualityControlRepo.findById(qualityControlId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Contrôle qualité non trouvé : " + qualityControlId));

		Document stored = storage.storeAll(qualityControl, List.of(file)).getFirst();
		stored.setUploadDate(LocalDateTime.now());

		return mapper.toDto(docRepo.save(stored));
	}

	/* ---------- lecture ---------- */

	@Override
	@Transactional(readOnly = true)
	public DocumentDto getDocumentById(Integer id) {
		return mapper.toDto(findEntity(id));
	}

	/* ---------- suppression ---------- */

	@Override
	public void deleteDocument(Integer id) {
		Document doc = findEntity(id);
		storage.delete(doc);
		docRepo.delete(doc);
	}

	/* ---------- listing ---------- */

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> listDocumentsByUser(Integer userId) {
		return docRepo.findByUserId(userId).stream().map(mapper::toDto)
				.collect(Collectors.toList());
	}

	/* ---------- streaming ---------- */

	@Override
	@Transactional(readOnly = true)
	public InputStream streamDocumentContent(Integer id) {
		return storage.get(findEntity(id));
	}

	/* ---------- utilitaire ---------- */

	private Document findEntity(Integer id) {
		return docRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document non trouvé : " + id));
	}
}