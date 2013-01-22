package org.jclouds.rackspace.cloudloadbalancers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Session persistence is a feature of the load balancing service that forces multiple requests from clients to be
 * directed to the same node. This is common with many web applications that do not inherently share application
 * state between back-end servers.
 */
public enum SessionPersistence {
   /**
    * A session persistence mechanism that inserts an HTTP cookie and is used to determine the destination back-end
    * node. This is supported for HTTP load balancing only.
    */
   HTTP_COOKIE,
   /**
    * A session persistence mechanism that will keep track of the source IP address that is mapped and is able to
    * determine the destination back-end node. This is supported for HTTPS pass-through and non-HTTP load balancing
    * only.
    */
   SOURCE_IP,

   UNRECOGNIZED;

   public static SessionPersistence fromValue(String sessionPersistence) {
      try {
         return valueOf(checkNotNull(sessionPersistence, "sessionPersistence"));
      }
      catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}