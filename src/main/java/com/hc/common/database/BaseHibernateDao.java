package com.hc.common.database;

import com.hc.common.utils.LogUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * BaseDao extends HibernateDaoSupport
 * 
 * @author lizz
 *
 */
public class BaseHibernateDao extends HibernateDaoSupport {

    /**
     * add a new object
     * 
     * @param myPOJO
     */
    public void add(Object myPOJO) {
        getHibernateTemplate().save(myPOJO);
    }

    /**
     * load an Object 
     * 
     * @param myPOJO  
     * @param primaryKey 
     * @return
     */
    public Object load(Class myPOJO, Serializable primaryKey) {
        try {
            Object thePOJO = getHibernateTemplate().load(myPOJO, primaryKey);
            return thePOJO;
        } catch (Exception e) {
        	LogUtils.printStackTrace(e);
            return null;
        }
    }

    /**
     * load an object List 
     *
     * @param myPOJO 
     * @param conditionMap 
     * @return List list 
     */
    public List list(Class myPOJO, Map conditionMap) {
        // return a POJO set
        try {
            StringBuffer myHQL = new StringBuffer("from " + myPOJO.getName()
                                                  + " as myPOJO");
            List parameters = new ArrayList();

            Iterator keyItr = conditionMap.keySet().iterator();
            boolean isWherePst = true;

            if ((conditionMap != null) && (conditionMap.size() > 0)) {
                while (keyItr.hasNext()) {
                    String fieldName = (String) keyItr.next();
                    Object obj = conditionMap.get(fieldName);
                    if(obj != null){
                        if (isWherePst) {
                            myHQL.append(" where");
                            isWherePst = false;
                        } else {
                            myHQL.append(" and");
                        }
                        myHQL.append(" myPOJO.").append(fieldName + " = ?");
                        parameters.add(obj);
                    }
                }
            }

            List qList = getHibernateTemplate().find(myHQL.toString(), parameters.toArray());
            if (qList.size() > 0) {
                return qList;
            } else {
                return new ArrayList();
            }
        } catch (Exception e) {
        	LogUtils.printStackTrace(e);
            return null;
        }
    }

    /**
     * load an object list , user hql 
     * 
     * @param hql
     * @param valueObjArray 
     * @return
     */
    public List list(String hql, Object[] valueObjArray) {
        try {
            List qList = getHibernateTemplate().find(hql, valueObjArray);

            if (qList.size() > 0) {
                return qList;
            } else {
                return new ArrayList();
            }
        } catch (Exception e){
        	LogUtils.printStackTrace(e);
            return null;
        }

    }

    /**
     * update 
     * 
     * @param myPojo
     * @return
     */
    public void update(Object myPOJO) {
        // update a POJO
        getHibernateTemplate().update(myPOJO);
    }

    /**
     * delete 
     * 
     * @param myPojo
     * @return
     */
    public void delete(Object myPOJO) {
        // delete a POJO
        this.getHibernateTemplate().delete(myPOJO);
    }
    
    /**
     * hql 
     * 
     * @param hql
     * @param preParamArray
     */
    /*
    public void delete(String hql, Object[] preParamArray) {
    	List preParamList = new ArrayList();
    	Type[] typeArray = new Type[preParamArray.length];
    	Object preParam = null;
    	for(int i=0; i<preParamArray.length; i++) {
    		preParam = preParamArray[i];
    		if(preParam instanceof java.util.Date) {
    			preParamList.add(preParam);
    			typeArray[i] = new DateType();
    		} else {
    			preParamList.add(preParam.toString());
    			typeArray[i] = new StringType();
    		}
    	}
    	this.getHibernateTemplate().delete(hql, preParamList.toArray(), typeArray);
    }
    */

}
