package com.hc.common.database;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * ResultRowMapper implements RowMapper
 * 
 * @author lizz
 *
 */
public class ResultRowMapper implements RowMapper {
    public ResultRowMapper() {
    }

    public Object mapRow(ResultSet rs, int _int) throws
            SQLException {
        HashMap res = new HashMap();
        ResultSetMetaData metaData = rs.getMetaData();
        //select name1 as label1 from table1
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String name = metaData.getColumnLabel(i);
            res.put(name.toUpperCase(), rs.getObject(i));
        }
        return res;
    }

}
