/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.googlecompute.config;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.domain.Project;
import org.jclouds.json.config.GsonModule;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.json.ClaimSetTypeAdapter;
import org.jclouds.oauth.v2.json.HeaderTypeAdapter;

import javax.inject.Singleton;
import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author David Alves
 */
public class GoogleComputeParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(GsonModule.DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
   }

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return ImmutableMap.<Type, Object>of(
              Metadata.class, new MetadataTypeAdapter(),
              Operation.class, new OperationTypeAdapter(),
              Header.class, new HeaderTypeAdapter(),
              ClaimSet.class, new ClaimSetTypeAdapter(),
              Project.class, new ProjectTypeAdapter()
      );
   }

   /**
    * Parser for operations that unwraps errors avoiding an extra intermediate object.
    *
    * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/operations"/>
    */
   @Singleton
   private static class OperationTypeAdapter implements JsonDeserializer<Operation> {

      @Override
      public Operation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         Operation.Builder operationBuilder = ((Operation) context.deserialize(json,
                 OperationInternal.class)).toBuilder();
         JsonObject error = json.getAsJsonObject().getAsJsonObject("error");
         if (error != null) {
            JsonArray array = error.getAsJsonArray("errors");
            if (array != null) {
               for (JsonElement element : array) {
                  operationBuilder.addError((Operation.Error) context.deserialize(element, Operation.Error.class));
               }
            }
         }
         return operationBuilder.build();
      }

      private static class OperationInternal extends Operation {
         @ConstructorProperties({
                 "id", "creationTimestamp", "selfLink", "name", "description", "targetLink", "targetId",
                 "clientOperationId", "status", "statusMessage", "user", "progress", "insertTime", "startTime",
                 "endTime", "httpErrorStatusCode", "httpErrorMessage", "operationType"
         })
         private OperationInternal(String id, Date creationTimestamp, URI selfLink, String name,
                                   String description, URI targetLink, String targetId, String clientOperationId,
                                   Status status, String statusMessage, String user, int progress, Date insertTime,
                                   Date startTime, Date endTime, int httpErrorStatusCode, String httpErrorMessage,
                                   String operationType) {
            super(id, creationTimestamp, selfLink, name, description, targetLink, targetId, clientOperationId,
                    status, statusMessage, user, progress, insertTime, startTime, endTime, httpErrorStatusCode,
                    httpErrorMessage, operationType, null);
         }
      }
   }

   /**
    * Parser for Metadata.
    */
   @Singleton
   private static class MetadataTypeAdapter implements JsonDeserializer<Metadata>, JsonSerializer<Metadata> {


      @Override
      public Metadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
         JsonObject metadata = json.getAsJsonObject();
         JsonArray items = metadata.getAsJsonArray("items");
         if (items != null) {
            for (JsonElement element : items) {
               JsonObject object = element.getAsJsonObject();
               builder.put(object.get("key").getAsString(), object.get("value").getAsString());
            }
         }
         return new Metadata(builder.build());
      }

      @Override
      public JsonElement serialize(Metadata src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject metadataObject = new JsonObject();
         metadataObject.add("kind", new JsonPrimitive("compute#metadata"));
         JsonArray items = new JsonArray();
         for (Map.Entry<String, String> entry : src.entrySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("key", entry.getKey());
            object.addProperty("value", entry.getValue());
            items.add(object);
         }
         metadataObject.add("items", items);
         return metadataObject;
      }
   }

   public static class Metadata extends ForwardingMap<String, String> {

      private final Map<String, String> delegate;

      public Metadata(Map<String, String> delegate) {
         this.delegate = delegate;
      }

      @Override
      protected Map<String, String> delegate() {
         return delegate;
      }
   }

   @Singleton
   private static class ProjectTypeAdapter implements JsonDeserializer<Project> {

      @Override
      public Project deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         return Project.builder().fromProject((Project) context.deserialize(json, ProjectInternal.class)).build();
      }

      private static class ProjectInternal extends Project {

         @ConstructorProperties({
                 "id", "creationTimestamp", "selfLink", "name", "description", "commonInstanceMetadata", "quotas",
                 "externalIpAddresses"
         })
         private ProjectInternal(String id, Date creationTimestamp, URI selfLink, String name, String description,
                                 Metadata commonInstanceMetadata, Set<Quota> quotas, Set<String> externalIpAddresses) {
            super(id, creationTimestamp, selfLink, name, description, commonInstanceMetadata, quotas,
                    externalIpAddresses);
         }

      }
   }
}
