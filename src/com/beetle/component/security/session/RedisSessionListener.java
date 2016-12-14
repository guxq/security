package com.beetle.component.security.session;

import org.slf4j.Logger;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.nosql.redis.pubsub.PubSubManager;
import com.beetle.framework.persistence.nosql.redis.pubsub.SubscribeListener;

public class RedisSessionListener implements SubscribeListener {
	private static final Logger logger = AppLogger.getLogger(RedisSessionListener.class);

	@Override
	public void onMessage(String topic, String msg) {
		if (topic.equals(RedisSessionDao.REDIS_SESSION_UPDATE_TOPIC)) {
			String sid = msg;
			PubSubManager psm = RedisSessionDao.getPubSubManager();
			psm.getRedisOperator().removeLocalCache(sid);
			if (logger.isDebugEnabled())
				logger.debug("remove cache:{}", sid);
		}
	}

	@Override
	public void onSubscribe(String topic, int subscribedChannels) {
		logger.info("Subscribe:{}", topic);
	}

	@Override
	public void onUnsubscribe(String topic, int subscribedChannels) {
		logger.info("Unsubscribe:{}", topic);
	}

}
