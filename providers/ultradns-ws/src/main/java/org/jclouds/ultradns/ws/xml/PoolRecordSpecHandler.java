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
package org.jclouds.ultradns.ws.xml;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Integer.parseInt;
import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.PoolRecordSpec;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class PoolRecordSpecHandler extends ParseSax.HandlerForGeneratedRequestWithResult<PoolRecordSpec> {

   private final PoolRecordSpec.Builder builder = PoolRecordSpec.builder();

   @Override
   public PoolRecordSpec getResult() {
      return builder.build();
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attrs) {
      if (!equalsOrSuffix(qName, "PoolRecordSpecData")) {
         return;
      }

      Map<String, String> attributes = cleanseAttributes(attrs);

      builder.description(attributes.get("description"))
             .state(attributes.get("recordState"));
      
      builder.probingEnabled(trueIfEnabled(attributes, "probing"))
             .allFailEnabled(trueIfEnabled(attributes, "allFail"));
      
      builder.weight(asInt(attributes, "weight"))
             .failOverDelay(asInt(attributes, "failOverDelay"))
             .threshold(asInt(attributes, "threshold"))
             .ttl(asInt(attributes, "ttl"));
   }

   private boolean trueIfEnabled(Map<String, String> attributes, String name) {
      return "ENABLED".equalsIgnoreCase(attributes.get(name));
   }

   private int asInt(Map<String, String> attributes, String name) {
      return parseInt(checkNotNull(attributes.get(name), name));
   }
}
