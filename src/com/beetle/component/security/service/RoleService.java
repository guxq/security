package com.beetle.component.security.service;

import java.util.List;

import com.beetle.component.security.dto.SecRoles;

public interface RoleService {
	SecRoles createRole(SecRoles role) throws SecurityServiceException;

	/**
	 * 根据所属标示找出此所属标示下所有的角色
	 * @param ownerId
	 * @return
	 * @throws SecurityServiceException
	 */
	List<SecRoles> findByOwnerId(String ownerId) throws SecurityServiceException;

	void deleteRole(Long roleId) throws SecurityServiceException;

	/**
	 * 添加角色-权限之间关系
	 * 
	 * @param roleId
	 * @param permissionIds
	 */
	void correlationPermissions(Long roleId, Long... permissionIds) throws SecurityServiceException;

	/**
	 * 移除角色-权限之间关系
	 * 
	 * @param roleId
	 * @param permissionIds
	 */
	void uncorrelationPermissions(Long roleId, Long... permissionIds) throws SecurityServiceException;
}
