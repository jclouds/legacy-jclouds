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

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.ultradns.ws.domain.ResourceRecord;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
public class ZoneAndResourceRecordToXML implements MapBinder {
   private static final String template = "<v01:createResourceRecord><transactionID /><resourceRecord ZoneName=\"%s\" Type=\"%s\" DName=\"%s\" TTL=\"%s\">%s</resourceRecord></v01:createResourceRecord>";

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String zoneName = postParams.get("zoneName").toString();
      ResourceRecord record = ResourceRecord.class.cast(postParams.get("resourceRecord"));
      String xml = toXML(zoneName, record);
      Optional<?> guid = Optional.fromNullable(postParams.get("guid"));
      if (guid.isPresent()) {
         xml = update(guid.get(), xml);
      }
      return (R) request.toBuilder().payload(xml).build();
   }

   @VisibleForTesting
   static String toXML(String zoneName, ResourceRecord record) {
      StringBuilder values = new StringBuilder("<InfoValues");
      for (int i = 0; i < record.getRData().size(); i++) {
         values.append(' ').append("Info").append(i + 1).append("Value=").append('"')
               .append(record.getRData().get(i)).append('"');
      }
      values.append(" />");
      return format(template, zoneName, record.getType(), record.getName(), record.getTTL(), values.toString());
   }

   static String update(Object guid, String xml) {
      return xml.replace("createResourceRecord", "updateResourceRecord").replace("<resourceRecord",
            format("<resourceRecord Guid=\"%s\"", guid));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("use map form");
   }
}
