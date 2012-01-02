package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 */
public class ServerDestroyOptions extends BaseHttpRequestOptions {
   public static class Builder {
      /**
       * Discard the server's ip on destroy
       */
      public static ServerDestroyOptions keepIp() {
         return new ServerDestroyOptions().keepIp(true);
      }

      /**
       * Discard the server's ip on destroy
       */
      public static ServerDestroyOptions discardIp() {
         return new ServerDestroyOptions().keepIp(false);
      }

   }

   /**
    * Determines whether to keep the server's ip attached to your account when destroying a server
    *
    * @param keepIp if true, keep the ip address
    */
   public ServerDestroyOptions keepIp(boolean keepIp) {
      formParameters.put("keepip", Integer.toString(keepIp ? 1 : 0));
      return this;
   }
}