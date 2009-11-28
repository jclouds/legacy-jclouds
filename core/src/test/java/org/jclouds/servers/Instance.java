package org.jclouds.servers;

import com.google.common.base.Service;

/**
 * @author Ivan Meredith
 */
public interface Instance extends Service {
   String getId();

   String getTag();
}
