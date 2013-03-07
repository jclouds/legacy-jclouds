/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
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
package org.jclouds.ultradns.ws.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.PoolRecord;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public class PoolRecordListHandler extends ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<PoolRecord>> {

   private final PoolRecordHandler zoneHandler;

   private Builder<PoolRecord> zones = ImmutableSet.<PoolRecord> builder();

   @Inject
   public PoolRecordListHandler(PoolRecordHandler zoneHandler) {
      this.zoneHandler = zoneHandler;
   }

   @Override
   public FluentIterable<PoolRecord> getResult() {
      return FluentIterable.from(zones.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "PoolRecordData")) {
         zoneHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "PoolRecordData")) {
         zones.add(zoneHandler.getResult());
      }
   }
}
