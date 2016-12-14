package com.beetle.component.security.session;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.shiro.session.mgt.SimpleSession;
import org.slf4j.Logger;

import com.beetle.framework.log.AppLogger;

public class StateSession extends SimpleSession {

	@Override
	public void touch() {
		this.setUpdate(false);
		super.touch();
		logger.debug("touch:{}",this.update);
	}

	private static final long serialVersionUID = 1L;
	private boolean update;
	private static Logger logger = AppLogger.getLogger(StateSession.class);

	public StateSession() {
		super();
		this.setUpdate(false);
	}

	public StateSession(String host) {
		super(host);
		this.setUpdate(false);
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	@Override
	public void setId(Serializable id) {
		this.setUpdate(true);
		super.setId(id);
		logger.debug("setId:{}",this.update);
	}

	@Override
	public void setStartTimestamp(Date startTimestamp) {
		this.setUpdate(false);
		super.setStartTimestamp(startTimestamp);
		logger.debug("setStartTimestamp:{}", this.update);
	}

	@Override
	public void setStopTimestamp(Date stopTimestamp) {
		this.setUpdate(false);
		super.setStopTimestamp(stopTimestamp);
		logger.debug("setStopTimestamp:{}", this.update);
	}

	@Override
	public void setLastAccessTime(Date lastAccessTime) {
		this.setUpdate(false);
		super.setLastAccessTime(lastAccessTime);
		logger.debug("setLastAccessTime:{}", this.update);
	}

	@Override
	public void setExpired(boolean expired) {
		this.setUpdate(true);
		super.setExpired(expired);
		logger.debug("setExpired:{}", this.update);
	}

	@Override
	public void setTimeout(long timeout) {
		this.setUpdate(true);
		super.setTimeout(timeout);
		logger.debug("setTimeout:{}", this.update);
	}

	@Override
	public void setHost(String host) {
		this.setUpdate(true);
		super.setHost(host);
		logger.debug("setHost:{}", this.update);
	}

	@Override
	public void setAttributes(Map<Object, Object> attributes) {
		this.setUpdate(true);
		super.setAttributes(attributes);
		logger.debug("setAttributes:{}", this.update);
	}

	@Override
	public void setAttribute(Object key, Object value) {
		this.setUpdate(true);
		super.setAttribute(key, value);
		logger.debug("setAttribute:{}", this.update);
	}

	@Override
	public Object removeAttribute(Object key) {
		this.setUpdate(true);
		logger.debug("removeAttribute:{}", this.update);
		return super.removeAttribute(key);
	}

}
