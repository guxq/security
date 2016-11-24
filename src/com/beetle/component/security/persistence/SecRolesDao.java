package com.beetle.component.security.persistence;

import java.util.List;

import com.beetle.component.security.dto.SecRoles;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface SecRolesDao {
	SecRoles get(Long roleid) throws DBOperatorException;

	/**
	 * 通过ownerId，获取其所有的角色记录
	 * @param ownerId
	 * @param ownerType
	 * @return
	 * @throws DBOperatorException
	 */
	List<SecRoles> get(String ownerId,int ownerType) throws DBOperatorException;

	List<SecRoles> getAll() throws DBOperatorException;

	SecRoles insert(SecRoles secRoles) throws DBOperatorException;

	int update(SecRoles secRoles) throws DBOperatorException;

	int delete(Long roleid) throws DBOperatorException;

}
