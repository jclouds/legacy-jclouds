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
 * Corresponds to the binary representation of the {@code PTR} (Pointer) RData
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * import static org.jclouds.dynect.v3.domain.rdata.NSData.ptr;
 * ...
 * NSData rdata = ptr("ptr.foo.com.");
 * </pre>
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc1035.txt">RFC 1035</a>
 */
public class PTRData extends ForwardingMap<String, Object> {

   private final String ptrdname;

   @ConstructorProperties("ptrdname")
   private PTRData(String ptrdname) {
      this.ptrdname = checkNotNull(ptrdname, "ptrdname");
      this.delegate = ImmutableMap.<String, Object> of("ptrdname", ptrdname);
   }

   /**
    * domain-name which points to some location in the domain name space.
    */
   public String getPtrdname() {
      return ptrdname;
   }

   private final transient ImmutableMap<String, Object> delegate;

   protected Map<String, Object> delegate() {
      return delegate;
   }

   public static PTRData ptr(String ptrdname) {
      return builder().ptrdname(ptrdname).build();
   }

   public static PTRData.Builder builder() {
      return new Builder();
   }

   public PTRData.Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String ptrdname;

      /**
       * @see PTRData#getPtrdname()
       */
      public PTRData.Builder ptrdname(String ptrdname) {
         this.ptrdname = ptrdname;
         return this;
      }

      public PTRData build() {
         return new PTRData(ptrdname);
      }

      public PTRData.Builder from(PTRData in) {
         return this.ptrdname(in.getPtrdname());
      }
   }
}
