package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorException;
import be.labil.anacarde.application.service.export.ExportService;
import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminExportApiController implements AdminExportApi {

	private final ExportService exportService;
	private final ObjectMapper objectMapper;

	@Override
	public ResponseEntity<Resource> getFilteredData(LocalDateTime start, LocalDateTime end,
			boolean onlyEnded, String format) {
		if ((start != null && end == null) || (start == null && end != null)) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
					"start",
					"Les paramètres 'start' et 'end' doivent être fournis ensemble ou omis ensemble.");
		}
		if (start != null && end != null && start.isAfter(end)) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
					"end", "La date 'start' ne peut pas être postérieure à la date 'end'.");
		}

		List<ExportAuctionDto> dtos;
		if (start != null && end != null) {
			log.info("Exporting auction data between {} and {}, onlyEnded: {}, format: {}", start,
					end, onlyEnded, format);
			dtos = exportService.getAnalysesBetween(start, end, onlyEnded);
		} else {
			log.info("Exporting all auction data, onlyEnded: {}, format: {}", onlyEnded, format);
			dtos = exportService.getAllAnalyses();
		}

		String timestamp = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String baseFilename = "auctions_export_" + timestamp;

		try {
			if ("csv".equalsIgnoreCase(format)) {
				return buildCsvFileResponse(dtos, baseFilename + ".csv");
			} else { // JSON par défaut
				return buildJsonFileResponse(dtos, baseFilename + ".json");
			}
		} catch (JsonProcessingException e) {
			log.error("Error generating export file (format: {}): {}", format, e.getMessage(), e);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					ApiErrorCode.INTERNAL_ERROR.code(), "start",
					"Erreur lors de la génération du fichier " + format.toUpperCase() + ".");
		}
	}

	private ResponseEntity<Resource> buildJsonFileResponse(List<ExportAuctionDto> dtos,
			String filename) throws JsonProcessingException {
		byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(dtos);
		return createFileResponse(bytes, filename, MediaType.APPLICATION_JSON);
	}

	private ResponseEntity<Resource> buildCsvFileResponse(List<ExportAuctionDto> dtos,
			String filename) throws JsonProcessingException {
		CsvMapper csvMapper = new CsvMapper();
		csvMapper.findAndRegisterModules();
		csvMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		CsvSchema schema = csvMapper.schemaFor(ExportAuctionDto.class).withHeader()
				.withColumnSeparator(';');
		String csvData = csvMapper.writer(schema).writeValueAsString(dtos);
		byte[] bytes = csvData.getBytes(StandardCharsets.UTF_8);
		return createFileResponse(bytes, filename, MediaType.parseMediaType("text/csv"));
	}

	private ResponseEntity<Resource> createFileResponse(byte[] data, String filename,
			MediaType mediaType) {
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		headers.setContentLength(data.length);
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
}