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

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ResourceRecordDetail;
import org.xml.sax.Attributes;

import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public class ResourceRecordDetailHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<ResourceRecordDetail> {
   private final DateService dateService;

   @Inject
   private ResourceRecordDetailHandler(DateService dateService) {
      this.dateService = dateService;
   }

   private ResourceRecordDetail.Builder rrm = ResourceRecordDetail.builder();
   private ResourceRecord.Builder rr = ResourceRecord.rrBuilder();

   @Override
   public ResourceRecordDetail getResult() {
      try {
         return rrm.record(rr.build()).build();
      } finally {
         rrm = ResourceRecordDetail.builder();
         rr = ResourceRecord.rrBuilder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "ResourceRecord")) {
         rrm.zoneId(attributes.get("ZoneId"));
         rrm.guid(attributes.get("Guid"));
         rrm.zoneName(attributes.get("ZoneName"));
         rrm.created(dateService.iso8601DateParse(attributes.get("Created")));
         rrm.modified(dateService.iso8601DateParse(attributes.get("Modified")));
         rr.type(Integer.parseInt(attributes.get("Type")));
         rr.name(attributes.get("DName"));
         rr.ttl(Integer.parseInt(attributes.get("TTL")));
      } else if (equalsOrSuffix(qName, "InfoValues")) {
         rr.rdata(attributes.values());
      }
   }
}
