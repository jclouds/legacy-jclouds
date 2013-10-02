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

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rackspace.clouddns.v1.binders.UpdateRecordsToJSON.UpdateRecord;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rest.MapBinder;

import com.google.common.collect.ImmutableMap;

/**
 * @author Everett Toews
 */
public class UpdateReverseDNSToJSON implements MapBinder {
   private final Json jsonBinder;

   @Inject
   public UpdateReverseDNSToJSON(Json jsonBinder) {
      this.jsonBinder = checkNotNull(jsonBinder, "jsonBinder");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(postParams.get("href"), "href") instanceof URI,
            "href is only valid for a URI!");
      checkArgument(checkNotNull(postParams.get("idsToRecords"), "idsToRecords") instanceof Map,
            "records is only valid for a Map!");
      checkNotNull(postParams.get("serviceName"), "serviceName");
      
      Map<String, Record> idsToRecords = Map.class.cast(postParams.get("idsToRecords"));
      List<UpdateRecord> updateRecords = UpdateRecordsToJSON.toUpdateRecordList(idsToRecords);
      URI deviceURI = URI.class.cast(postParams.get("href"));
      String serviceName = postParams.get("serviceName").toString();

      String json = toJSON(updateRecords, deviceURI, serviceName);
      request.setPayload(json);
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_JSON);

      return (R) request.toBuilder().payload(json).build();
   }

   private String toJSON(Iterable<UpdateRecord> records, URI deviceURI, String serviceName) {
      return jsonBinder.toJson(ImmutableMap.<String, Object> of(
            "recordsList", ImmutableMap.of("records", records),
            "link", ImmutableMap.<String, Object> of(
                  "href", deviceURI, 
                  "rel", serviceName)));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("use map form");
   }
}
