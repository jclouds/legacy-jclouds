package org.jclouds.gogrid.domain.internal;

import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.util.SortedSet;

/**
 * @author Oleksiy Yarmula
 */
public class GenericResponseContainer<T> {

    private Summary summary;
    private String status;
    private String method;
    private SortedSet<T> list;

    public Summary getSummary() {
        return summary;
    }

    public String getStatus() {
        return status;
    }

    public String getMethod() {
        return method;
    }

    public SortedSet<T> getList() {
        return list;
    }

    static class Summary {
        private int total;
        private int start;
        private int numPages;
        private int returned;

        public int getTotal() {
            return total;
        }

        public int getStart() {
            return start;
        }

        public int getNumPages() {
            return numPages;
        }

        public int getReturned() {
            return returned;
        }
    }

}
