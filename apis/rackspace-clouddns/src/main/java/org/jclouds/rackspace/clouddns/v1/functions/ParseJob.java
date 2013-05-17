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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.functions.ParseDomain.RawDomain;
import org.jclouds.rackspace.clouddns.v1.functions.ParseRecord.RawRecord;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;

/**
 * @author Everett Toews
 */
public class ParseJob implements Function<HttpResponse, Job<?>> {

   private final ParseJson<RawJob> parseJson;
   private final Json json;
   private boolean isCreateSingleRecord;

   @Inject
   ParseJob(Json json, ParseJson<RawJob> parseJson) {
      this.json = checkNotNull(json, "json");
      this.parseJson = checkNotNull(parseJson, "parseJson");
   }

   @Override
   public Job<?> apply(HttpResponse response) {
      RawJob rawJob = parseJson.apply(response);

      if (rawJob == null)
         return null;

      return toJob(rawJob);
   }

   public Job<?> toJob(RawJob in) {
      return Job.builder().id(in.jobId).status(in.status).error(in.error)
            .resource(parseResponse(in.requestUrl, in.response)).build();
   }

   protected Object parseResponse(String requestUrl, JsonBall response) {
      if (response == null) {
         return null;
      }
      else if (requestUrl.contains("import")) {
         Type type = new TypeLiteral<Map<String, Set<ParseDomain.RawDomain>>>() { }.getType();
         Map<String, Set<RawDomain>> domainMap = json.fromJson(response.toString(), type);
         Domain domain = Iterators.getOnlyElement(domainMap.get("domains").iterator()).getDomain();

         return domain;
      }
      else if (requestUrl.contains("export")) {
         Type type = new TypeLiteral<Map<String, String>>() { }.getType();
         Map<String, String> exportMap = json.fromJson(response.toString(), type);
         String contents = exportMap.get("contents");
         List<String> contentsAsList = Lists.newArrayList(Splitter.on("\n").omitEmptyStrings().split(contents));

         return contentsAsList;
      }
      else if (response.toString().contains("domains")) {
         Type type = new TypeLiteral<Map<String, Set<RawDomain>>>() { }.getType();
         Map<String, Set<RawDomain>> domainMap = json.fromJson(response.toString(), type);
         Set<Domain> domains = FluentIterable.from(domainMap.get("domains")).transform(toDomain).toSet();

         return domains;
      }
      else if (response.toString().contains("records")) {
         Type type = new TypeLiteral<Map<String, Set<RawRecord>>>() { }.getType();
         Map<String, Set<RawRecord>> recordMap = json.fromJson(response.toString(), type);
         Set<RecordDetail> records = FluentIterable.from(recordMap.get("records")).transform(toRecordDetails).toSet();
         
         if (isCreateSingleRecord) {
            return Iterables.getOnlyElement(records);
         } else {
            return records;
         }
      }
      else {
         throw new IllegalStateException("Job parsing problem. Did not recognize any type in job response.\n"
               + response.toString());
      }
   }

   private static class RawJob {
      private String jobId;
      private Job.Status status;
      private Job.Error error;
      private String requestUrl;
      private JsonBall response;
   }

   private static final Function<RawDomain, Domain> toDomain = new Function<RawDomain, Domain>() {
      public Domain apply(RawDomain domain) {
         return domain.getDomain();
      }
   };
}
