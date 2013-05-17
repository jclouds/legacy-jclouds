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
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecord;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;

/**
 * 
 * @author Adrian Cole
 */
public class DirectionalRecordAndGeoGroupToXML implements MapBinder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      DirectionalPoolRecord record = DirectionalPoolRecord.class.cast(postParams.get("record"));
      DirectionalGroup group = DirectionalGroup.class.cast(postParams.get("group"));
      String xml = toXML(postParams.get("poolId"), record, group, postParams.get("dirPoolRecordId"),
            postParams.get("groupId"));
      return (R) request.toBuilder().payload(xml).build();
   }

   private static final String ADD_TEMPLATE = "<v01:addDirectionalPoolRecord><transactionID />%s%s%s</AddDirectionalRecordData></v01:addDirectionalPoolRecord>";
   private static final String UPDATE_TEMPLATE = "<v01:updateDirectionalPoolRecord><transactionID /><UpdateDirectionalRecordData directionalPoolRecordId=\"%s\">%s%s</UpdateDirectionalRecordData></v01:updateDirectionalPoolRecord>";
   private static final String NEWGROUP_TEMPLATE = "<GeolocationGroupData><GroupData groupingType=\"DEFINE_NEW_GROUP\" />%s</GeolocationGroupData>";
   private static final String EXISTINGGROUP_TEMPLATE = "<GeolocationGroupData><GroupData groupingType=\"ASSIGN_EXISTING_GROUP\" assignExistingGroupId=\"%s\" />%s</GeolocationGroupData>";

   @VisibleForTesting
   static String toXML(Object poolId, DirectionalPoolRecord record, DirectionalGroup group, Object recordId,
         Object groupId) {
      if (poolId == null) {
         if (group != null)
            return format(UPDATE_TEMPLATE, recordId, updateRecord(record), geo(group));
         return format(UPDATE_TEMPLATE, recordId, updateRecord(record), "");
      }
      if (group == null && groupId == null) {
         return format(
               ADD_TEMPLATE,
               format("<AddDirectionalRecordData directionalPoolId=\"%s\" createAllNonConfiguredGrp=\"true\">", poolId),
               createRecord(record), "");
      }
      String addRecordToPool = format("<AddDirectionalRecordData directionalPoolId=\"%s\">", poolId);
      if (groupId != null) {
         return format(ADD_TEMPLATE, addRecordToPool, createRecord(record), format(EXISTINGGROUP_TEMPLATE, groupId, ""));
      }
      return format(ADD_TEMPLATE, addRecordToPool, createRecord(record), format(NEWGROUP_TEMPLATE, geo(group)));
   }

   private static String createRecord(DirectionalPoolRecord record) {
      StringBuilder recordConfig = new StringBuilder();
      recordConfig.append("<DirectionalRecordConfiguration recordType=\"").append(record.getType()).append('"');
      recordConfig.append(" TTL=\"").append(record.getTTL()).append('"');
      recordConfig.append(" noResponseRecord=\"").append(record.isNoResponseRecord()).append("\" >");
      recordConfig.append(values(record));
      recordConfig.append("</DirectionalRecordConfiguration>");
      return recordConfig.toString();
   }

   /**
    * don't pass type or is no response when updating
    */
   private static String updateRecord(DirectionalPoolRecord record) {
      return format("<DirectionalRecordConfiguration TTL=\"%s\" >%s</DirectionalRecordConfiguration>", record.getTTL(),
            values(record));
   }

   private static String values(DirectionalPoolRecord record) {
      StringBuilder values = new StringBuilder("<InfoValues");
      for (int i = 0; i < record.getRData().size(); i++) {
         values.append(' ').append("Info").append(i + 1).append("Value=").append('"').append(record.getRData().get(i))
               .append('"');
      }
      values.append(" />");
      return values.toString();
   }

   private static String geo(DirectionalGroup group) {
      StringBuilder groupData = new StringBuilder();
      groupData.append("<GeolocationGroupDetails groupName=\"").append(group.getName()).append('"');
      if (group.getDescription().isPresent())
         groupData.append(" description=\"").append(group.getDescription().get()).append('"');
      groupData.append(" >");
      for (Entry<String, Collection<String>> region : group.asMap().entrySet()) {
         groupData.append("<GeolocationGroupDefinitionData regionName=\"").append(region.getKey()).append('"');
         groupData.append(" territoryNames=\"").append(Joiner.on(';').join(region.getValue())).append("\" />");
      }
      groupData.append("</GeolocationGroupDetails>");
      return groupData.toString();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("use map form");
   }
}
