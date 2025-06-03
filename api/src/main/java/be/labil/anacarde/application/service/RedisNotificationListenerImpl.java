package be.labil.anacarde.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.redis.notifications", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisNotificationListenerImpl implements RedisNotificationListener {
	private static final Logger log = LoggerFactory.getLogger(RedisNotificationListenerImpl.class);
	private final NotificationSseServiceImpl notificationSseService;
	private final RedisMessageListenerContainer listenerContainer;
	private final ObjectMapper objectMapper;

	@Autowired
	public RedisNotificationListenerImpl(NotificationSseServiceImpl notificationSseService,
			RedisMessageListenerContainer listenerContainer, ObjectMapper objectMapper) {
		this.notificationSseService = notificationSseService;
		this.listenerContainer = listenerContainer;
		this.objectMapper = objectMapper;
	}

	@PostConstruct
	@Override
	public void subscribe() {
		log.debug("[SSE] Listener Redis démarré sur notifications:user:*");
		listenerContainer.addMessageListener(new MessageListenerAdapter(new MessageListener() {
			@Override
			public void onMessage(Message message, byte[] pattern) {
				try {
					String channel = new String(message.getChannel());
					if (channel.startsWith("notifications:user:")) {
						String userKey = channel.substring("notifications:user:".length());
						String body = new String(message.getBody());
						RedisNotificationEvent event = objectMapper.readValue(body,
								RedisNotificationEvent.class);
						notificationSseService.sendEvent(userKey, event.getEventName(),
								event.getData());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}), new PatternTopic("notifications:user:*"));
		// Ne pas utiliser ChannelTopic car écoute littéralement sur "notifications:user:*"
		listenerContainer.start();
	}

}
