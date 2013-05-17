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
import static org.jclouds.rackspace.clouddns.v1.functions.ParseRecord.toRecordDetails;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.functions.ParseRecord.RawRecord;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
public class ParseOnlyRecord implements Function<HttpResponse, RecordDetail> {

   private final ParseJson<Map<String, List<RawRecord>>> json;

   @Inject
   ParseOnlyRecord(ParseJson<Map<String, List<RawRecord>>> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public RecordDetail apply(HttpResponse response) {
      Map<String, List<RawRecord>> records = json.apply(response);

      if (records == null)
         return null;
      
      RawRecord rawRecord = Iterables.getOnlyElement(records.get("records"));

      return toRecordDetails.apply(rawRecord);
   }
}
