package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.OperationNotAllowedException;
import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.NewsCategoryDto;
import be.labil.anacarde.domain.mapper.NewsCategoryMapper;
import be.labil.anacarde.domain.model.NewsCategory;
import be.labil.anacarde.infrastructure.persistence.NewsCategoryRepository;
import be.labil.anacarde.infrastructure.persistence.NewsRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service pour la gestion des catégories de nouvelles.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class NewsCategoryServiceImpl implements NewsCategoryService {

	private final NewsCategoryRepository newsCategoryRepository;
	private final NewsRepository newsRepository;
	private final NewsCategoryMapper newsCategoryMapper;

	@Override
	public NewsCategoryDto createNewsCategory(NewsCategoryDto newsCategoryDto) {
		try {
			NewsCategory newsCategory = newsCategoryMapper.toEntity(newsCategoryDto);
			NewsCategory savedCategory = newsCategoryRepository.save(newsCategory);
			return newsCategoryMapper.toDto(savedCategory);
		} catch (DataIntegrityViolationException e) {
			throw new OperationNotAllowedException("Une catégorie de nouvelles avec le nom '"
					+ newsCategoryDto.getName() + "' existe déjà.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public NewsCategoryDto getNewsCategoryById(Integer id) {
		return newsCategoryRepository.findById(id).map(newsCategoryMapper::toDto)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Catégorie de nouvelles non trouvée avec l'ID : " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<NewsCategoryDto> listNewsCategories() {
		return newsCategoryRepository.findAll().stream().map(newsCategoryMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public NewsCategoryDto updateNewsCategory(Integer id, NewsCategoryDto newsCategoryDto) {
		NewsCategory existingCategory = newsCategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Catégorie de nouvelles non trouvée avec l'ID : " + id));

		// Vérifie l'unicité du nom si le nom est modifié
		if (newsCategoryDto.getName() != null
				&& !newsCategoryDto.getName().equals(existingCategory.getName())) {
			newsCategoryRepository.findAll().stream()
					.filter(cat -> cat.getName().equals(newsCategoryDto.getName())
							&& !cat.getId().equals(id))
					.findFirst().ifPresent(cat -> {
						throw new OperationNotAllowedException(
								"Une catégorie de nouvelles avec le nom '"
										+ newsCategoryDto.getName() + "' existe déjà.");
					});
		}

		NewsCategory updatedCategory = newsCategoryMapper.partialUpdate(newsCategoryDto,
				existingCategory);
		NewsCategory savedCategory = newsCategoryRepository.save(updatedCategory);
		return newsCategoryMapper.toDto(savedCategory);
	}

	@Override
	public void deleteNewsCategory(Integer id) {
		if (!newsCategoryRepository.existsById(id)) {
			throw new ResourceNotFoundException(
					"Catégorie de nouvelles non trouvée avec l'ID : " + id);
		}
		// Vérifie si des articles de nouvelles sont associés à cette catégorie
		if (newsRepository.findByCategoryId(id, Pageable.ofSize(1)).hasContent()) {
			throw new OperationNotAllowedException("Impossible de supprimer la catégorie avec l'ID "
					+ id + " car elle est associée à des articles de nouvelles existants.");
		}
		newsCategoryRepository.deleteById(id);
	}
}