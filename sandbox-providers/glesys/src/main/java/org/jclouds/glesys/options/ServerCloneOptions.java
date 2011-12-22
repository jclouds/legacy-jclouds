package org.jclouds.glesys.options;

/**
 * @author Adam Lowe
 */
public class ServerCloneOptions extends ServerEditOptions {
   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#disksize
       */
      public static ServerCloneOptions disksize(int disksize) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.disksize(disksize));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#memorysize
       */
      public static ServerCloneOptions memorysize(int memorysize) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.memorysize(memorysize));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#cpucores
       */
      public static ServerCloneOptions cpucores(int cpucores) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.cpucores(cpucores));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#cpucores
       */
      public static ServerCloneOptions transfer(int transfer) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.transfer(transfer));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#hostname
       */
      public static ServerCloneOptions hostname(String hostname) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.hostname(hostname));
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#description
       */
      public static ServerCloneOptions description(String description) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.description(description));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#dataCenter
       */
      public static ServerCloneOptions dataCenter(String dataCenter) {
         ServerCloneOptions options = new ServerCloneOptions();
         return options.dataCenter(dataCenter);
      }
   }

   public ServerCloneOptions dataCenter(String dataCenter) {
      formParameters.put("datacenter", dataCenter);
      return this;
   }
}
