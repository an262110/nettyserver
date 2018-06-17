package com.hc.common.database;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ResultSingleValueMapper implements RowMapper
 * 
 * @author lizz 
 *
 */
public class ResultSingleValueMapper implements RowMapper {
    public ResultSingleValueMapper() {
    }

    public Object mapRow(ResultSet rs, int _int) throws
            SQLException {
        return rs.getObject(1);
    }

}
