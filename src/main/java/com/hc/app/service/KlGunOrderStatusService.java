package com.hc.app.service;

public interface KlGunOrderStatusService {

    public void update(String xlh, String gunNo, String gun_status_hex) throws Exception;

    void peachUpdate(String query_address, String flag, String status) throws Exception;
}
