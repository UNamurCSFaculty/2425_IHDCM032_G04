package be.labil.anacarde.application.service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class AuctionSseService {
	private static final Logger log = LoggerFactory.getLogger(AuctionSseService.class);
	private final ConcurrentMap<Integer, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
	private final StringRedisTemplate redisTemplate;

	@Autowired
	public AuctionSseService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public SseEmitter subscribe(Integer auctionId, String userKey) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.computeIfAbsent(auctionId, k -> new CopyOnWriteArrayList<>()).add(emitter);
		redisTemplate.opsForSet().add("auction-subscribers:" + auctionId, userKey);

		emitter.onCompletion(() -> emitters.get(auctionId).remove(emitter));
		emitter.onTimeout(() -> emitters.get(auctionId).remove(emitter));
		return emitter;
	}

	public void sendEvent(Integer auctionId, String eventName, Object data) {
		var list = emitters.getOrDefault(auctionId, new CopyOnWriteArrayList<>());
		for (SseEmitter emitter : list) {
			try {
				emitter.send(
						SseEmitter.event().name(eventName).data(data, MediaType.APPLICATION_JSON));
			} catch (IOException e) {
				emitter.complete();
				list.remove(emitter);
			}
		}
	}

	public Set<String> getSubscribers(Integer auctionId) {
		Set<String> subs = redisTemplate.opsForSet().members("auction-subscribers:" + auctionId);
		log.info("[SSE] Abonnés à l'enchère " + auctionId + ": " + subs);
		return subs;
	}

	public void addSubscriber(Integer auctionId, String userKey) {
		redisTemplate.opsForSet().add("auction-subscribers:" + auctionId, userKey); // devrait être
																					// idempotent
		log.info("[SSE] Abonné " + userKey + " ajouté à l'enchère ID " + auctionId);
	}
}
