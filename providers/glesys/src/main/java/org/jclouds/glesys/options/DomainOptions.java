/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
      public static DomainOptions refresh(int refresh) {
         DomainOptions options = new DomainOptions();
         return options.refresh(refresh);
      }

      /**
       * @see DomainOptions#retry
       */
      public static DomainOptions retry(int retry) {
         DomainOptions options = new DomainOptions();
         return options.retry(retry);
      }

      /**
       * @see DomainOptions#expire
       */
      public static DomainOptions expire(int expire) {
         DomainOptions options = new DomainOptions();
         return options.expire(expire);
      }

      /**
       * @see DomainOptions#minimum
       */
      public static DomainOptions minimum(int minimum) {
         DomainOptions options = new DomainOptions();
         return options.minimum(minimum);
      }
   }

   /**
    *  Configure the primary DNS server for this domain.
    */
   public DomainOptions primaryNameServer(String primaryNameServer) {
      formParameters.put("primarynameserver", primaryNameServer);
      return this;
   }

   /**
    *  Configure the E-mail address of the person responsible for this domain (usually attached to MX records).
    */
   public DomainOptions responsiblePerson(String responsiblePerson) {
      responsiblePerson = responsiblePerson.replaceAll("@", ".");
      if (!responsiblePerson.endsWith(".")) {
         responsiblePerson = responsiblePerson + ".";
      }
      formParameters.put("responsibleperson", responsiblePerson);
      return this;
   }

   /**
    * TTL (time to live). The number of seconds a domain name is cached locally before expiration and return to authoritative nameservers for updates
    */
   public DomainOptions ttl(int ttl) {
      formParameters.put("ttl", Integer.toString(ttl));
      return this;
   }

   /**
    * Configure the number of seconds between update requests from secondary and slave name servers
    */
   public DomainOptions refresh(int refresh) {
      formParameters.put("refresh", Integer.toString(refresh));
      return this;
   }

   /**
    * Configure the number of seconds the secondary/slave will wait before retrying when the last attempt failed
    */
   public DomainOptions retry(int retry) {
      formParameters.put("retry", Integer.toString(retry));
      return this;
   }

   /**
    * Configure the number of seconds a master or slave will wait before considering the data stale if it cannot reach the primary name server
    */
   public DomainOptions expire(int expire) {
      formParameters.put("expire", Integer.toString(expire));
      return this;
   }

   /**
    * Configure the minimum/default TTL if the domain does not specify ttl
    * 
    * @see #ttl
    */
   public DomainOptions minimum(int minimum) {
      formParameters.put("minimum", Integer.toString(minimum));
      return this;
   }
}
