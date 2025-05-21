package be.labil.anacarde.application.service.storage;

import be.labil.anacarde.application.exception.DocumentStorageException;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.User;
import com.github.slugify.Slugify;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Implémentation « Amazon S3 » – activée si le profil <b>s3</b> est présent.
 **/
@Service
@Profile("s3")
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

	private final S3Client s3;

	@Value("${storage.s3.bucket}")
	private String bucket;

	@Override
	public List<Document> storeAll(User user, List<MultipartFile> files) {
		List<Document> result = new ArrayList<>();
		for (MultipartFile f : files) {
			String key = "users/%d/%d_%s".formatted(user.getId(), System.currentTimeMillis(),
					f.getOriginalFilename());
			key = Slugify.builder().build().slugify(key);
			try (var in = f.getInputStream()) {
				s3.putObject(
						PutObjectRequest.builder().bucket(bucket).key(key)
								.contentType(f.getContentType()).build(),
						RequestBody.fromInputStream(in, f.getSize()));
			} catch (IOException e) {
				throw new DocumentStorageException("Impossible de stocker les fichiers", e);
			}
			result.add(
					DiskStorageUtils.buildDocument(user, f, "s3://%s/%s".formatted(bucket, key)));
		}
		return result;
	}

	@Override
	public InputStream get(Document d) {
		var resp = s3
				.getObject(GetObjectRequest.builder().bucket(bucket).key(extractKey(d)).build());
		return resp;
	}

	@Override
	public void delete(Document d) {
		s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(extractKey(d)).build());
	}

	private String extractKey(Document d) {
		return d.getStoragePath().split("/", 4)[3];
	}
}