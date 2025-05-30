package be.labil.anacarde;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@TestConfiguration
public class TestRedisMocks {

	@Bean
	public StringRedisTemplate mockRedisTemplate() {
		// Création du mock principal
		StringRedisTemplate mockTemplate = Mockito.mock(StringRedisTemplate.class);

		// Mock du comportement de opsForSet()
		@SuppressWarnings("unchecked")
		SetOperations<String, String> setOpsMock = Mockito.mock(SetOperations.class);

		// Quand on appelle opsForSet(), on retourne le mock
		Mockito.when(mockTemplate.opsForSet()).thenReturn(setOpsMock);

		return mockTemplate;
	}
}