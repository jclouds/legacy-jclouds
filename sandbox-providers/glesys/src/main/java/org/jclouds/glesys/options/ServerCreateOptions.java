package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 */
public class ServerCreateOptions extends BaseHttpRequestOptions {
   public static class Builder {
      /**
       * @see ServerCreateOptions#description
       */
      public static ServerCreateOptions description(String primaryNameServer) {
         ServerCreateOptions options = new ServerCreateOptions();
         return options.description(primaryNameServer);
      }

      /**
       * @see ServerCreateOptions#ip
       */
      public static ServerCreateOptions ip(String ip) {
         ServerCreateOptions options = new ServerCreateOptions();
         return options.ip(ip);
      }
   }

   /**
    * @param description the description of the server
    */
   public ServerCreateOptions description(String description) {
      formParameters.put("description", description);
      return this;
   }

   /**
    * @param ip the ip address to assign to the server
    */
   public ServerCreateOptions ip(String ip) {
      formParameters.put("ip", ip);
      return this;
   }

}
