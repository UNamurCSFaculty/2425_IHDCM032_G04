package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.NewsService;
import be.labil.anacarde.domain.dto.db.NewsDto;
import be.labil.anacarde.domain.dto.db.NewsPageDto;
import be.labil.anacarde.domain.dto.write.NewsCreateDto;
import be.labil.anacarde.domain.dto.write.NewsFilterDto;
import be.labil.anacarde.domain.dto.write.NewsUpdateDto;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class NewsApiController implements NewsApi {

	private final NewsService newsService;

	@Override
	public ResponseEntity<NewsDto> createNews(NewsCreateDto newsDto) {
		NewsDto createdNews = newsService.createNews(newsDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(createdNews.getId()).toUri();
		return ResponseEntity.created(location).body(createdNews);
	}

	@Override
	public ResponseEntity<NewsDto> getNews(Integer id) {
		NewsDto newsDto = newsService.getNewsById(id);
		return ResponseEntity.ok(newsDto);
	}

	@Override
	public ResponseEntity<NewsPageDto> listNews(NewsFilterDto requestDto, Pageable pageable) {
		Page<NewsDto> newsPage = newsService.listNews(requestDto, pageable);
		NewsPageDto newsPageDto = new NewsPageDto(newsPage.getContent(), newsPage.getTotalPages(),
				newsPage.getTotalElements(), newsPage.getNumber(), newsPage.getSize(),
				newsPage.isFirst(), newsPage.isLast(), newsPage.getNumberOfElements(),
				newsPage.isEmpty());
		return ResponseEntity.ok(newsPageDto);
	}

	@Override
	public ResponseEntity<NewsDto> updateNews(Integer id, NewsUpdateDto newsDto) {
		NewsDto updatedNews = newsService.updateNews(id, newsDto);
		return ResponseEntity.ok(updatedNews);
	}

	@Override
	public ResponseEntity<Void> deleteNews(Integer id) {
		newsService.deleteNews(id);
		return ResponseEntity.noContent().build();
	}
}
