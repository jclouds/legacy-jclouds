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
import com.google.common.primitives.UnsignedInteger;

/**
 * Corresponds to the binary representation of the {@code MX} (Mail Exchange)
 * RData
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * import static org.jclouds.dynect.v3.domain.rdata.MXData.mx;
 * ...
 * MXData rdata = mx(1, "mail.jclouds.org");
 * </pre>
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc1035.txt">RFC 1035</a>
 */
public class MXData extends ForwardingMap<String, Object> {

   @ConstructorProperties({ "preference", "exchange" })
   private MXData(UnsignedInteger preference, String exchange) {
      this.delegate = ImmutableMap.<String, Object> builder().put("preference", checkNotNull(preference, "preference"))
            .put("exchange", checkNotNull(exchange, "exchange")).build();
   }

   private final ImmutableMap<String, Object> delegate;

   protected Map<String, Object> delegate() {
      return delegate;
   }

   /**
    * specifies the preference given to this RR among others at the same owner.
    * Lower values are preferred.
    */
   public UnsignedInteger getPreference() {
      return UnsignedInteger.class.cast(get("preference"));
   }

   /**
    * domain-name which specifies a host willing to act as a mail exchange for
    * the owner name.
    */
   public String getExchange() {
      return String.class.cast(get("exchange"));
   }

   public static MXData mx(int preference, String exchange) {
      return builder().preference(preference).exchange(exchange).build();
   }

   public static MXData.Builder builder() {
      return new Builder();
   }

   public MXData.Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private UnsignedInteger preference;
      private String exchange;

      /**
       * @see MXData#getPreference()
       */
      public MXData.Builder preference(int preference) {
         return preference(UnsignedInteger.valueOf(preference));
      }

      /**
       * @see MXData#getPreference()
       */
      public MXData.Builder preference(UnsignedInteger preference) {
         this.preference = preference;
         return this;
      }

      /**
       * @see MXData#getExchange()
       */
      public MXData.Builder exchange(String exchange) {
         this.exchange = exchange;
         return this;
      }

      public MXData build() {
         return new MXData(preference, exchange);
      }

      public MXData.Builder from(MXData in) {
         return this.preference(in.getPreference()).exchange(in.getExchange());
      }
   }
}
