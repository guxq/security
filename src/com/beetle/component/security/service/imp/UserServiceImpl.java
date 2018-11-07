package com.beetle.component.security.service.imp;

import java.util.Set;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.component.security.dto.SecUsersRoles;
import com.beetle.component.security.persistence.SecUsersDao;
import com.beetle.component.security.persistence.SecUsersRolesDao;
import com.beetle.component.security.service.SecurityServiceException;
import com.beetle.component.security.service.UserService;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.define.PageList;
import com.beetle.framework.resource.dic.def.DaoField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;

public class UserServiceImpl implements UserService {
	@DaoField
	private SecUsersDao userDao;
	@DaoField
	private SecUsersRolesDao userRoleDao;
	private final Helper helper;

	public UserServiceImpl() {
		super();
		this.helper = new Helper();
	}

	@Override
	public SecUsers createUser(SecUsers user) throws SecurityServiceException {
		try {
			helper.encryptPassword(user);
			int i = userDao.insert(user);
			if (i <= 0) {
				throw new SecurityServiceException(-2001, "create user err");
			}
			return userDao.getByName(user.getUsername());
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public void changePassword(Long userId, String newPassword) throws SecurityServiceException {
		try {
			SecUsers user = userDao.get(userId);
			user.setPassword(newPassword);
			helper.encryptPassword(user);
			userDao.update(user);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	@ServiceTransaction
	public void correlationRoles(Long userId, Long... roleIds) throws SecurityServiceException {
		try {
			for (Long roleId : roleIds) {
				SecUsersRoles ur = new SecUsersRoles();
				ur.setRoleId(roleId);
				ur.setUserId(userId);
				if (!userRoleDao.exists(ur)) {
					userRoleDao.insert(ur);
				}
			}
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	@ServiceTransaction
	public void uncorrelationRoles(Long userId, Long... roleIds) throws SecurityServiceException {
		try {
			for (Long roleId : roleIds) {
				SecUsersRoles ur = new SecUsersRoles();
				ur.setRoleId(roleId);
				ur.setUserId(userId);
				userRoleDao.delete(ur);
			}
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public SecUsers findByUsername(String username) throws SecurityServiceException {
		return userDao.getByName(username);
	}

	@Override
	public Set<String> findRoles(String username) throws SecurityServiceException {
		try {
			return userDao.findRoles(username);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public Set<String> findPermissions(String username) throws SecurityServiceException {
		try {
			return userDao.findPermissions(username);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public int updateTryTime(long userid, int time) throws SecurityServiceException {
		try {
			return userDao.updateTryTime(userid, time);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public void changePassword(Long userId, String oldPassowrd, String newPassword) throws SecurityServiceException {
		try {
			SecUsers user = userDao.get(userId);
			String oldEncPassword = user.getPassword();
			user.setPassword(oldPassowrd);
			helper.encryptPasswordForOld(user);
			if (user.getPassword().equals(oldEncPassword)) {
				user.setPassword(newPassword);
				helper.encryptPassword(user);
				userDao.update(user);
			} else {
				throw new SecurityServiceException(-1002, "The old password is not correct！");
			}
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public int lockUser(long userid) throws SecurityServiceException {
		try {
			return userDao.updateLock(userid, 1);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public int unlockUser(long userid) throws SecurityServiceException {
		try {
			return userDao.updateLock(userid, 0);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public PageList<SecUsers> compositeQuery(Long userid, String username, Integer lock, int pageNumber, int pageSize)
			throws SecurityServiceException {
		try {
			return userDao.compositeQuery(userid, username, lock, pageNumber, pageSize);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public boolean verifyOldPassowrd(Long userId, String oldPassowrd) throws SecurityServiceException {
		try {
			SecUsers user = userDao.get(userId);
			String oldEncPassword = user.getPassword();
			user.setPassword(oldPassowrd);
			helper.encryptPasswordForOld(user);
			if (user.getPassword().equals(oldEncPassword)) {
				return true;
			} else {
				return false;
			}
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

}
