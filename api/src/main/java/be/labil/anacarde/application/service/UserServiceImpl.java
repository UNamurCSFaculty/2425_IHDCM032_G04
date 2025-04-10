package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.BadRequestException;
import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.user.UserDetailDto;
import be.labil.anacarde.domain.dto.user.UserListDto;
import be.labil.anacarde.domain.mapper.*;
import be.labil.anacarde.domain.model.Role;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.RoleRepository;
import be.labil.anacarde.infrastructure.persistence.user.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

	private final RoleRepository roleRepository;

	private final UserRepository userRepository;
	private final AdminRepository adminRepository;
	private final ProducerRepository producerRepository;
	private final TransformerRepository transformerRepository;
	private final ExporterRepository exporterRepository;
	private final QualityInspectorRepository qualityInspectorRepository;
	private final CarrierRepository carrierRepository;

	private final UserDetailMapper userDetailMapper;
	private final AdminDetailMapper adminDetailMapper;
	private final ProducerDetailMapper producerDetailMapper;
	private final TransformerDetailMapper transformerDetailMapper;
	private final ExporterDetailMapper exporterDetailMapper;
	private final QualityInspectorDetailMapper qualityInspectorDetailMapper;
	private final CarrierDetailMapper carrierDetailMapper;

	private final UserListMapper userListMapper;
	private final PasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));
	}

	@Override
	public UserDetailDto createUser(UserDetailDto dto) {
		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
		}

		User user = userDetailMapper.toEntity(dto);
		User saved = userRepository.save(user);
		return userDetailMapper.toDto(saved);

		/*
		 * Si il faut une logiqiue de création spécifique pour chaque type d'utilisateur: switch (dto) { case
		 * AdminDetailDto adminDetailDto -> { Admin admin = adminDetailMapper.toEntity(adminDetailDto); Admin saved =
		 * adminRepository.save(admin); return adminDetailMapper.toDto(saved); } case ProducerDetailDto
		 * producerDetailDto -> { Producer producer = producerDetailMapper.toEntity(producerDetailDto); Producer saved =
		 * producerRepository.save(producer); return producerDetailMapper.toDto(saved); } case TransformerDetailDto
		 * transformerDetailDto -> { Transformer transformer = transformerDetailMapper.toEntity(transformerDetailDto);
		 * Transformer saved = transformerRepository.save(transformer); return transformerDetailMapper.toDto(saved); }
		 * case ExporterDetailDto exporterDetailDto -> { Exporter exporter =
		 * exporterDetailMapper.toEntity(exporterDetailDto); Exporter saved = exporterRepository.save(exporter); return
		 * exporterDetailMapper.toDto(saved); } case QualityInspectorDetailDto qualityInspectorDetailDto -> {
		 * QualityInspector qualityInspector = qualityInspectorDetailMapper.toEntity(qualityInspectorDetailDto);
		 * QualityInspector saved = qualityInspectorRepository.save(qualityInspector); return
		 * qualityInspectorDetailMapper.toDto(saved); } case CarrierDetailDto carrierDetailDto -> { Carrier
		 * qualityInspector = carrierDetailMapper.toEntity(carrierDetailDto); Carrier saved =
		 * carrierRepository.save(qualityInspector); return carrierDetailMapper.toDto(saved); } default -> throw new
		 * IllegalArgumentException("Type d'utilisateur non supporté : " + dto.getClass()); }
		 */
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetailDto getUserById(Integer id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		return userDetailMapper.toDto(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserListDto> listUsers() {
		return userRepository.findAll().stream().map(userListMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public UserDetailDto updateUser(Integer id, UserDetailDto userDetailDto) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		// Mets uniquement à jour les champs non nuls du DTO
		User updatedUser = userDetailMapper.partialUpdate(userDetailDto, existingUser);

		if (userDetailDto.getPassword() != null && !userDetailDto.getPassword().isBlank()) {
			updatedUser.setPassword(bCryptPasswordEncoder.encode(userDetailDto.getPassword()));
		}

		User saved = userRepository.save(updatedUser);
		return userDetailMapper.toDto(saved);
	}

	@Override
	public void deleteUser(Integer id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Utilisateur non trouvé");
		}
		userRepository.deleteById(id);
	}

	@Override
	public UserDetailDto addRoleToUser(Integer userId, String roleName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé"));

		if (user.getRoles().contains(role)) {
			throw new BadRequestException("Le rôle est déjà attribué à l'utilisateur");
		}
		user.getRoles().add(role);

		User savedUser = userRepository.save(user);
		return userDetailMapper.toDto(savedUser);
	}

	@Override
	public UserDetailDto updateUserRoles(Integer userId, List<String> roleNames) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

		Set<Role> newRoles = roleNames.stream()
				.map(roleName -> roleRepository.findByName(roleName)
						.orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + roleName)))
				.collect(Collectors.toSet());

		user.setRoles(newRoles);

		User savedUser = userRepository.save(user);
		return userDetailMapper.toDto(savedUser);
	}
}
