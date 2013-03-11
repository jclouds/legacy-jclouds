/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.domain.rdata;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;

/**
 * Corresponds to the binary representation of the {@code CNAME} (Canonical Name) RData
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * import static org.jclouds.dynect.v3.domain.rdata.NSData.cname;
 * ...
 * NSData rdata = cname("cname.foo.com.");
 * </pre>
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc1035.txt">RFC 1035</a>
 */
public class CNAMEData extends ForwardingMap<String, Object> {

   private final String cname;

   @ConstructorProperties("cname")
   private CNAMEData(String cname) {
      this.cname = checkNotNull(cname, "cname");
      this.delegate = ImmutableMap.<String, Object> of("cname", cname);
   }

   /**
    * domain-name which specifies the canonical or primary name for the owner.
    * The owner name is an alias.
    */
   public String getCname() {
      return cname;
   }

   private final transient ImmutableMap<String, Object> delegate;

   protected Map<String, Object> delegate() {
      return delegate;
   }

   public static CNAMEData cname(String cname) {
      return builder().cname(cname).build();
   }

   public static CNAMEData.Builder builder() {
      return new Builder();
   }

   public CNAMEData.Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String cname;

      /**
       * @see CNAMEData#getCname()
       */
      public CNAMEData.Builder cname(String cname) {
         this.cname = cname;
         return this;
      }

      public CNAMEData build() {
         return new CNAMEData(cname);
      }

      public CNAMEData.Builder from(CNAMEData in) {
         return this.cname(in.getCname());
      }
   }
}