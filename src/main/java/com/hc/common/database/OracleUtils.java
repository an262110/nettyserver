package com.hc.common.database;

/**
 * Oracle数据库操作辅助类
 * 
 * @author Zed
 *
 */
public class OracleUtils {
	
	
	/**
	 * 设置分页查询语句
	 * 
	 * @param querySql
	 * @param startPageIndex
	 * @param endPageIndex
	 * @return
	 * @throws Exception
	 */
	public static String getPageQuerySql(String querySql,int startPageIndex,int endPageIndex) throws Exception {
		
		String sql = "SELECT * FROM ( "
				+ "SELECT tt.*, ROWNUM RN FROM ( "+querySql+" ) tt WHERE ROWNUM <= "+endPageIndex+" ) "
				+ "WHERE RN > "+startPageIndex;
		
		return sql;
	}

}
