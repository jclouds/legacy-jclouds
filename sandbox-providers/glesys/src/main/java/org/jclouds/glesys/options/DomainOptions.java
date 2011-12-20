package org.jclouds.glesys.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adam Lowe
 */
public class DomainOptions extends BaseHttpRequestOptions {
   public static class Builder {
      /**
       * @see DomainOptions#primaryNameServer
       */
      public static DomainOptions primaryNameServer(String primaryNameServer) {
         DomainOptions options = new DomainOptions();
         return options.primaryNameServer(primaryNameServer);
      }

      /**
       * @see DomainOptions#responsiblePerson
       */
      public static DomainOptions responsiblePerson(String responsiblePerson) {
         DomainOptions options = new DomainOptions();
         return options.responsiblePerson(responsiblePerson);
      }

      /**
       * @see DomainOptions#ttl
       */
      public static DomainOptions ttl(int ttl) {
         DomainOptions options = new DomainOptions();
         return options.ttl(ttl);
      }

      /**
       * @see DomainOptions#refresh
       */
      public static DomainOptions refresh(String refresh) {
         DomainOptions options = new DomainOptions();
         return options.refresh(refresh);
      }

      /**
       * @see DomainOptions#retry
       */
      public static DomainOptions retry(String retry) {
         DomainOptions options = new DomainOptions();
         return options.retry(retry);
      }

      /**
       * @see DomainOptions#expire
       */
      public static DomainOptions expire(String expire) {
         DomainOptions options = new DomainOptions();
         return options.expire(expire);
      }

      /**
       * @see DomainOptions#minimum
       */
      public static DomainOptions minimum(String minimum) {
         DomainOptions options = new DomainOptions();
         return options.minimum(minimum);
      }
   }

   public DomainOptions primaryNameServer(String primaryNameServer) {
      formParameters.put("primary_ns", primaryNameServer);
      return this;
   }

   public DomainOptions responsiblePerson(String responsiblePerson) {
      formParameters.put("resp_person", responsiblePerson);
      return this;
   }

   public DomainOptions ttl(int ttl) {
      formParameters.put("ttl", Integer.toString(ttl));
      return this;
   }

   public DomainOptions refresh(String refresh) {
      formParameters.put("refresh", refresh);
      return this;
   }

   public DomainOptions retry(String retry) {
      formParameters.put("retry", retry);
      return this;
   }

   public DomainOptions expire(String expire) {
      formParameters.put("primary_ns", expire);
      return this;
   }

   public DomainOptions minimum(String minimum) {
      formParameters.put("minimum", minimum);
      return this;
   }
}