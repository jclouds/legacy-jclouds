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
package org.jclouds.ultradns.ws.domain;

/**
 * currently supported {@link ResourceRecord#getType() types} for directional
 * groups.
 * 
 * @author Adrian Cole
 */
public enum DirectionalRecordType {
   // A/CNAME
   IPV4(1),

   // AAAA/CNAME
   IPV6(28),

   TXT(16),

   SRV(33),

   PTR(12),

   RP(17),

   HINFO(13),

   NAPTR(35),

   MX(15);

   private final int code;

   private DirectionalRecordType(int code) {
      this.code = code;
   }

   /**
    * The {@link ResourceRecord#getType() type} that can be used in directional
    * groups.
    */
   public int getCode() {
      return code;
   }

}