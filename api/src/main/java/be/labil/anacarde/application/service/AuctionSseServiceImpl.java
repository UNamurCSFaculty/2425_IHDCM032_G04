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
public class AuctionSseServiceImpl implements AuctionSseService {
	private static final Logger log = LoggerFactory.getLogger(AuctionSseServiceImpl.class);
	private final ConcurrentMap<Integer, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
	private final StringRedisTemplate redisTemplate;

	@Autowired
	public AuctionSseServiceImpl(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public SseEmitter subscribe(Integer auctionId, String userKey, boolean isVisitor) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.computeIfAbsent(auctionId, k -> new CopyOnWriteArrayList<>()).add(emitter);
		try {
			if (isVisitor) {
				redisTemplate.opsForSet().add("auction-visitors:" + auctionId, userKey);
				log.debug("[SSE] Visiteur " + userKey + " ajouté à l'enchère ID " + auctionId);
			} else {
				redisTemplate.opsForSet().add("auction-subscribers:" + auctionId, userKey);
			}
		} catch (Exception e) {
		}

		emitter.onCompletion(() -> {
			try {
				emitters.get(auctionId).remove(emitter);
				if (isVisitor) {
					redisTemplate.opsForSet().remove("auction-visitors:" + auctionId, userKey);
				} else {
					redisTemplate.opsForSet().remove("auction-subscribers:" + auctionId, userKey);
				}
			} catch (Exception e) {
			}
		});
		emitter.onTimeout(() -> {
			try {
				emitters.get(auctionId).remove(emitter);
				if (isVisitor) {
					redisTemplate.opsForSet().remove("auction-visitors:" + auctionId, userKey);
				} else {
					redisTemplate.opsForSet().remove("auction-subscribers:" + auctionId, userKey);
				}
			} catch (Exception e) {
			}
		});
		emitter.onError(e -> {
			try {
				emitters.get(auctionId).remove(emitter);
				if (isVisitor) {
					redisTemplate.opsForSet().remove("auction-visitors:" + auctionId, userKey);
				} else {
					redisTemplate.opsForSet().remove("auction-subscribers:" + auctionId, userKey);
				}
			} catch (Exception ex) {
			}
		});
		return emitter;
	}

	@Override
	public void removeVisitor(Integer auctionId, String userKey) {
		try {
			redisTemplate.opsForSet().remove("auction-visitors:" + auctionId, userKey);
			log.debug("[SSE] Visiteur " + userKey + " supprimé de l'enchère ID " + auctionId);
		} catch (Exception e) {
		}
	}

	@Override
	public void removeSubscriber(Integer auctionId, String userKey) {
		try {
			redisTemplate.opsForSet().remove("auction-subscribers:" + auctionId, userKey);
			log.debug("[SSE] Abonné " + userKey + " supprimé de l'enchère ID " + auctionId);
		} catch (Exception e) {
		}
	}

	@Override
	public Set<String> getVisitors(Integer auctionId) {
		try {
			Set<String> visitors = redisTemplate.opsForSet()
					.members("auction-visitors:" + auctionId);
			log.debug("[SSE] Visiteurs de l'enchère " + auctionId + ": " + visitors);
			return visitors;
		} catch (Exception e) {
			return Set.of();
		}
	}

	@Override
	public void sendEvent(Integer auctionId, String eventName, Object data) {
		var list = emitters.getOrDefault(auctionId, new CopyOnWriteArrayList<>());
		for (SseEmitter emitter : list) {
			try {
				emitter.send(
						SseEmitter.event().name(eventName).data(data, MediaType.APPLICATION_JSON));
			} catch (IOException e) {
				try {
					emitter.complete();
					list.remove(emitter);
				} catch (Exception ex) {
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public Set<String> getSubscribers(Integer auctionId) {
		try {
			Set<String> subs = redisTemplate.opsForSet()
					.members("auction-subscribers:" + auctionId);
			log.debug("[SSE] Abonnés à l'enchère " + auctionId + ": " + subs);
			return subs;
		} catch (Exception e) {
			return Set.of();
		}
	}

	@Override
	public void addSubscriber(Integer auctionId, String userKey) {
		try {
			redisTemplate.opsForSet().add("auction-subscribers:" + auctionId, userKey); // devrait
																						// être
																						// idempotent
			log.debug("[SSE] Abonné " + userKey + " ajouté à l'enchère ID " + auctionId);
		} catch (Exception e) {
		}
	}
}
