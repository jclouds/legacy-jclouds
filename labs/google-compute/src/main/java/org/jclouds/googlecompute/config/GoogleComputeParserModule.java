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
import com.google.common.collect.Range;
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
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.InstanceTemplate;
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

import static org.jclouds.googlecompute.domain.Firewall.Rule;

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
      return new ImmutableMap.Builder<Type, Object>()
              .put(Metadata.class, new MetadataTypeAdapter())
              .put(Operation.class, new OperationTypeAdapter())
              .put(Header.class, new HeaderTypeAdapter())
              .put(ClaimSet.class, new ClaimSetTypeAdapter())
              .put(Project.class, new ProjectTypeAdapter())
              .put(Instance.class, new InstanceTypeAdapter())
              .put(InstanceTemplate.class, new InstanceTemplateTypeAdapter())
              .put(Rule.class, new RuleTypeAdapter())
              .build();
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

   @Singleton
   private static class InstanceTemplateTypeAdapter implements JsonSerializer<InstanceTemplate> {

      @Override
      public JsonElement serialize(InstanceTemplate src, Type typeOfSrc, JsonSerializationContext context) {
         InstanceTemplateInternal template = new InstanceTemplateInternal(src);
         JsonObject instance = (JsonObject) context.serialize(template, InstanceTemplateInternal.class);

         // deal with network
         JsonArray networkInterfaces = new JsonArray();
         networkInterfaces.add(context.serialize(src.getNetwork(), InstanceTemplate.Network.class));
         instance.add("networkInterfaces", networkInterfaces);

         // deal with persistent disks
         if (src.getDisks() != null && !src.getDisks().isEmpty()) {
            JsonArray disks = new JsonArray();
            for (InstanceTemplate.PersistentDisk persistentDisk : src.getDisks()) {
               JsonObject disk = (JsonObject) context.serialize(persistentDisk, InstanceTemplate.PersistentDisk.class);
               disk.addProperty("type", "PERSISTENT");
               disks.add(disk);
            }
            instance.add("disks", disks);
         }

         // deal with metadata
         if (src.getMetadata() != null && !src.getMetadata().isEmpty()) {
            JsonObject metadata = (JsonObject) context.serialize(new Metadata(src.getMetadata()));
            instance.add("metadata", metadata);
            return instance;
         }

         return instance;
      }

      private static class InstanceTemplateInternal extends InstanceTemplate {
         private InstanceTemplateInternal(InstanceTemplate template) {
            super(template.getMachineType());
            name(template.getName());
            description(template.getDescription());
            zone(template.getZone());
            image(template.getImage());
            tags(template.getTags());
            serviceAccounts(template.getServiceAccounts());
         }
      }
   }

   @Singleton
   private static class InstanceTypeAdapter implements JsonDeserializer<Instance> {

      @Override
      public Instance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         Instance.Builder instanceBuilder = ((Instance) context.deserialize(json,
                 InstanceInternal.class)).toBuilder();
         JsonObject object = (JsonObject) json;
         if (object.get("disks") != null) {
            JsonArray disks = (JsonArray) object.get("disks");
            for (JsonElement element : disks) {
               JsonObject disk = (JsonObject) element;
               if (disk.get("type").getAsString().equals("PERSISTENT")) {
                  instanceBuilder.addDisk((Instance.PersistentAttachedDisk) context.deserialize(disk,
                          Instance.PersistentAttachedDisk.class));
               } else {
                  instanceBuilder.addDisk((Instance.AttachedDisk) context.deserialize(disk,
                          Instance.AttachedDisk.class));
               }
            }

         }

         return Instance.builder().fromInstance(instanceBuilder.build()).build();
      }


      private static class InstanceInternal extends Instance {
         @ConstructorProperties({
                 "id", "creationTimestamp", "selfLink", "name", "description", "tags", "image", "machineType",
                 "status", "statusMessage", "zone", "networkInterfaces", "metadata", "serviceAccounts"
         })
         private InstanceInternal(String id, Date creationTimestamp, URI selfLink, String name, String description,
                                  Set<String> tags, URI image, URI machineType, Status status, String statusMessage,
                                  URI zone, Set<NetworkInterface> networkInterfaces, Metadata metadata,
                                  Set<ServiceAccount> serviceAccounts) {
            super(id, creationTimestamp, selfLink, name, description, tags, image, machineType,
                    status, statusMessage, zone, networkInterfaces, null, metadata, serviceAccounts);
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

   private static class RuleTypeAdapter implements JsonDeserializer<Firewall.Rule>, JsonSerializer<Firewall.Rule> {

      @Override
      public Firewall.Rule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         JsonObject rule = json.getAsJsonObject();
         Rule.Builder builder = Rule.builder();
         builder.IPProtocol(Rule.IPProtocol.fromValue(rule.get("IPProtocol").getAsString()));
         if (rule.get("ports") != null) {
            JsonArray ports = (JsonArray) rule.get("ports");
            for (JsonElement port : ports) {
               String portAsString = port.getAsString();
               if (portAsString.contains("-")) {
                  String[] split = portAsString.split("-");
                  builder.addPortRange(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
               } else {
                  builder.addPort(Integer.parseInt(portAsString));
               }
            }
         }
         return builder.build();
      }

      @Override
      public JsonElement serialize(Firewall.Rule src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject ruleObject = new JsonObject();
         ruleObject.addProperty("IPProtocol", src.getIPProtocol().value());
         if (src.getPorts() != null && !src.getPorts().isEmpty()) {
            JsonArray ports = new JsonArray();
            for (Range<Integer> range : src.getPorts().asRanges()) {
               ports.add(new JsonPrimitive(range.lowerEndpoint() == range.upperEndpoint() ? range.lowerEndpoint() + "" :
                       range.lowerEndpoint() + "-" + range.upperEndpoint()));
            }
            ruleObject.add("ports", ports);
         }
         return ruleObject;
      }
   }
}
