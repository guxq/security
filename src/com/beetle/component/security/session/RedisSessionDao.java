package com.beetle.component.security.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.nosql.redis.RedisOperator;
import com.beetle.framework.persistence.nosql.redis.pubsub.PubSubManager;

public class RedisSessionDao extends AbstractSessionDAO {
	private String ds;
	private int expire;
	private static final Logger logger = AppLogger.getLogger(RedisSessionDao.class);
	private static PubSubManager psm;
	static final String REDIS_SESSION_UPDATE_TOPIC = "REDIS_SESSION_UPDATE_TOPIC";

	public RedisSessionDao() {
		super();
		this.ds = AppProperties.get("security_session_redis_datasource");
		if (ds == null || ds.trim().length() == 0) {
			String msg = "redis datasourece not setted![security_session_redis_datasource]";
			logger.error(msg);
			throw new AppRuntimeException(msg);
		}
		this.expire = AppProperties.getAsInt("security_session_redis_expire", 1830);
		if (psm == null) {
			psm = new PubSubManager(new RedisOperator(ds),
					"com.beetle.component.security.session.RedisSessionListener");
			logger.debug("PubSubManager inited!");
			psm.subscribe(REDIS_SESSION_UPDATE_TOPIC);
			logger.debug("sbuscribe:{} OK!", REDIS_SESSION_UPDATE_TOPIC);
		}
	}

	public static PubSubManager getPubSubManager() {
		return psm;
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		StateSession ss = (StateSession) session;
		if (ss.isUpdate()) {
			RedisOperator ro = new RedisOperator(ds);
			boolean f = ro.put(session.getId().toString(), session, expire);
			if (logger.isDebugEnabled()) {
				logger.debug("sessionid:{},lastTime:{},update[{}]", session.getId(), session.getLastAccessTime(), f);
			}
			// 通知更新
			psm.publish(REDIS_SESSION_UPDATE_TOPIC, session.getId().toString());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("update flag:{}", ss.isUpdate());
		}
	}

	@Override
	public void delete(Session session) {
		RedisOperator ro = new RedisOperator(ds);
		boolean f = ro.del(session.getId().toString());
		if (logger.isDebugEnabled()) {
			logger.debug("sessionid:{},del[{}]", session.getId(), f);
		}
		// 通知更新
		psm.publish(REDIS_SESSION_UPDATE_TOPIC, session.getId().toString());
	}

	@Override
	public Collection<Session> getActiveSessions() {
		return Collections.emptySet();
	}

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);
		RedisOperator ro = new RedisOperator(ds);
		boolean f = ro.put(sessionId.toString(), session, expire);
		if (logger.isDebugEnabled()) {
			logger.debug("sessionid:{},created[{}].", sessionId, f);
		}
		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		Session session = null;
		RedisOperator ro = new RedisOperator(ds);
		// Object o = ro.getAsDTO(sessionId.toString());
		// if (o != null) {
		// session = (Session) o;
		// }
		session = ro.getWithCache(sessionId.toString(), Session.class, expire / 2);
		if (logger.isDebugEnabled()) {
			logger.debug("session:{},read", session);
		}
		return session;
	}

}
