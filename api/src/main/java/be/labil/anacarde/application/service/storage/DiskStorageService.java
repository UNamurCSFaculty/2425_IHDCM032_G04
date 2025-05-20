package be.labil.anacarde.application.service.storage;

import be.labil.anacarde.domain.model.Document;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implémentation « disque local » – activée si le profil <b>disk</b> est présent.
 */
@Service
@Profile("disk")
@RequiredArgsConstructor
public class DiskStorageService implements StorageService {

	/** Répertoire racine des uploads – configurable : storage.disk.root=uploads */
	@Value("${storage.disk.root:uploads}")
	private final Path rootDir;

	@Override
	public List<Document> storeAll(Integer userId, List<MultipartFile> files) throws IOException {
		Files.createDirectories(rootDir);

		List<Document> result = new ArrayList<>();
		for (MultipartFile f : files) {
			String timestamped = "%d_%d_%s".formatted(userId, System.currentTimeMillis(),
					f.getOriginalFilename());
			Path target = rootDir.resolve(timestamped);

			try (var in = f.getInputStream()) {
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			}

			result.add(buildDocument(userId, f, target.toString()));
		}
		return result;
	}

	/* Utilitaire commun ---------------------------------------------------- */

	private static Document buildDocument(Integer userId, MultipartFile file, String storagePath) {
		String ext = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
		/*
		 * return Document.builder().contentType(file.getContentType())
		 * .originalFilename(file.getOriginalFilename()).size(file.getSize()).extension(ext)
		 * .storagePath(storagePath).uploadDate(LocalDateTime.now()).build();
		 */
		return null;
	}

	private static String getExtension(String filename) {
		int i = filename.lastIndexOf('.');
		return (i > 0 && i < filename.length() - 1) ? filename.substring(i + 1).toLowerCase() : "";
	}
}