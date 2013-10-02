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


/**
 * @author Adam Lowe
 */
public class AddDomainOptions extends DomainOptions {
   public static class Builder {
      /**
       * @see AddDomainOptions#primaryNameServer
       */
      public static AddDomainOptions primaryNameServer(String primaryNameServer) {
         return AddDomainOptions.class.cast(new AddDomainOptions().primaryNameServer(primaryNameServer));
      }

      /**
       * @see AddDomainOptions#responsiblePerson
       */
      public static AddDomainOptions responsiblePerson(String responsiblePerson) {
         return AddDomainOptions.class.cast(new AddDomainOptions().responsiblePerson(responsiblePerson));
      }

      /**
       * @see AddDomainOptions#ttl
       */
      public static AddDomainOptions ttl(int ttl) {
         return AddDomainOptions.class.cast(new AddDomainOptions().ttl(ttl));
      }

      /**
       * @see AddDomainOptions#refresh
       */
      public static AddDomainOptions refresh(int refresh) {
         return AddDomainOptions.class.cast(new AddDomainOptions().refresh(refresh));
      }

      /**
       * @see AddDomainOptions#retry
       */
      public static AddDomainOptions retry(int retry) {
         return AddDomainOptions.class.cast(new AddDomainOptions().retry(retry));
      }

      /**
       * @see AddDomainOptions#expire
       */
      public static AddDomainOptions expire(int expire) {
         return AddDomainOptions.class.cast(new AddDomainOptions().expire(expire));
      }

      /**
       * @see AddDomainOptions#minimum
       */
      public static AddDomainOptions minimum(int minimum) {
         return AddDomainOptions.class.cast(new AddDomainOptions().minimum(minimum));
      }

      /**
       * @see AddDomainOptions#minimalRecords
       */
      public static AddDomainOptions minimalRecords() {
         return AddDomainOptions.class.cast(new AddDomainOptions().minimalRecords());
      }
   }

   /**
    * Ensure only NS and SOA records will be created by default, when this option is not used a number of default records will be created on the domain.
    */
   public DomainOptions minimalRecords() {
      formParameters.put("createrecords", Boolean.FALSE.toString());
      return this;
   }

}
