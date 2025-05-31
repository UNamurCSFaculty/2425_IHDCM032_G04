package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.NewsDto;
import be.labil.anacarde.domain.dto.write.NewsCreateDto;
import be.labil.anacarde.domain.dto.write.NewsFilterDto;
import be.labil.anacarde.domain.dto.write.NewsUpdateDto;
import be.labil.anacarde.domain.mapper.NewsMapper;
import be.labil.anacarde.domain.model.News;
import be.labil.anacarde.domain.model.NewsCategory;
import be.labil.anacarde.infrastructure.persistence.NewsCategoryRepository;
import be.labil.anacarde.infrastructure.persistence.NewsRepository;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implémentation du service pour la gestion des articles de nouvelles.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

	private final NewsRepository newsRepository;
	private final NewsCategoryRepository newsCategoryRepository;
	private final UserRepository userRepository;
	private final NewsMapper newsMapper;

	@Override
	public NewsDto createNews(NewsCreateDto dto) {
		News news = newsMapper.toEntity(dto);

		// Auteur
		if (!StringUtils.hasText(dto.getAuthorName())) {
			news.setAuthorName(resolveCurrentUserName().orElse("Admin"));
		} else {
			news.setAuthorName(dto.getAuthorName());
		}

		if (dto.getPublicationDate() == null) {
			news.setPublicationDate(java.time.LocalDateTime.now());
		}

		// Catégorie
		NewsCategory category = newsCategoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Catégorie non trouvée avec l'ID : " + dto.getCategoryId()));
		news.setCategory(category);

		return newsMapper.toDto(newsRepository.save(news));
	}

	@Override
	@Transactional(readOnly = true)
	public NewsDto getNewsById(Integer id) {
		return newsRepository.findById(id).map(newsMapper::toDto).orElseThrow(
				() -> new ResourceNotFoundException("Article non trouvé avec l'ID : " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<NewsDto> listNews(NewsFilterDto filters, Pageable pageable) {
		Specification<News> spec = buildSpecification(filters);
		return newsRepository.findAll(spec, pageable).map(newsMapper::toDto);
	}

	@Override
	public NewsDto updateNews(Integer id, NewsUpdateDto dto) {
		News existing = newsRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Article non trouvé avec l'ID : " + id));

		String originalAuthor = existing.getAuthorName();
		News updated = newsMapper.partialUpdate(dto, existing);
		updated.setAuthorName(originalAuthor);

		if (dto.getCategoryId() != null) {
			NewsCategory category = newsCategoryRepository.findById(dto.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Catégorie non trouvée avec l'ID : " + dto.getCategoryId()));
			updated.setCategory(category);
		}
		return newsMapper.toDto(newsRepository.save(updated));
	}

	@Override
	public void deleteNews(Integer id) {
		if (!newsRepository.existsById(id)) {
			throw new ResourceNotFoundException("Article non trouvé avec l'ID : " + id);
		}
		newsRepository.deleteById(id);
	}

	/* ------------------------- PRIVATE HELPERS ------------------------- */

	private Specification<News> buildSpecification(NewsFilterDto f) {
		return (root, query, cb) -> {
			List<Predicate> p = new ArrayList<>();
			if (StringUtils.hasText(f.getAuthorName())) {
				p.add(cb.equal(cb.lower(root.get("authorName")), f.getAuthorName().toLowerCase()));
			}
			if (f.getCategoryId() != null) {
				p.add(cb.equal(root.get("category").get("id"), f.getCategoryId()));
			}
			return cb.and(p.toArray(new Predicate[0]));
		};
	}

	private Optional<String> resolveCurrentUserName() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails ud) {
			return userRepository.findByEmail(ud.getUsername())
					.map(u -> u.getFirstName() + " " + u.getLastName());
		}
		return Optional.empty();
	}
}
