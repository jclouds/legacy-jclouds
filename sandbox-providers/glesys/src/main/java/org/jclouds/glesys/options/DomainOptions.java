/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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