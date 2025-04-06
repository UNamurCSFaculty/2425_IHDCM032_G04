package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.ProducerDto;
import be.labil.anacarde.domain.mapper.ProducerMapper;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.infrastructure.persistence.BidderRepository;
import be.labil.anacarde.infrastructure.persistence.ProducerRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ProducerServiceImpl implements ProducerService {

	private final ProducerRepository producerRepository;
	private final ProducerMapper producerMapper;
	private final BidderRepository bidderRepository;

	@Override
	public ProducerDto createProducer(ProducerDto producerDto) {
		if (!bidderRepository.existsById(producerDto.getBidderId())) {
			throw new ResourceNotFoundException("Bidder associé non trouvé");
		}
		Producer producer = producerMapper.toEntity(producerDto);
		Producer saved = producerRepository.save(producer);
		return producerMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public ProducerDto getProducerById(Integer id) {
		Producer producer = producerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Producteur non trouvé"));
		return producerMapper.toDto(producer);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProducerDto> listProducers() {
		return producerRepository.findAll().stream().map(producerMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public ProducerDto updateProducer(Integer id, ProducerDto producerDto) {
		Producer existingProducer = producerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Producteur non trouvé"));

		Producer updatedProducer = producerMapper.partialUpdate(producerDto, existingProducer);
		Producer saved = producerRepository.save(updatedProducer);
		return producerMapper.toDto(saved);
	}

	@Override
	public void deleteProducer(Integer id) {
		if (!producerRepository.existsById(id)) {
			throw new ResourceNotFoundException("Producteur non trouvé");
		}
		producerRepository.deleteById(id);
	}
}
