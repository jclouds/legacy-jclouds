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
public class EditRecordOptions extends AddRecordOptions {

   public static class Builder {
      /**
       * @see EditRecordOptions#host
       */
      public static EditRecordOptions host(String host) {
         return new EditRecordOptions().host(host);
      }

      /**
       * @see EditRecordOptions#type
       */
      public static EditRecordOptions type(String type) {
         return new EditRecordOptions().type(type);
      }

      /**
       * @see EditRecordOptions#data
       */
      public static EditRecordOptions data(String data) {
         return new EditRecordOptions().data(data);
      }

      /**
       * @see EditRecordOptions#ttl
       */
      public static EditRecordOptions ttl(int ttl) {
         return EditRecordOptions.class.cast(new EditRecordOptions().ttl(ttl));
      }
   }


   /** Configure the hostname attached to this record */
   public EditRecordOptions host(String host) {
      formParameters.put("host", host);
      return this;
   }

   /** Configure the type of record, ex. "A", "CNAME" or "MX"  */
   public EditRecordOptions type(String type) {
      formParameters.put("type", type);
      return this;
   }

   /** Set the content of this record (depending on type, for an "A" record this would be an ip address) */
   public EditRecordOptions data(String data) {
      formParameters.put("data", data);
      return this;
   }
}