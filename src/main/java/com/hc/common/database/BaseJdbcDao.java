package com.hc.common.database;

import com.hc.common.utils.LogUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.JdbcUtils;
//import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * BaseDao extends JdbcDaoSupport
 * 
 * @author lizz
 * 
 */

public class BaseJdbcDao extends JdbcDaoSupport {

	private Map updateMap;
	private List batchUpdateList;
	private String[] keyArray;
	private List batchQueryList;
	private JdbcTemplate jdbcTemplate;

	public BaseJdbcDao() {
	}

	/**
	 * 记录操作系统记录:create,update,delete
	 * 
	 * @param parameters
	 *            operate_user,operate_datetime,operate_action,operate_module,
	 *            operate_table,operate_desc
	 * @throws Exception
	 */
	public void operateSysLog(Object[] parameters) throws Exception {

		String sql = "insert into cc_sys_logs(operate_user,operate_datetime,operate_action,operate_module,operate_table,operate_desc) values(?,?,?,?,?,?)";

		getJdbcTemplate().update(sql, parameters);
	}
	
	/**
	 * 查询oracle Sequence
	 * 
	 * @param seqName
	 * @return
	 * @throws Exception
	 */
	public String querySequence(String seqName) throws Exception {
		
		String sql = "select seq_"+seqName+".NEXTVAL from dual ";
		
		String seq = "";
		BigDecimal result = null;
		Map dataMap = this.querySingleRow(sql, null);
		if(null != dataMap){
			result = (BigDecimal)dataMap.get("NEXTVAL");
			if (null != result) {
				seq = String.valueOf(result.longValue()); 
			}
		}
		//
//		StringBuffer sb = new StringBuffer();
//		for(int i=seq.length();i<8;i++){
//			sb.append("0");
//		}
//		seq = sb.append(seq).toString();
//		seq = TimeUtils.getCurrentTime("yyyyMMdd")+seq;//yyyyMMdd+8位序列号
		
		return seq;
	}

	/**
	 * 获取8位客户号 --- Add by Zed 20150612
	 * @param seqName
	 * @return
	 * @throws Exception
	 */
   public String querySequenceForCustNo(String seqName) throws Exception {
		
		String sql = "select seq_"+seqName+".NEXTVAL from dual ";
		
		String seq = "";
		BigDecimal result = null;
		Map dataMap = this.querySingleRow(sql, null);
		if(null != dataMap){
			result = (BigDecimal)dataMap.get("NEXTVAL");
			if (null != result) {
				seq = String.valueOf(result.longValue()); 
			}
		}

		return seq;
	}


	/**
	 * add a record
	 * 
	 * @param tableName
	 * @param filedValueMap
	 * @return
	 */
	public void add(String tableName, Map filedValueMap) throws Exception {
		Set keySet = filedValueMap.keySet();

		// fields
		StringBuffer fieldsBuffer = new StringBuffer();
		// values
		StringBuffer valuesBuffer = new StringBuffer();

		for (Iterator it = keySet.iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			Object value = filedValueMap.get(fieldName);
			if (value != null) {
				fieldsBuffer.append(fieldName + ",");
				valuesBuffer.append("?,");
			}
		}
		String fields = fieldsBuffer.substring(0, fieldsBuffer.length() - 1);
		String values = valuesBuffer.substring(0, valuesBuffer.length() - 1);

		StringBuffer mySQL = new StringBuffer("INSERT INTO " + tableName + " ("
				+ fields + ") VALUES (" + values + ")");

		String sql = mySQL.toString();
		updateMap = filedValueMap;
		PreparedStatementSetter setter = null;
		setter = new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				Map columnMap = updateMap;
				Set keySet = columnMap.keySet();
				int i = 1;
				for (Iterator it = keySet.iterator(); it.hasNext();) {
					String fieldName = (String) it.next();
					Object value = columnMap.get(fieldName);
					if (value != null) {
						if (value instanceof java.sql.Timestamp) {
							ps.setTimestamp(i, new java.sql.Timestamp(
									((Date) value).getTime()));
							i++;
						} else if (value instanceof Date) {
							ps.setDate(i, new java.sql.Date(
									((Date) value).getTime()));
							i++;
						} else if (value instanceof BigDecimal) {
							ps.setDouble(i, ((BigDecimal) value).doubleValue());
							i++;
						} else if (value instanceof Long) {
							ps.setInt(i, ((Long) value).intValue());
							i++;
						} else if (value instanceof Integer) {
							ps.setInt(i, ((Integer) value).intValue());
							i++;
						} else {
							String valueStr = value.toString();
							try {
								valueStr = new String(valueStr.getBytes(),
										"UTF-8");
							} catch (Exception e) {
								LogUtils.printStackTrace(e);
							}
							ps.setString(i, value.toString());
							i++;
						}
					}

				}
			}
		};

		getJdbcTemplate().update(sql, setter);
	}

	/**
	 * batch insert records
	 *
	 * @param tableName
	 * @param recordList
	 * @throws Exception
	 */
	public void batchAdd(String tableName, List recordList) throws Exception {

		Map columnMap = new HashMap();
		if (recordList == null || recordList.size() == 0) {
			return;
		} else {
			columnMap = (Map) recordList.get(0);
		}
		Set keySet = columnMap.keySet();

		StringBuffer fieldsBuffer = new StringBuffer();
		StringBuffer valuesBuffer = new StringBuffer();
		for (Iterator it = keySet.iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			Object value = columnMap.get(fieldName);
			if (value != null) {
				fieldsBuffer.append(fieldName + ",");
				valuesBuffer.append("?,");
			}
		}
		String fields = fieldsBuffer.substring(0, fieldsBuffer.length() - 1);
		String values = valuesBuffer.substring(0, valuesBuffer.length() - 1);

		StringBuffer mySQL = new StringBuffer("INSERT INTO " + tableName + " ("
				+ fields + ") VALUES (" + values + ")");

		String sql = mySQL.toString();
		batchUpdateList = recordList;
		BatchPreparedStatementSetter setter = null;
		setter = new BatchPreparedStatementSetter() {
			public int getBatchSize() {
				return batchUpdateList.size();
			}

			public void setValues(PreparedStatement ps, int index)
					throws SQLException {
				Map columnMap = (Map) batchUpdateList.get(index);
				Set keySet = columnMap.keySet();
				int i = 1;
				for (Iterator it = keySet.iterator(); it.hasNext();) {
					String fieldName = (String) it.next();
					Object value = columnMap.get(fieldName);
					if (value != null) {
						if (value instanceof Date) {
							ps.setDate(i, new java.sql.Date(
									((Date) value).getTime()));
							i++;
						} else if (value instanceof BigDecimal) {
							ps.setDouble(i, ((BigDecimal) value).doubleValue());
							i++;
						} else if (value instanceof Long) {
							ps.setInt(i, ((Long) value).intValue());
							i++;
						} else if (value instanceof Integer) {
							ps.setInt(i, ((Integer) value).intValue());
							i++;
						} else {
							String valueStr = value.toString();
							try {
								valueStr = new String(valueStr.getBytes(),
										"UTF-8").trim();
							} catch (Exception e) {
								LogUtils.printStackTrace(e);
							}
							ps.setString(i, value.toString());
							i++;
						}
					}

				}
			}
		};

		getJdbcTemplate().batchUpdate(sql, setter);
	}

	/**
	 * batch update records
	 *
	 * @param tableName
	 * @param recordList
	 * @throws Exception
	 */
	public void batchUpdate(String sql, List recordList) throws Exception {

		batchUpdateList = recordList;
		BatchPreparedStatementSetter setter = null;
		setter = new BatchPreparedStatementSetter() {
			public int getBatchSize() {
				return batchUpdateList.size();
			}

			public void setValues(PreparedStatement ps, int index)
					throws SQLException {
				Object[] columnArray = (Object[]) batchUpdateList.get(index);
				for (int i = 0; i < columnArray.length; i++) {
					Object value = columnArray[i];
					if (value != null) {
						if (value instanceof Date) {
							ps.setDate(i + 1, new java.sql.Date(
									((Date) value).getTime()));
						} else if (value instanceof BigDecimal) {
							ps.setDouble(i + 1,
									((BigDecimal) value).doubleValue());
						} else if (value instanceof Long) {
							ps.setInt(i + 1, ((Long) value).intValue());
						} else if (value instanceof Integer) {
							ps.setInt(i + 1, ((Integer) value).intValue());
						} else {
							String valueStr = value.toString();
							try {
								valueStr = new String(valueStr.getBytes(),
										"UTF-8").trim();
							} catch (Exception e) {
								LogUtils.printStackTrace(e);
							}
							ps.setString(i + 1, value.toString());
						}
					}

				}
			}
		};

		getJdbcTemplate().batchUpdate(sql, setter);
	}

	/**
	 * batch query
	 *
	 * @param tableName
	 * @param recordList
	 * @throws Exception
	 */
	public List batchQuery(String sql, List recordList, String[] recordKeys)
			throws Exception {

		batchQueryList = recordList;
		keyArray = recordKeys;

		BatchPreparedStatementSetter setter = null;
		setter = new BatchPreparedStatementSetter() {
			public int getBatchSize() {
				return batchQueryList.size();
			}

			public void setValues(PreparedStatement ps, int index)
					throws SQLException {
				Map columnMap = (Map) batchQueryList.get(index);
				for (int i = 0; i < keyArray.length; i++) {
					String fieldName = keyArray[i];
					Object value = columnMap.get(fieldName);
					if (value != null) {
						if (value instanceof Date) {
							ps.setDate(i + 1, new java.sql.Date(
									((Date) value).getTime()));
						} else if (value instanceof BigDecimal) {
							ps.setDouble(i + 1,
									((BigDecimal) value).doubleValue());
						} else if (value instanceof Long) {
							ps.setInt(i + 1, ((Long) value).intValue());
						} else if (value instanceof Integer) {
							ps.setInt(i + 1, ((Integer) value).intValue());
						} else {
							String valueStr = value.toString();
							try {
								valueStr = new String(valueStr.getBytes(),
										"UTF-8");
							} catch (Exception e) {
								LogUtils.printStackTrace(e);
							}
							ps.setString(i + 1, value.toString());
						}
					}

				}
			}
		};

		ResultSetExtractor extractor = null;
		extractor = new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
				if (rs.next()) {
					HashMap res = new HashMap();
					ResultSetMetaData metaData = rs.getMetaData();
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						String name = metaData.getColumnName(i);
						res.put(name, rs.getObject(i));
					}
					return res;
				}
				return null;
			}
		};

		Object[] retArray = batchQuery(sql, setter, extractor);
		List retList = new ArrayList();
		for (int i = 0; i < retArray.length; i++) {
			retList.add(retArray[i]);
		}
		return retList;
	}

	/**
	 * batch query
	 *
	 * @param sql
	 * @param pss
	 * @param rse
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public Object[] batchQuery(String sql,
                               final BatchPreparedStatementSetter pss, final ResultSetExtractor rse)
			throws DataAccessException {

		/*final Object[] returnValues = new Object[pss.getBatchSize()];

		jdbcTemplate = this.getJdbcTemplate();
		jdbcTemplate.execute(sql, new PreparedStatementCallback() {
			public Object doInPreparedStatement(PreparedStatement ps)
					throws SQLException {
				ResultSet rs = null;
				try {
					int batchSize = pss.getBatchSize();
					for (int i = 0; i < batchSize; i++) {
						if (pss != null) {
							pss.setValues(ps, i);
						}
						rs = ps.executeQuery();
						ResultSet rsToUse = rs;
						NativeJdbcExtractor ne = jdbcTemplate.getNativeJdbcExtractor();
						if (ne != null) {
							rsToUse = ne.getNativeResultSet(rs);
						}
						returnValues[i] = rse.extractData(rsToUse);
						JdbcUtils.closeResultSet(rs);
						if (pss instanceof ParameterDisposer) {
							((ParameterDisposer) pss).cleanupParameters();
						}
					}
					return returnValues;
				} finally {
					JdbcUtils.closeResultSet(rs);
					if (pss instanceof ParameterDisposer) {
						((ParameterDisposer) pss).cleanupParameters();
					}
				}
			}
		});*/
		return null;
	}

	/*
	 * public Object[] batchQuery(String sql, final BatchPreparedStatementSetter
	 * pss, final RowMapper rm) throws DataAccessException { return
	 * batchQuery(sql,pss,new RowMapperResultSetExtractor(rm)); }
	 */

	/**
	 * update a record
	 *
	 * @param tableName
	 * @param columnMap
	 * @param conditionMap
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void update(String tableName, Map columnMap, Map conditionMap)
			throws Exception {

		Set keySet = columnMap.keySet();
		Set keySet2 = conditionMap.keySet();
		StringBuffer fieldsBuffer = new StringBuffer();
		StringBuffer keyWordsBuffer = new StringBuffer();
		ArrayList valueList = new ArrayList();

		for (Iterator it = keySet.iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			fieldsBuffer.append(fieldName + "=?");
			if (it.hasNext()) {
				fieldsBuffer.append(",");
			}
			Object value = columnMap.get(fieldName);
			if (value instanceof java.sql.Timestamp) {
				valueList.add(new java.sql.Timestamp(((Date) value)
						.getTime()));
			} else if (value instanceof Date) {
				valueList.add(new java.sql.Date(((Date) value)
						.getTime()));
			} else {
				valueList.add(value.toString());
			}
		}
		String fields = fieldsBuffer.toString();

		for (Iterator it = keySet2.iterator(); it.hasNext();) {
			String keyWord = (String) it.next();
			keyWordsBuffer.append(" and " + keyWord + "=?");
			Object value = conditionMap.get(keyWord);
			if (value instanceof java.sql.Timestamp) {
				valueList.add(new java.sql.Timestamp(((Date) value)
						.getTime()));
			} else if (value instanceof Date) {
				valueList.add(new java.sql.Date(((Date) value)
						.getTime()));
			} else {
				valueList.add(value.toString());
			}
		}
		String keyWords = keyWordsBuffer.toString();
		Object[] valueObjArray = valueList.toArray();

		StringBuffer mySQL = new StringBuffer("UPDATE " + tableName + " SET "
				+ fields + " WHERE 0=0 " + keyWords);
		getJdbcTemplate().update(mySQL.toString(), valueObjArray);
	}

	/**
	 * update a record
	 *
	 * @param sql
	 * @param parameters
	 * @throws Exception
	 */
	public void update(String sql, Object[] parameters) throws Exception {

		getJdbcTemplate().update(sql, parameters);
	}

	/**
	 * query records
	 *
	 * @param tableName
	 * @param columnList
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List query(String tableName, List columnList, Map conditionMap)
			throws Exception {
		StringBuffer fieldsBuffer = new StringBuffer();

		for (int i = 0; i < columnList.size(); i++) {
			if (i > 0) {
				fieldsBuffer.append(",");
			}
			fieldsBuffer.append(columnList.get(i));
		}
		String fields = fieldsBuffer.toString();

		Set conditionKeySet = conditionMap.keySet();
		StringBuffer keyWordsBuffer = new StringBuffer();
		ArrayList valueList = new ArrayList();
		for (Iterator it = conditionKeySet.iterator(); it.hasNext();) {
			String keyWord = (String) it.next();

			keyWordsBuffer.append(keyWord).append("=?");
			if (it.hasNext()) {
				keyWordsBuffer.append(" and ");
			}
			Object value = conditionMap.get(keyWord);
			if (value instanceof Date) {
				valueList.add(new java.sql.Date(((Date) value)
						.getTime()));
			} else {
				valueList.add(value.toString());
			}
		}
		String keyWords = keyWordsBuffer.toString();
		Object[] valueObjArray = valueList.toArray();

		String mySQL = "SELECT  " + fields + " FROM " + tableName + " WHERE "
				+ keyWords;

		return getJdbcTemplate().query(mySQL, valueObjArray,
				new ResultRowMapper());
	}

	/**
	 * query records
	 *
	 * @param sql
	 * @param valueObjArray
	 * @return
	 * @throws Exception
	 */
	public List query(String sql, Object[] valueObjArray) throws Exception {
		List list = getJdbcTemplate().query(sql, valueObjArray,
				new ResultRowMapper());
		return list;
	}

	/**
	 * query records
	 *
	 * @param tableName
	 * @param columnList
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public Map querySingleRow(String tableName, List columnList,
			Map conditionMap) throws Exception {
		List list = query(tableName, columnList, conditionMap);
		if (list.size() > 0) {
			return (Map) list.get(0);
		}
		return null;
	}

	/**
	 * query records
	 *
	 * @param sql
	 * @param valueObjArray
	 * @return
	 * @throws Exception
	 */
	public Map querySingleRow(String sql, Object[] valueObjArray)
			throws Exception {
		List list = getJdbcTemplate().query(sql, valueObjArray,
				new ResultRowMapper());
		if (list.size() > 0) {
			return (Map) list.get(0);
		}
		return null;
	}

	/**
	 * query records
	 *
	 * @param tableName
	 * @param fieldName
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	public Map querySingleValue(String tableName, String fieldName,
			Map conditionMap) throws Exception {
		Set conditionKeySet = conditionMap.keySet();
		StringBuffer keyWordsBuffer = new StringBuffer();
		ArrayList valueList = new ArrayList();
		for (Iterator it = conditionKeySet.iterator(); it.hasNext();) {
			String keyWord = (String) it.next();
			keyWordsBuffer.append(keyWord);
			if (it.hasNext()) {
				keyWordsBuffer.append("=? and ");
			}
			Object value = conditionMap.get(keyWord);
			if (value instanceof Date) {
				valueList.add(new java.sql.Date(((Date) value)
						.getTime()));
			} else {
				valueList.add(value.toString());
			}
		}
		String keyWords = keyWordsBuffer.toString();
		Object[] valueObjArray = valueList.toArray();

		String sql = "SELECT  " + fieldName + " FROM " + tableName + " WHERE "
				+ keyWords;

		List list = getJdbcTemplate().query(sql, valueObjArray,
				new ResultRowMapper());
		if (list.size() > 0) {
			return (Map) list.get(0);
		}
		return null;
	}

	/**
	 * query records
	 * 
	 * @param sql
	 * @param valueObjArray
	 * @return
	 * @throws Exception
	 */
	public Object querySingleValue(String sql, Object[] valueObjArray)
			throws Exception {
		List list = getJdbcTemplate().query(sql, valueObjArray,
				new ResultSingleValueMapper());
		if (list.size() > 0) {
			return (Object) list.get(0);
		}
		return null;
	}

	/**
	 * delete records
	 * 
	 * @param tableName
	 * @throws Exception
	 */
	public void removeAll(String tableName) throws Exception {
		this.getJdbcTemplate().execute("delete from " + tableName);
	}

	@Resource
	public void setJb(JdbcTemplate jb) {
		super.setJdbcTemplate(jb);
	}
}
