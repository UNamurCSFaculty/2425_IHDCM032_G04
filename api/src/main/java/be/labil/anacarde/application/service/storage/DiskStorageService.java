package be.labil.anacarde.application.service.storage;

import be.labil.anacarde.application.exception.DocumentStorageException;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.domain.model.User;
import com.github.slugify.Slugify;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
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

	@Value("${storage.disk.root}")
	private Path rootDir;

	@Override
	public List<Document> storeAll(User user, List<MultipartFile> files) {
		if (user.getId() == null) throw new IllegalArgumentException("User ID is null");

		return storeAllGeneric(user.getId().toString(), files,
				(file, path) -> DiskStorageUtils.buildDocument(user, file, path));
	}

	@Override
	public List<Document> storeAll(QualityControl qualityControl, List<MultipartFile> files) {
		if (qualityControl.getId() == null)
			throw new IllegalArgumentException("QualityControl ID is null");

		return storeAllGeneric(qualityControl.getId().toString(), files,
				(file, path) -> DiskStorageUtils.buildDocument(qualityControl, file, path));
	}

	@Override
	public InputStream get(Document doc) {
		try {
			return Files.newInputStream(Paths.get(doc.getStoragePath()), StandardOpenOption.READ);
		} catch (IOException e) {
			throw new DocumentStorageException("Impossible de lire le document", e);
		}
	}

	@Override
	public void delete(Document doc) {
		try {
			Files.deleteIfExists(Paths.get(doc.getStoragePath()));
		} catch (IOException e) {
			throw new DocumentStorageException("Impossible de supprimer le document", e);
		}
	}

	private List<Document> storeAllGeneric(String uniqueId, List<MultipartFile> files,
			BiFunction<MultipartFile, String, Document> documentBuilder) {
		try {
			Files.createDirectories(rootDir);

			List<Document> result = new ArrayList<>();
			for (MultipartFile file : files) {
				Path target = getFiletTarget(file, uniqueId);

				try (var in = file.getInputStream()) {
					Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
				}

				result.add(documentBuilder.apply(file, target.toString()));
			}
			return result;
		} catch (IOException e) {
			throw new DocumentStorageException("Impossible de stocker les documents", e);
		}
	}

	private Path getFiletTarget(MultipartFile file, String uniqueId) {
		String ts = String.valueOf(System.currentTimeMillis());

		String filename = "%s_%s_%s".formatted(uniqueId, ts, file.getOriginalFilename());
		filename = Slugify.builder().build().slugify(filename);

		Path target = rootDir.resolve(filename);
		return target;
	}
}