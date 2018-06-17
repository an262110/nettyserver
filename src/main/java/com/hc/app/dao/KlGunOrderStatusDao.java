package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/***
 * 对枪的状态和订单状态进行更改
 */
@Repository
public class KlGunOrderStatusDao extends BaseJdbcDao {


    public String queryinfo(String sql, Object[] objects) {
        List<Map<String, Object>> maps = super.getJdbcTemplate().queryForList(sql, objects);
        String orderId = maps.get(0).get("orderId").toString();
        return orderId;
    }
}
