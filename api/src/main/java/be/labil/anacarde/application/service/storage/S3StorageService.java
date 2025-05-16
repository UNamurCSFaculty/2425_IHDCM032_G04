package be.labil.anacarde.application.service.storage;

import be.labil.anacarde.domain.model.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Implémentation « Amazon S3 » – activée si le profil <b>s3</b> est présent.
 */
@Service
@Profile("s3")
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

	private final S3Client s3;

	/** Nom du bucket – ex : storage.s3.bucket=anacarde-files */
	@Value("${storage.s3.bucket}")
	private final String bucket;

	@Override
	public List<Document> storeAll(Integer userId, List<MultipartFile> files) throws IOException {
		List<Document> result = new ArrayList<>();
		for (MultipartFile f : files) {
			String key = "users/%d/%d_%s".formatted(userId, System.currentTimeMillis(),
					f.getOriginalFilename());

			// Récupération et envoi du flux : IOException peut remonter naturellement
			try (var in = f.getInputStream()) {
				s3.putObject(
						PutObjectRequest.builder().bucket(bucket).key(key)
								.contentType(f.getContentType()).build(),
						RequestBody.fromInputStream(in, f.getSize()));
			}

			result.add(buildDocument(f, "s3://%s/%s".formatted(bucket, key)));
		}
		return result;
	}
	/* Utilitaire commun ---------------------------------------------------- */

	private static Document buildDocument(MultipartFile file, String storagePath) {
		String ext = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
		return null;
		/*
		 * return Document.builder().contentType(file.getContentType())
		 * .originalFilename(file.getOriginalFilename()).size(file.getSize()).extension(ext)
		 * .storagePath(storagePath).uploadDate(LocalDateTime.now()).build();
		 * 
		 */
	}

	private static String getExtension(String filename) {
		int i = filename.lastIndexOf('.');
		return (i > 0 && i < filename.length() - 1) ? filename.substring(i + 1).toLowerCase() : "";
	}
}