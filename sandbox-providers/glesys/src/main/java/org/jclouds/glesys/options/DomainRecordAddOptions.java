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