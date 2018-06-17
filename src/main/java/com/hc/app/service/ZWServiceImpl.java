package com.hc.app.service;

import com.hc.app.dao.ZWPileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ZWServiceImpl implements ZWService {
  @Autowired
  private ZWPileDao zwPileDao;

@Override
public String addHead(Map<String, String> map) {
	try {
		zwPileDao.addHead(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0xB0(Map<String, String> map) {
	try {
		zwPileDao.addBody0xB0(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0xA1(Map<String, String> map) {
	try {
		zwPileDao.addBody0xA1(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0x51(Map<String, String> map) {
	try {
		zwPileDao.addBody0x51(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
@Override
public String addBody0x54(Map<String, String> map) {
	try {
		zwPileDao.addBody0x54(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0x5A(Map<String, String> map) {
	try {
		zwPileDao.addBody0x5A(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0x11(Map<String, String> map) {
	try {
		zwPileDao.addBody0x11(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
@Override
public String addBody0x21(Map<String, String> map) {
	try {
		zwPileDao.addBody0x21(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0x20(Map<String, String> map) {
	try {
		zwPileDao.addBody0x20(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0x22(Map<String, String> map) {
	try {
		zwPileDao.addBody0x22(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0x23(Map<String, String> map) {
	try {
		zwPileDao.addBody0x23(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public String addBody0x13(Map<String, String> map) {
	try {
		zwPileDao.addBody0x13(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

@Override
public Map queryOrderIfExist(String charge_order_id) {
	Map m=null;
	try {
		 m= zwPileDao.queryOrderIfExist(charge_order_id);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return m;
}

@Override
public String addBody0x58(Map<String, String> map){
	try {
		zwPileDao.addBody0x58(map);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
}
