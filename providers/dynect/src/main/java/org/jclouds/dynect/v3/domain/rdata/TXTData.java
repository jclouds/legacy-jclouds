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
 * Corresponds to the binary representation of the {@code TXT} (Text) RData
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * import static org.jclouds.dynect.v3.domain.rdata.NSData.txt;
 * ...
 * NSData rdata = txt("=spf1 ip4:1.1.1.1/24 ip4:2.2.2.2/24 -all");
 * </pre>
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc1035.txt">RFC 1035</a>
 */
public class TXTData extends ForwardingMap<String, Object> {

   private final String txtdata;

   @ConstructorProperties("txtdata")
   private TXTData(String txtdata) {
      this.txtdata = checkNotNull(txtdata, "txtdata");
      this.delegate = ImmutableMap.<String, Object> of("txtdata", txtdata);
   }

   /**
    * One or more character-strings.
    */
   public String getTxtdata() {
      return txtdata;
   }

   private final transient ImmutableMap<String, Object> delegate;

   protected Map<String, Object> delegate() {
      return delegate;
   }

   public static TXTData txt(String txtdata) {
      return builder().txtdata(txtdata).build();
   }

   public static TXTData.Builder builder() {
      return new Builder();
   }

   public TXTData.Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String txtdata;

      /**
       * @see TXTData#getTxtdata()
       */
      public TXTData.Builder txtdata(String txtdata) {
         this.txtdata = txtdata;
         return this;
      }

      public TXTData build() {
         return new TXTData(txtdata);
      }

      public TXTData.Builder from(TXTData in) {
         return this.txtdata(in.getTxtdata());
      }
   }
}
