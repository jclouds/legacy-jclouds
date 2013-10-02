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
import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.Zone.DNSSECStatus;
import org.xml.sax.Attributes;


/**
 * 
 * @author Adrian Cole
 */
public class ZoneHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Zone> {

   private Zone zone;

   @Override
   public Zone getResult() {
      try {
         return zone;
      } finally {
         zone = null;
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "UltraZone")) {
         zone = Zone.builder()
                    .id(attributes.get("zoneId"))
                    .name(attributes.get("zoneName"))
                    .typeCode(Integer.parseInt(checkNotNull(attributes.get("zoneType"), "zoneType")))
                    .accountId(attributes.get("accountId"))
                    .ownerId(attributes.get("owner"))
                    .dnssecStatus(DNSSECStatus.fromValue(attributes.get("dnssecStatus")))
                    .primarySrc(attributes.get("primarySrc")).build();
      }
   }
}
