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

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.jclouds.ultradns.ws.domain.TrafficControllerPool.Builder;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class TrafficControllerPoolHandler extends ParseSax.HandlerForGeneratedRequestWithResult<TrafficControllerPool> {

   private Builder pool = TrafficControllerPool.builder();

   @Override
   public TrafficControllerPool getResult() {
      try {
         return pool.build();
      } finally {
         pool = TrafficControllerPool.builder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "LBPoolData")) {
         pool.zoneId(attributes.get("zoneid"));
      } else if (equalsOrSuffix(qName, "PoolData")) {
         pool.id(attributes.get("PoolId")).name(attributes.get("description")).dname(attributes.get("PoolDName"));
         pool.statusCode(Integer.parseInt(attributes.get("PoolStatus")));
         pool.failOverEnabled("Enabled".equalsIgnoreCase(attributes.get("FailOver")));
         pool.probingEnabled("Enabled".equalsIgnoreCase(attributes.get("Probing")));
         // MaxActiveServers is always 0 for TC
      }
   }
}
