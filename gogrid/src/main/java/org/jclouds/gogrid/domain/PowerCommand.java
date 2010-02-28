package org.jclouds.gogrid.domain;

import com.google.common.base.CaseFormat;

/**
 * Server's state transition.
 *
 * Using this value, server's state will be changed
 * to one of the following:
 * <ul>
 * <li>Start</li>
 * <li>Stop</li>
 * <li>Restart</li>
 * </ul>
 *
 * @see org.jclouds.gogrid.services.GridServerClient#power(String, PowerCommand)
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API:grid.server.power" />
 *
 * @author Oleksiy Yarmula
 */
public enum PowerCommand {
    START,
    STOP /*NOTE: This is a hard shutdown, equivalent to powering off a server.*/,
    RESTART;


    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
