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

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import static org.jclouds.ultradns.ws.domain.IdAndName.*;
import org.jclouds.ultradns.ws.domain.DirectionalRecord;
import org.jclouds.ultradns.ws.domain.DirectionalRecordDetail;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class DirectionalRecordDetailHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<DirectionalRecordDetail> {

   private DirectionalRecordDetail.Builder drd = DirectionalRecordDetail.builder();
   private DirectionalRecord.Builder dr = DirectionalRecord.drBuilder();

   private String zoneName;
   private String dname;

   @Override
   public DirectionalRecordDetail getResult() {
      try {
         return drd.record(dr.build()).build();
      } finally {
         drd = DirectionalRecordDetail.builder().zoneName(zoneName).name(dname);
         dr = DirectionalRecord.drBuilder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (attributes.containsKey("ZoneName")) {
         zoneName = attributes.get("ZoneName");
         dname = attributes.get("DName");
         drd.zoneName(zoneName).name(dname);
      }
      if (attributes.containsKey("DirPoolRecordId")) {
         drd.id(attributes.get("DirPoolRecordId"));
      }
      if (attributes.containsKey("GroupId")) {
         drd.group(fromIdAndName(attributes.get("GroupId"), attributes.get("GroupName")));
      }
      if (attributes.containsKey("GeolocationGroupId")) {
         drd.geolocationGroup(fromIdAndName(attributes.get("GeolocationGroupId"),
               attributes.get("GeolocationGroupName")));
      }
      if (attributes.containsKey("SourceIPGroupId")) {
         drd.sourceIpGroup(fromIdAndName(attributes.get("SourceIPGroupId"), attributes.get("SourceIPGroupName")));
      }
      if (attributes.containsKey("recordType")) {
         dr.type(attributes.get("recordType"));
         dr.ttl(Integer.parseInt(attributes.get("TTL")));
         dr.noResponseRecord("true".equalsIgnoreCase(attributes.get("noResponseRecord")));
      }
      if (equalsOrSuffix(qName, "InfoValues")) {
         dr.rdata(attributes.values());
      }
   }
}
