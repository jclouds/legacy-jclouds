package org.jclouds.glesys.options;

/**
 * @author Adam Lowe
 */
public class DomainRecordEditOptions extends DomainRecordAddOptions {

   public static class Builder {
      /**
       * @see DomainRecordEditOptions#host
       */
      public static DomainRecordEditOptions host(String host) {
         return new DomainRecordEditOptions().host(host);
      }

      /**
       * @see DomainRecordEditOptions#type
       */
      public static DomainRecordEditOptions type(String type) {
         return new DomainRecordEditOptions().type(type);
      }

      /**
       * @see DomainRecordEditOptions#data
       */
      public static DomainRecordEditOptions data(String data) {
         return new DomainRecordEditOptions().data(data);
      }

      /**
       * @see DomainRecordEditOptions#ttl
       */
      public static DomainRecordEditOptions ttl(int ttl) {
         return DomainRecordEditOptions.class.cast(new DomainRecordEditOptions().ttl(ttl));
      }

      /**
       * @see DomainRecordEditOptions#mxPriority
       */
      public static DomainRecordEditOptions mxPriority(int mxPriority) {
         return DomainRecordEditOptions.class.cast(new DomainRecordEditOptions().mxPriority(mxPriority));
      }
   }


   /** Configure the hostname attached to this record */
   public DomainRecordEditOptions host(String host) {
      formParameters.put("host", host);
      return this;
   }

   /** Configure the type of record, ex. "A", "CNAME" or "MX"  */
   public DomainRecordEditOptions type(String type) {
      formParameters.put("type", type);
      return this;
   }

   /** Set the content of this record (depending on type, for an "A" record this would be an ip address) */
   public DomainRecordEditOptions data(String data) {
      formParameters.put("data", data);
      return this;
   }
}