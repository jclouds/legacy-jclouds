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