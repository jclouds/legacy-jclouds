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
package org.jclouds.ultradns.ws.binders;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.ultradns.ws.domain.DirectionalGroup;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;

/**
 * 
 * @author Adrian Cole
 */
public class UpdateDirectionalDNSGroupToXML implements MapBinder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      DirectionalGroup group = DirectionalGroup.class.cast(postParams.get("group"));
      String xml = toXML(postParams.get("dirPoolRecordId"), group);
      return (R) request.toBuilder().payload(xml).build();
   }

   private static final String template = "<v01:updateDirectionalDNSGroup><transactionID /><dirPoolRecordId>%s</dirPoolRecordId>%s</v01:updateDirectionalDNSGroup>";

   @VisibleForTesting
   static String toXML(Object dirPoolRecordId, DirectionalGroup group) {
      return format(template, dirPoolRecordId, geo(group));
   }

   private static String geo(DirectionalGroup group) {
      StringBuilder groupData = new StringBuilder();
      groupData.append("<DirectionalDNSGroupDetail GroupName=\"").append(group.getName()).append('"');
      if (group.getDescription().isPresent())
         groupData.append(" Description=\"").append(group.getDescription().get()).append('"');
      groupData.append(" ><DirectionalDNSRegion>");
      for (Entry<String, Collection<String>> region : group.asMap().entrySet()) {
         groupData.append("<RegionForNewGroups RegionName=\"").append(region.getKey()).append('"');
         groupData.append(" TerritoryName=\"").append(Joiner.on(';').join(region.getValue())).append("\" />");
      }
      groupData.append("</DirectionalDNSRegion></DirectionalDNSGroupDetail>");
      return groupData.toString();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("use map form");
   }
}
