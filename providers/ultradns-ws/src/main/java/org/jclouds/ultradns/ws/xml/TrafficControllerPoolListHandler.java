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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public class TrafficControllerPoolListHandler extends ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<TrafficControllerPool>> {

   private final TrafficControllerPoolHandler poolHandler;

   private final Builder<TrafficControllerPool> pools = ImmutableSet.<TrafficControllerPool> builder();

   @Inject
   public TrafficControllerPoolListHandler(TrafficControllerPoolHandler poolHandler) {
      this.poolHandler = poolHandler;
   }

   @Override
   public FluentIterable<TrafficControllerPool> getResult() {
      return FluentIterable.from(pools.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "LBPoolData") || equalsOrSuffix(qName, "PoolData")) {
         poolHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "LBPoolData")) {
         pools.add(poolHandler.getResult());
      }
   }
}
