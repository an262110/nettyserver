package com.hc.app.service;

import com.hc.app.dao.KlGunOrderStatusDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/***
 * 对枪的状态和订单状态进行更改
 */
@Service
public class KlGunOrderStatusServiceImpl implements KlGunOrderStatusService {

    @Autowired
    private KlGunOrderStatusDao klGunOrderStatusDao;

    @Override
    public void update(String xlh, String gunNo,String gun_status_hex) throws Exception {
        if(gunNo.length() == 1){
            gunNo = "0" + gunNo;
        }
        String showStatus = "";
        if("01".equals(gun_status_hex)){
            showStatus = "03";
        }else if("02".equals(gun_status_hex)){
            showStatus = "04";
        }else if("03".equals(gun_status_hex)){
            showStatus = "01";
        }else if("09".equals(gun_status_hex)){
            showStatus = "06";
        }
        if("".equals(showStatus)){
            String sql ="update hc_charge_gun set STATUS = ? where PILE_SERI = ? and GUN_NO = ?";
            Object[] objects = {gun_status_hex, xlh, gunNo};
            klGunOrderStatusDao.update(sql,objects);
        }else{
            String sql ="update hc_charge_gun set STATUS = ?, SHOW_STATUS = ? where PILE_SERI = ? and GUN_NO = ?";
            Object[] objects = {gun_status_hex, showStatus, xlh, gunNo};
            klGunOrderStatusDao.update(sql,objects);
        }
    }

    @Override
    public void peachUpdate(String query_address, String gunNo, String status) throws Exception {

        if(gunNo.length() == 1){
            gunNo = "0" + gunNo;
        }
        String sql = "select t.CHARGE_ORDER_ID as orderId from hc_charge_order t where t.CHARGE_PILE_SERI = ? and t.CHARGE_GUN = ? order by t.CREATE_TIME DESC limit 1 ";
        Object[] objects = {query_address, gunNo};
        String orderId = klGunOrderStatusDao.queryinfo(sql, objects);
        String statuSql = " update hc_charge_order set ORDER_STATE = ?, START_CHARGE_TIME = CURRENT_TIMESTAMP  where CHARGE_ORDER_ID = ? ";
        Object[] obj = {status, orderId};
        klGunOrderStatusDao.update(statuSql,obj);
        update(query_address,gunNo,status);

    }
}
