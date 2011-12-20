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
      public static DomainRecordAddOptions mxPriority(String mxPriority) {
         DomainRecordAddOptions options = new DomainRecordAddOptions();
         return options.mxPriority(mxPriority);
      }
   }

   public DomainRecordAddOptions host(String host) {
      formParameters.put("host", host);
      return this;
   }

   public DomainRecordAddOptions ttl(int ttl) {
      formParameters.put("ttl", Integer.toString(ttl));
      return this;
   }

   public DomainRecordAddOptions mxPriority(String mxPriority) {
      formParameters.put("mx_priority", mxPriority);
      return this;
   }

}