package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

@Repository
public class ZwStopChargeDao extends BaseJdbcDao {
	/**
	 * 查询订单表中的充电枪口
	 * @param sn 订单号	
	 * @return 充电枪口
	 * @throws Exception
	 */
	public String getPile(String sn) throws Exception {
		String sql = "select o.charge_gun from HC_charge_order o where o.charge_order_id=? ";
		Object value = querySingleValue(sql, new Object[]{sn});
		return value.toString();
	}
	
	/**
	 *查询订单表中的充电桩 
	 * @param sn
	 * @return
	 * @throws Exception
	 */
	public String findPileNum(String sn) throws Exception {
		String sql = "select o.charge_pile_seri from HC_charge_order o where o.charge_order_id=?";
		Object value = querySingleValue(sql, new Object[]{sn});
		return value.toString();
	}
}
