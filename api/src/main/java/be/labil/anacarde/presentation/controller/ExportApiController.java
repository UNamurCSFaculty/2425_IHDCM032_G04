package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.export.ExportService;
import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExportApiController implements ExportApi {

	private final ExportService exportService;

	@Override
	public ResponseEntity<ExportAuctionDto> getAuction(Integer id) {
		return ResponseEntity.ok(exportService.getAuctionById(id));
	}

	@Override
	public ResponseEntity<List<ExportAuctionDto>> listAuctions(LocalDateTime start,
			LocalDateTime end, boolean onlyEnded) {
		return ResponseEntity.ok(exportService.getAnalysesBetween(start, end, onlyEnded));
	}

	@Override
	public ResponseEntity<?> listAllAuctions(String format) throws JsonProcessingException {
		List<ExportAuctionDto> dtos = exportService.getAllAnalyses();

		if ("csv".equalsIgnoreCase(format)) {
			CsvMapper csvMapper = (CsvMapper) new CsvMapper().findAndRegisterModules()
					.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

			CsvSchema schema = csvMapper.schemaFor(ExportAuctionDto.class).withHeader()
					.withColumnSeparator(';');

			String csv = csvMapper.writer(schema).writeValueAsString(dtos);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("text/csv"));
			headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"auctions.csv\"");

			return new ResponseEntity<>(csv, headers, HttpStatus.OK);
		}

		// JSON par défaut
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(dtos);
	}
}
