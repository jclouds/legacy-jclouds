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
         return ServerCloneOptions.class.cast(new ServerCloneOptions().disksize(disksize));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#memorysize
       */
      public static ServerCloneOptions memorysize(int memorysize) {
         return ServerCloneOptions.class.cast(new ServerCloneOptions().memorysize(memorysize));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#cpucores
       */
      public static ServerCloneOptions cpucores(int cpucores) {
         return ServerCloneOptions.class.cast(new ServerCloneOptions().cpucores(cpucores));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#cpucores
       */
      public static ServerCloneOptions transfer(int transfer) {
         return ServerCloneOptions.class.cast(new ServerCloneOptions().transfer(transfer));
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#description
       */
      public static ServerCloneOptions description(String description) {
         return ServerCloneOptions.class.cast(new ServerCloneOptions().description(description));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#dataCenter
       */
      public static ServerCloneOptions dataCenter(String dataCenter) {
         return new ServerCloneOptions().dataCenter(dataCenter);
      }
   }

   /**
    * Configure which datacenter to create the clone in
    */
   public ServerCloneOptions dataCenter(String dataCenter) {
      formParameters.put("datacenter", dataCenter);
      return this;
   }
}
