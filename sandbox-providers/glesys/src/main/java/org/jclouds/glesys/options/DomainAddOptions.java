package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 */
public class DomainAddOptions extends DomainOptions {
   public static class Builder {
      /**
       * @see DomainAddOptions#primaryNameServer
       */
      public static DomainAddOptions primaryNameServer(String primaryNameServer) {
         return DomainAddOptions.class.cast(new DomainAddOptions().primaryNameServer(primaryNameServer));
      }

      /**
       * @see DomainAddOptions#responsiblePerson
       */
      public static DomainAddOptions responsiblePerson(String responsiblePerson) {
         return DomainAddOptions.class.cast(new DomainAddOptions().responsiblePerson(responsiblePerson));
      }

      /**
       * @see DomainAddOptions#ttl
       */
      public static DomainAddOptions ttl(int ttl) {
         return DomainAddOptions.class.cast(new DomainAddOptions().ttl(ttl));
      }

      /**
       * @see DomainAddOptions#refresh
       */
      public static DomainAddOptions refresh(int refresh) {
         return DomainAddOptions.class.cast(new DomainAddOptions().refresh(refresh));
      }

      /**
       * @see DomainAddOptions#retry
       */
      public static DomainAddOptions retry(int retry) {
         return DomainAddOptions.class.cast(new DomainAddOptions().retry(retry));
      }

      /**
       * @see DomainAddOptions#expire
       */
      public static DomainAddOptions expire(int expire) {
         return DomainAddOptions.class.cast(new DomainAddOptions().expire(expire));
      }

      /**
       * @see DomainAddOptions#minimum
       */
      public static DomainAddOptions minimum(int minimum) {
         return DomainAddOptions.class.cast(new DomainAddOptions().minimum(minimum));
      }

      /**
       * @see DomainAddOptions#minimalRecords
       */
      public static DomainAddOptions minimalRecords() {
         return DomainAddOptions.class.cast(new DomainAddOptions().minimalRecords());
      }
   }

   /**
    * Ensure only NS and SOA records will be created by default, when this option is not used a number of default records will be created on the domain.
    */
   public DomainOptions minimalRecords() {
      formParameters.put("create_records",  "0");
      return this;
   }

}