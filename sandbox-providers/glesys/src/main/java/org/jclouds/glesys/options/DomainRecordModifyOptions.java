package org.jclouds.glesys.options;

/**
 * @author Adam Lowe
 */
public class DomainRecordModifyOptions extends DomainRecordAddOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.DomainRecordAddOptions#host
       */
      public static DomainRecordModifyOptions host(String host) {
         DomainRecordModifyOptions options = new DomainRecordModifyOptions();
         return options.host(host);
      }

      /**
       * @see org.jclouds.glesys.options.DomainRecordModifyOptions#type
       */
      public static DomainRecordModifyOptions type(String type) {
         DomainRecordModifyOptions options = new DomainRecordModifyOptions();
         return options.type(type);
      }

      /**
       * @see org.jclouds.glesys.options.DomainRecordModifyOptions#data
       */
      public static DomainRecordModifyOptions data(String data) {
         DomainRecordModifyOptions options = new DomainRecordModifyOptions();
         return options.data(data);
      }

      /**
       * @see org.jclouds.glesys.options.DomainRecordModifyOptions#ttl
       */
      public static DomainRecordModifyOptions ttl(int ttl) {
         DomainRecordModifyOptions options = new DomainRecordModifyOptions();
         return DomainRecordModifyOptions.class.cast(options.ttl(ttl));
      }

      /**
       * @see org.jclouds.glesys.options.DomainRecordModifyOptions#mxPriority
       */
      public static DomainRecordModifyOptions mxPriority(String mxPriority) {
         DomainRecordModifyOptions options = new DomainRecordModifyOptions();
         return DomainRecordModifyOptions.class.cast(options.mxPriority(mxPriority));
      }
   }

   public DomainRecordModifyOptions host(String host) {
      formParameters.put("host", host);
      return this;
   }


   public DomainRecordModifyOptions type(String type) {
      formParameters.put("type", type);
      return this;
   }

   public DomainRecordModifyOptions data(String data) {
      formParameters.put("data", data);
      return this;
   }
}