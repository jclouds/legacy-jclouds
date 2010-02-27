package org.jclouds.gogrid.domain.internal;

import java.util.SortedSet;

/**
 * General format of GoGrid's response.
 *
 * This is the wrapper for most responses, and the actual
 * result (or error) will be set to {@link #list}.
 * Note that even the single returned item will be set to
 * {@link #list} per GoGrid's design.
 *
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
