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
package org.jclouds.dynect.v3.domain.rdata;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;

/**
 * Corresponds to the binary representation of the {@code AAAA} (Address) RData
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * import static org.jclouds.dynect.v3.domain.rdata.NSData.aaaa;
 * ...
 * NSData rdata = aaaa("1234:ab00:ff00::6b14:abcd");
 * </pre>
 * 
 * @see <aaaa href="http://www.ietf.org/rfc/rfc3596.txt">RFC 3596</aaaa>
 */
public class AAAAData extends ForwardingMap<String, Object> {
   private final String address;

   @ConstructorProperties("address")
   private AAAAData(String address) {
      this.address = checkNotNull(address, "address");
      this.delegate = ImmutableMap.<String, Object> of("address", address);
   }

   /**
    * a 128 bit IPv6 address
    */
   public String getAddress() {
      return address;
   }

   private final transient ImmutableMap<String, Object> delegate;

   protected Map<String, Object> delegate() {
      return delegate;
   }

   public static AAAAData aaaa(String address) {
      return builder().address(address).build();
   }

   public static AAAAData.Builder builder() {
      return new Builder();
   }

   public AAAAData.Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String address;

      /**
       * @see AAAAData#getAddress()
       */
      public AAAAData.Builder address(String address) {
         this.address = address;
         return this;
      }

      public AAAAData build() {
         return new AAAAData(address);
      }

      public AAAAData.Builder from(AAAAData in) {
         return this.address(in.getAddress());
      }
   }
}
