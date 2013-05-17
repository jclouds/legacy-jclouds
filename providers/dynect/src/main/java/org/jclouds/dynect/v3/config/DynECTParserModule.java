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
package org.jclouds.dynect.v3.config;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.dynect.v3.domain.GeoService;
import org.jclouds.dynect.v3.domain.Node;
import org.jclouds.dynect.v3.domain.RecordSet;
import org.jclouds.dynect.v3.domain.RecordSet.Value;
import org.jclouds.dynect.v3.domain.RecordSet.Value.Builder;
import org.jclouds.dynect.v3.domain.GeoRegionGroup;
import org.jclouds.dynect.v3.domain.SessionCredentials;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
public class DynECTParserModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return new ImmutableMap.Builder<Type, Object>()
            .put(SessionCredentials.class, new SessionCredentialsTypeAdapter())
            .put(GeoRegionGroup.class, new GeoRegionGroupTypeAdapter())
            .put(GeoService.class, new GeoServiceTypeAdapter()).build();
   }

   private static class SessionCredentialsTypeAdapter implements JsonSerializer<SessionCredentials> {
      public JsonElement serialize(SessionCredentials src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject metadataObject = new JsonObject();
         metadataObject.addProperty("customer_name", src.getCustomerName());
         metadataObject.addProperty("user_name", src.getUserName());
         metadataObject.addProperty("password", src.getPassword());
         return metadataObject;
      }
   }

   private static class GeoRegionGroupTypeAdapter implements JsonDeserializer<GeoRegionGroup> {

      @Override
      public GeoRegionGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         CreepyGeoRegionGroup creepyGeoRegionGroup = context.deserialize(json, CreepyGeoRegionGroup.class);
         GeoRegionGroup.Builder builder = GeoRegionGroup.builder();
         builder.name(creepyGeoRegionGroup.name);
         builder.serviceName(creepyGeoRegionGroup.service_name);
         builder.countries(creepyGeoRegionGroup.countries);
         ImmutableList.Builder<RecordSet> rsets = ImmutableList.builder();
         for (Entry<String, List<Map<String, Object>>> entry : creepyGeoRegionGroup.rdata.entrySet()) {
            if (entry.getValue().isEmpty())
               continue;
            // ex. spf_rdata -> SPF
            String type = entry.getKey().substring(0, entry.getKey().indexOf('_')).toUpperCase();
            // ex. dhcid_ttl
            int ttl = creepyGeoRegionGroup.ttl.get(type.toLowerCase() + "_ttl");
            RecordSet.Builder rset = RecordSet.builder();
            rset.type(type);
            rset.ttl(ttl);

            // weight is only present for a couple record types
            List<Integer> weights = creepyGeoRegionGroup.weight.get(type.toLowerCase() + "_weight");
            if (weights == null)
               weights = ImmutableList.of();

            List<String> labels = creepyGeoRegionGroup.label.get(type.toLowerCase() + "_label");
            for (int i = 0; i < entry.getValue().size(); i++) {
               Builder elementBuilder = Value.builder().rdata(entry.getValue().get(i));
               // chance of index out of bounds
               if (i < labels.size())
                  elementBuilder.label(labels.get(i));
               if (i < weights.size())
                  elementBuilder.weight(weights.get(i));
               rset.add(elementBuilder.build());
            }
            rsets.add(rset.build());
         }
         builder.recordSets(rsets.build());
         return builder.build();
      }
   }

   private static class CreepyGeoRegionGroup {
      String name;
      // aaaa_weight
      Map<String, List<Integer>> weight;
      List<String> countries;
      String service_name;
      // spf_rdata
      Map<String, List<Map<String, Object>>> rdata;
      // a_label
      Map<String, List<String>> label;
      // dhcid_ttl
      Map<String, Integer> ttl;
   }
   

   private static class GeoServiceTypeAdapter implements JsonDeserializer<GeoService> {

      @Override
      public GeoService deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         CreepyGeoService creepyGeoService = context.deserialize(json, CreepyGeoService.class);
         GeoService.Builder builder = GeoService.builder();
         builder.name(creepyGeoService.name);
         builder.active("Y".equals(creepyGeoService.active));
         builder.ttl(creepyGeoService.ttl);
         builder.nodes(creepyGeoService.nodes);
         builder.groups(creepyGeoService.groups);
         return builder.build();
      }
   }

   private static class CreepyGeoService {
      String name;
      String active;// creepy part
      int ttl;
      List<Node> nodes;
      List<GeoRegionGroup> groups;
   }
}
