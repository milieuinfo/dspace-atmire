package com.atmire.util;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 04 Nov 2014
 */
public class WhereConstruct {

    private List<String> wheres = new LinkedList<String>();

    public void addEqualField(String field, String value) {
        wheres.add(field + "='" + value + "'");
    }

    public void addEqualField(String field, int value) {
        wheres.add(field + "=" + value + "");
    }

    public String toString() {
        String whereClause = "";
        if (!wheres.isEmpty()) {
            for (String where : wheres) {
                if (StringUtils.isNotBlank(whereClause)) {
                    whereClause += " AND ";
                }
                whereClause += where.trim();
            }
            return "WHERE " + whereClause;
        }
        return "";
    }
}
