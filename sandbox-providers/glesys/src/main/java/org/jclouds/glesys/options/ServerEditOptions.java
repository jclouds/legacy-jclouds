package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * 
 * @author Adam Lowe
 */
public class ServerEditOptions extends BaseHttpRequestOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#disksize
       */
      public static ServerEditOptions disksize(int disksize) {
         ServerEditOptions options = new ServerEditOptions();
         return options.disksize(disksize);
      }
      
      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#memorysize
       */
      public static ServerEditOptions memorysize(int memorysize) {
         ServerEditOptions options = new ServerEditOptions();
         return options.memorysize(memorysize);
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#cpucores
       */
      public static ServerEditOptions cpucores(int cpucores) {
         ServerEditOptions options = new ServerEditOptions();
         return options.cpucores(cpucores);
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#cpucores
       */
      public static ServerEditOptions transfer(int transfer) {
         ServerEditOptions options = new ServerEditOptions();
         return options.transfer(transfer);
      }
      
      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#hostname
       */
      public static ServerEditOptions hostname(String hostname) {
         ServerEditOptions options = new ServerEditOptions();
         return options.hostname(hostname);
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#description
       */
      public static ServerEditOptions description(String description) {
         ServerEditOptions options = new ServerEditOptions();
         return options.description(description);
      }
   }

   public ServerEditOptions disksize(int disksize) {
      formParameters.put("disksize", Integer.toString(disksize));
      return this;
   }

   public ServerEditOptions memorysize(int memorysize) {
      formParameters.put("memorysize", Integer.toString(memorysize));
      return this;
   }

   public ServerEditOptions cpucores(int cpucores) {
      formParameters.put("cpucores", Integer.toString(cpucores));
      return this;
   }

   public ServerEditOptions transfer(int transfer) {
      formParameters.put("cpucores", Integer.toString(transfer));
      return this;
   }

   public ServerEditOptions hostname(String hostname) {
      formParameters.put("hostname", hostname);
      return this;
   }

   public ServerEditOptions description(String description) {
      formParameters.put("description", description);
      return this;
   }

}
