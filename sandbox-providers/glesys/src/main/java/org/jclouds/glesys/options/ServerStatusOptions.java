package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 */
public class ServerStatusOptions extends BaseHttpRequestOptions {

   public enum StatusTypes {
      state, cpu, memory, disk, bandwidth, uptime
   }

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions state() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.state);
      }

      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions cpu() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.cpu);
      }

      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions memory() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.memory);
      }

      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions disk() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.disk);
      }

      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions bandwidth() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.bandwidth);
      }
   }

   /**
    * Select the given type of information form the server
    */
   public ServerStatusOptions statusType(StatusTypes type) {
      formParameters.put("statustype", type.name());
      return this;
   }
}
