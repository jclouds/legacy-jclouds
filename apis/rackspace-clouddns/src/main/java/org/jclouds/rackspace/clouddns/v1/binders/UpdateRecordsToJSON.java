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
package org.jclouds.rackspace.clouddns.v1.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Binds the Records to the request as a JSON payload.
 * 
 * @author Everett Toews
 */
@Singleton
public class UpdateRecordsToJSON implements Binder {

   private final Json jsonBinder;

   @Inject
   public UpdateRecordsToJSON(Json jsonBinder) {
      this.jsonBinder = checkNotNull(jsonBinder, "jsonBinder");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Map, "This binder is only valid for Map<String, Record>");
      checkNotNull(request, "request");

      Map<String, Record> idsToRecords = (Map<String, Record>) input;      
      List<UpdateRecord> updateRecords = toUpdateRecordList(idsToRecords);
      
      String json = jsonBinder.toJson(ImmutableMap.of("records", updateRecords));
      request.setPayload(json);
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_JSON);
      
      return request;
   }
   
   static List<UpdateRecord> toUpdateRecordList(Map<String, Record> idsToRecords) {
      List<UpdateRecord> updateRecords = Lists.newArrayList();

      for (String recordId: idsToRecords.keySet()) {
         Record record = idsToRecords.get(recordId);
         
         UpdateRecord updateRecord = new UpdateRecord();
         updateRecord.id = recordId;
         updateRecord.name = record.getName();
         updateRecord.ttl = record.getTTL().isPresent() ? record.getTTL().get() : null;
         updateRecord.data = record.getData();
         updateRecord.priority = record.getPriority();
         updateRecord.comment = record.getComment();
         
         updateRecords.add(updateRecord);
      }
      
      return updateRecords;
   }
   
   static final class UpdateRecord {
      public String id;
      public String name;
      public Integer ttl;
      public String data;
      public Integer priority;
      public String comment;
   }
}
