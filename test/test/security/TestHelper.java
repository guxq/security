package test.security;

import com.beetle.component.security.persistence.imp.Helper;
import com.beetle.framework.persistence.access.operator.UpdateOperator;

public class TestHelper {
	public static void exesql(String sql) {
		UpdateOperator u = new UpdateOperator();
		u.setDataSourceName(Helper.DATASOURCE);
		u.setSql(sql);
		u.access();
		
		
	}
}
