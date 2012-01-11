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