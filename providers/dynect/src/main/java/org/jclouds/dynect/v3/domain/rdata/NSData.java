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
 * Corresponds to the binary representation of the {@code NS} (Name Server) RData
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * import static org.jclouds.dynect.v3.domain.rdata.NSData.ns;
 * ...
 * NSData rdata = ns("ns.foo.com.");
 * </pre>
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc1035.txt">RFC 1035</a>
 */
public class NSData extends ForwardingMap<String, Object> {

   private final String nsdname;

   @ConstructorProperties("nsdname")
   private NSData(String nsdname) {
      this.nsdname = checkNotNull(nsdname, "nsdname");
      this.delegate = ImmutableMap.<String, Object> of("nsdname", nsdname);
   }

   /**
    * domain-name which specifies a host which should be authoritative for the
    * specified class and domain.
    */
   public String getNsdname() {
      return nsdname;
   }

   private final transient ImmutableMap<String, Object> delegate;

   protected Map<String, Object> delegate() {
      return delegate;
   }

   public static NSData ns(String nsdname) {
      return builder().nsdname(nsdname).build();
   }

   public static NSData.Builder builder() {
      return new Builder();
   }

   public NSData.Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String nsdname;

      /**
       * @see NSData#getNsdname()
       */
      public NSData.Builder nsdname(String nsdname) {
         this.nsdname = nsdname;
         return this;
      }

      public NSData build() {
         return new NSData(nsdname);
      }

      public NSData.Builder from(NSData in) {
         return this.nsdname(in.getNsdname());
      }
   }
}
