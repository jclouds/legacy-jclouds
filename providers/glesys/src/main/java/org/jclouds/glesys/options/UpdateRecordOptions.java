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
public class UpdateRecordOptions extends AddRecordOptions {

   public static class Builder {
      /**
       * @see UpdateRecordOptions#host
       */
      public static UpdateRecordOptions host(String host) {
         return new UpdateRecordOptions().host(host);
      }

      /**
       * @see UpdateRecordOptions#type
       */
      public static UpdateRecordOptions type(String type) {
         return new UpdateRecordOptions().type(type);
      }

      /**
       * @see UpdateRecordOptions#data
       */
      public static UpdateRecordOptions data(String data) {
         return new UpdateRecordOptions().data(data);
      }

      /**
       * @see UpdateRecordOptions#ttl
       */
      public static UpdateRecordOptions ttl(int ttl) {
         return UpdateRecordOptions.class.cast(new UpdateRecordOptions().ttl(ttl));
      }
   }


   /** Configure the hostname attached to this record */
   public UpdateRecordOptions host(String host) {
      formParameters.put("host", host);
      return this;
   }

   /** Configure the type of record, ex. "A", "CNAME" or "MX"  */
   public UpdateRecordOptions type(String type) {
      formParameters.put("type", type);
      return this;
   }

   /** Set the content of this record (depending on type, for an "A" record this would be an ip address) */
   public UpdateRecordOptions data(String data) {
      formParameters.put("data", data);
      return this;
   }

   @Override
   public UpdateRecordOptions ttl(int ttl) {
      return UpdateRecordOptions.class.cast(super.ttl(ttl));
   }
}
