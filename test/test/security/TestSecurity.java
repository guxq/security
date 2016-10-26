package test.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.beetle.component.security.dto.SecPermissions;
import com.beetle.component.security.dto.SecRoles;
import com.beetle.component.security.dto.SecUsers;
import com.beetle.component.security.service.PermissionService;
import com.beetle.component.security.service.RoleService;
import com.beetle.component.security.service.UserService;
import com.beetle.framework.resource.dic.DIContainer;

public class TestSecurity {
	protected final PermissionService permissionService;
	protected final RoleService roleService;
	protected final UserService userService;

	protected String password = "888888";

	protected SecPermissions p1;
	protected SecPermissions p2;
	protected SecPermissions p3;
	protected SecRoles r1;
	protected SecRoles r2;
	protected SecUsers u1;
	protected SecUsers u2;
	protected SecUsers u3;
	protected SecUsers u4;

	public TestSecurity() {
		super();
		permissionService = DIContainer.getInstance().retrieve(PermissionService.class);
		roleService = DIContainer.getInstance().retrieve(RoleService.class);
		userService = DIContainer.getInstance().retrieve(UserService.class);
	}

	@Before
	public void setUp() throws Exception {
		TestHelper.exesql("delete from sec_users_roles");
		TestHelper.exesql("delete from sec_roles_permissions");
		TestHelper.exesql("delete from sec_users");
		TestHelper.exesql("delete from sec_roles");
		TestHelper.exesql("delete from sec_permissions");

		// 1、新增权限
		p1 = new SecPermissions("user:create:*", "用户模块新增", 1);
		p2 = new SecPermissions("user:update", "用户模块修改", 1);
		p3 = new SecPermissions("menu:create", "菜单模块新增", 1);
		permissionService.createPermission(p1);
		permissionService.createPermission(p2);
		permissionService.createPermission(p3);
		// 2、新增角色
		r1 = new SecRoles("admin", "管理员", 1);
		r2 = new SecRoles("all", "all users", 1);
		roleService.createRole(r1);
		roleService.createRole(r2);
		roleService.createRole(new SecRoles("Anonymous", "users who are not authenticated on the system", 1));
		roleService.createRole(new SecRoles("Registered", "users who are authenticated (logged in) on the system", 1));
		roleService.createRole(new SecRoles("administrators", "管理员组", 1));
		roleService.createRole(new SecRoles("project-manager", "项目管理员", 1));
		// 3、新增用户
		u1 = new SecUsers("Henry", password);
		u1.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
		u1.setLocked(0);
		u1.setTrycount(0);
		u2 = new SecUsers("Tom", password);
		u2.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
		u2.setLocked(0);
		u2.setTrycount(0);
		u3 = new SecUsers("Jack", password);
		u3.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
		u3.setLocked(0);
		u3.setTrycount(0);
		u4 = new SecUsers("Mark", password);
		u4.setLocked(1);
		u4.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
		u4.setTrycount(0);
		u1 = userService.createUser(u1);
		u2 = userService.createUser(u2);
		u3 = userService.createUser(u3);
		u4 = userService.createUser(u4);
		// 4、关联角色-权限
		roleService.correlationPermissions(r1.getRoleId(), p1.getPermissionId());
		roleService.correlationPermissions(r1.getRoleId(), p2.getPermissionId());
		roleService.correlationPermissions(r1.getRoleId(), p3.getPermissionId());
		roleService.correlationPermissions(r2.getRoleId(), p1.getPermissionId());
		roleService.correlationPermissions(r2.getRoleId(), p2.getPermissionId());

		// 5、关联用户-角色
		userService.correlationRoles(u1.getUserId(), r1.getRoleId());
	}

	@After
	public void tearDown() throws Exception {
		ThreadContext.unbindSubject();// 退出时请解除绑定Subject到线程 否则对下次测试造成影响
	}

	protected void login(String configFile, String username, String password) {
		// 1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
//		Ini config = new Ini();
//		config.setSectionProperty("main", "credentialsMatcher",
//				"com.beetle.component.security.credentials.RetryLimitHashedCredentialsMatcher");
//		config.setSectionProperty("main", "userRealm", "com.beetle.component.security.realm.UserRealm");
//		config.setSectionProperty("main", "userRealm.credentialsMatcher", "$credentialsMatcher");
//		config.setSectionProperty("main", "securityManager.realms", "$userRealm");
//		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(config);
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(configFile);
		
		// 2、得到SecurityManager实例 并绑定给SecurityUtils
		org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);

		// 3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);

		subject.login(token);

		// 对于Subject我们一般这么使用：
		// 1、身份验证（login）
		// 2、授权（hasRole*/isPermitted*或checkRole*/checkPermission*）
		// 3、将相应的数据存储到会话（Session）
		// 4、切换身份（RunAs）/多线程身份传播
		// 5、退出
	}

	public Subject subject() {
		return SecurityUtils.getSubject();
	}

	@Test
	public void testLoginSuccess() {
		login("classpath:shiro.ini", u1.getUsername(), password);
		
		Assert.assertTrue(subject().isAuthenticated());
	}

	@Test(expected = UnknownAccountException.class)
	public void testLoginFailWithUnknownUsername() {
		login("classpath:shiro.ini", u1.getUsername() + "1", password);
	}

	@Test(expected = IncorrectCredentialsException.class)
	public void testLoginFailWithErrorPassowrd() {
		login("classpath:shiro.ini", u1.getUsername(), password + "1");
	}

	@Test(expected = LockedAccountException.class)
	public void testLoginFailWithLocked() {
		login("classpath:shiro.ini", u4.getUsername(), password + "1");
	}

	@Test(expected = ExcessiveAttemptsException.class)
	public void testLoginFailWithLimitRetryCount() {
		for (int i = 1; i <= 5; i++) {
			try {
				login("classpath:shiro.ini", u3.getUsername(), password + "1");
			} catch (Exception e) {
				/* ignore */}
		}
		login("classpath:shiro.ini", u3.getUsername(), password + "1");

		// 需要清空缓存，否则后续的执行就会遇到问题(或者使用一个全新账户测试)
	}

	@Test
	public void testHasRole() {
		login("classpath:shiro.ini", u1.getUsername(), password);
		Subject sj = subject();
		boolean f = sj.hasRole("admin");
		// System.out.println(f);
		Assert.assertTrue(f);
	}

	@Test
	public void testNoRole() {
		login("classpath:shiro.ini", u2.getUsername(), password);
		Assert.assertFalse(subject().hasRole("admin"));
	}

	@Test
	public void testHasPermission() {
		login("classpath:shiro.ini", u1.getUsername(), password);
		Assert.assertTrue(subject().isPermittedAll("user:create:*", "menu:create"));
	}

	@Test
	public void testNoPermission() {
		login("classpath:shiro.ini", u2.getUsername(), password);
		Assert.assertFalse(subject().isPermitted("user:create"));
	}

}
