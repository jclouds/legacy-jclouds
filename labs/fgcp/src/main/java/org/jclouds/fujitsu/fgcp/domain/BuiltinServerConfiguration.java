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
package org.jclouds.fujitsu.fgcp.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * Possible statuses of a built-in server, also called extended function module
 * (EFM), such as a firewall or load balancer (SLB).
 * <p>
 * In addition to statuses that apply to regular virtual servers, it includes
 * statuses relevant to the upgrade process for functionality of the built-in
 * server.
 * 
 * @author Dies Koper
 */
public enum BuiltinServerConfiguration {
   FW_NAT_RULE,
   FW_DNS,
   FW_POLICY,
   FW_LOG,
   FW_LIMIT_POLICY,
   SLB_RULE,
   SLB_LOAD_STATISTICS,
   SLB_ERROR_STATISTICS,
   SLB_CERTIFICATE_LIST,
   EFM_UPDATE,
   SLB_CONNECTION,
   UNRECOGNIZED;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
   }

   @Override
   public String toString() {
      return value();
   }

   public static BuiltinServerConfiguration fromValue(String configuration) {
      try {
         return valueOf(CaseFormat.UPPER_CAMEL
               .to(CaseFormat.UPPER_UNDERSCORE,
                     checkNotNull(configuration, "configuration")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }

}
