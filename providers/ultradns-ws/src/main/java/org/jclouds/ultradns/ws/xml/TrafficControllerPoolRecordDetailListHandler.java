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
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail.Status;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class TrafficControllerPoolRecordDetailListHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<TrafficControllerPoolRecordDetail>> {

   private final Builder<TrafficControllerPoolRecordDetail> records = ImmutableSet.<TrafficControllerPoolRecordDetail> builder();

   @Override
   public FluentIterable<TrafficControllerPoolRecordDetail> getResult() {
      return FluentIterable.from(records.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attrs) {
      if (!equalsOrSuffix(qName, "PoolRecordData"))
         return;
      Map<String, String> attributes = cleanseAttributes(attrs);
      TrafficControllerPoolRecord record = TrafficControllerPoolRecord.create(
            attributes.get("recordType"),
            attributes.get("pointsTo"));
      records.add(TrafficControllerPoolRecordDetail.builder()
                                             .id(attributes.get("poolRecordID"))
                                             .poolId(attributes.get("poolId"))
                                             .record(record)
                                             .weight(parseInt(checkNotNull(attributes.get("weight"), "weight")))
                                             .priority(parseInt(checkNotNull(attributes.get("priority"), "priority")))
                                             .forceAnswer(attributes.get("forceAnswer"))
                                             .probingEnabled("ENABLED".equalsIgnoreCase(attributes.get("probing")))
                                             .status(Status.valueOf(attributes.get("status")))
                                             .serving("Yes".equalsIgnoreCase(attributes.get("serving")))
                                             .description(attributes.get("description")).build());
   }
}
