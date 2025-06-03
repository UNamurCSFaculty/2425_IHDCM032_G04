package be.labil.anacarde.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NotificationSseServiceImpl implements NotificationSseService {
	private static final Logger log = LoggerFactory.getLogger(NotificationSseServiceImpl.class);
	private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	@Autowired
	public NotificationSseServiceImpl(StringRedisTemplate redisTemplate,
			ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public SseEmitter subscribe(String userKey) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.computeIfAbsent(userKey, k -> new CopyOnWriteArrayList<>()).add(emitter);
		emitter.onCompletion(() -> emitters.get(userKey).remove(emitter));
		emitter.onTimeout(() -> emitters.get(userKey).remove(emitter));
		return emitter;
	}

	@Override
	public void sendEvent(String userKey, String eventName, Object data) {
		var list = emitters.getOrDefault(userKey, new CopyOnWriteArrayList<>());
		for (SseEmitter emitter : list) {
			try {
				emitter.send(SseEmitter.event().name(eventName).data(data));
			} catch (IOException e) {
				emitter.complete();
				list.remove(emitter);
			}
		}
	}

	@Override
	public void publishEvent(String userKey, String eventName, Object data) {
		try {
			RedisNotificationListenerImpl.RedisNotificationEvent event = new RedisNotificationListenerImpl.RedisNotificationEvent();
			event.setEventName(eventName);
			event.setData(data);
			String json = objectMapper.writeValueAsString(event);
			String channel = "notifications:user:" + userKey;
			redisTemplate.convertAndSend(channel, json);
			log.debug("[SSE] Event publié sur {}: {}", channel, json);
		} catch (Exception e) {
			log.error("[SSE] Event publié sur {}: {}", userKey, eventName, e);
		}
	}
}
