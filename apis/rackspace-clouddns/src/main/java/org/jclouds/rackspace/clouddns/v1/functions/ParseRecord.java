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
package org.jclouds.rackspace.clouddns.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import javax.inject.Inject;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;

import com.google.common.base.Function;

/**
 * @author Everett Toews
 */
public class ParseRecord implements Function<HttpResponse, RecordDetail> {

   private final ParseJson<RawRecord> json;

   @Inject
   ParseRecord(ParseJson<RawRecord> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public RecordDetail apply(HttpResponse response) {
      RawRecord rawRecord = json.apply(response);

      if (rawRecord == null)
         return null;

      return toRecordDetails.apply(rawRecord);
   }

   static class RawRecord {
      public String id;
      public String name;
      public String type;
      public int ttl;
      public String data;
      public Integer priority;
      public String comment;
      public Date created;
      public Date updated;

      @ConstructorProperties({ "id", "name", "type", "ttl", "data", "priority", "comment", "created", "updated" })
      protected RawRecord(String id, String name, String type, int ttl, String data, Integer priority, String comment,
            Date created, Date updated) {
         super();
         this.id = id;
         this.name = name;
         this.type = type;
         this.ttl = ttl;
         this.data = data;
         this.priority = priority;
         this.comment = comment;
         this.created = created;
         this.updated = updated;
      }
   }

   static final Function<RawRecord, RecordDetail> toRecordDetails = new Function<RawRecord, RecordDetail>() {
      @Override
      public RecordDetail apply(RawRecord rawRecord) {
         Record record = Record.builder().name(rawRecord.name).type(rawRecord.type).ttl(rawRecord.ttl)
               .data(rawRecord.data).priority(rawRecord.priority).comment(rawRecord.comment).build();
         RecordDetail recordDetails = RecordDetail.builder().id(rawRecord.id).created(rawRecord.created)
               .updated(rawRecord.updated).record(record).build();

         return recordDetails;
      }
   };
}
