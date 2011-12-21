package org.jclouds.glesys.options;

/**
 * @author Adam Lowe
 */
public class ServerStopOptions extends ServerEditOptions {
   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.ServerStopOptions#hard
       */
      public static ServerStopOptions hard() {
         ServerStopOptions options = new ServerStopOptions();
         return options.hard();
      }
   }

   /**
    * Hard stop - only supported on Xen platform
    */
   public ServerStopOptions hard() {
      formParameters.put("type", "hard");
      return this;
   }

}
