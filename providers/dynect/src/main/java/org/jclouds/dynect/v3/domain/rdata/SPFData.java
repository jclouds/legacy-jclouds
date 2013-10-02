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
 * Corresponds to the binary representation of the {@code SPF} (Sender Policy
 * Framework) RData
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * import static denominator.model.rdata.SPFData.spf;
 * ...
 * SPFData rdata = spf("v=spf1 +mx a:colo.example.com/28 -all");
 * </pre>
 * 
 * @see <a href="http://tools.ietf.org/html/rfc4408#section-3.1.1">RFC 4408</a>
 */
public class SPFData extends ForwardingMap<String, Object> {

   public static SPFData create(String txtdata) {
      return new SPFData(txtdata);
   }

   private final String txtdata;

   @ConstructorProperties("txtdata")
   private SPFData(String txtdata) {
      this.txtdata = checkNotNull(txtdata, "txtdata");
      this.delegate = ImmutableMap.<String, Object> of("txtdata", txtdata);
   }

   /**
    * One or more character-strings.
    */
   public String getTxtdata() {
      return txtdata;
   }

   // transient to avoid serializing by default, for example in json
   private final transient ImmutableMap<String, Object> delegate;

   @Override
   protected Map<String, Object> delegate() {
      return delegate;
   }
}
