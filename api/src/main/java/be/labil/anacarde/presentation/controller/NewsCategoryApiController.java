package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.NewsCategoryService;
import be.labil.anacarde.domain.dto.db.NewsCategoryDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class NewsCategoryApiController implements NewsCategoryApi {

	private final NewsCategoryService newsCategoryService;

	@Override
	public ResponseEntity<NewsCategoryDto> createNewsCategory(NewsCategoryDto newsCategoryDto) {
		NewsCategoryDto createdCategory = newsCategoryService.createNewsCategory(newsCategoryDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(createdCategory.getId()).toUri();
		return ResponseEntity.created(location).body(createdCategory);
	}

	@Override
	public ResponseEntity<NewsCategoryDto> getNewsCategory(Integer id) {
		NewsCategoryDto categoryDto = newsCategoryService.getNewsCategoryById(id);
		return ResponseEntity.ok(categoryDto);
	}

	@Override
	public ResponseEntity<List<NewsCategoryDto>> listNewsCategories() {
		List<NewsCategoryDto> categories = newsCategoryService.listNewsCategories();
		return ResponseEntity.ok(categories);
	}

	@Override
	public ResponseEntity<NewsCategoryDto> updateNewsCategory(Integer id,
			NewsCategoryDto newsCategoryDto) {
		NewsCategoryDto updatedCategory = newsCategoryService.updateNewsCategory(id,
				newsCategoryDto);
		return ResponseEntity.ok(updatedCategory);
	}

	@Override
	public ResponseEntity<Void> deleteNewsCategory(Integer id) {
		newsCategoryService.deleteNewsCategory(id);
		return ResponseEntity.noContent().build();
	}
}
