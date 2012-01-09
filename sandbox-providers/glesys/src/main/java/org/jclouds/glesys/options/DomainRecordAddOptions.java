package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 */
public class DomainRecordAddOptions extends BaseHttpRequestOptions {

   public static class Builder {
      /**
       * @see DomainRecordAddOptions#ttl
       */
      public static DomainRecordAddOptions ttl(int ttl) {
         DomainRecordAddOptions options = new DomainRecordAddOptions();
         return options.ttl(ttl);
      }

      /**
       * @see DomainRecordAddOptions#mxPriority
       */
      public static DomainRecordAddOptions mxPriority(int mxPriority) {
         DomainRecordAddOptions options = new DomainRecordAddOptions();
         return options.mxPriority(mxPriority);
      }
   }

   /** Configure TTL/Time-to-live for record */
   public DomainRecordAddOptions ttl(int ttl) {
      formParameters.put("ttl", Integer.toString(ttl));
      return this;
   }

   /** Configure the priority of an MX record */
   public DomainRecordAddOptions mxPriority(int mxPriority) {
      formParameters.put("mx_priority", Integer.toString(mxPriority));
      return this;
   }

}