package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

@Data
public class NewsFilterDto {

	@Parameter(description = "Filter by author's name (exact match, case-insensitive)")
	private String authorName;

	@Parameter(description = "Filter by category ID (interpreted as 'type')")
	private Integer categoryId;
}
