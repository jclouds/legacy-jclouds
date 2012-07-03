/**
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
package org.jclouds.openstack.nova.v2_0.config;

import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedAttributes;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;
import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 */
public class NovaParserModule extends AbstractModule {

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return ImmutableMap.<Type, Object>of(
            HostResourceUsage.class, new HostResourceUsageAdapter(),
            ServerWithSecurityGroups.class, new ServerWithSecurityGroupsAdapter(),
            Server.class, new ServerAdapter()
      );
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
   }

   @Singleton
   public static class HostResourceUsageAdapter implements JsonSerializer<HostResourceUsage>, JsonDeserializer<HostResourceUsage> {
      public HostResourceUsage apply(HostResourceUsageView in) {
         return in.resource.toBuilder().build();
      }

      @Override
      public HostResourceUsage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
         return apply((HostResourceUsageView) context.deserialize(jsonElement, HostResourceUsageView.class));
      }

      @Override
      public JsonElement serialize(HostResourceUsage hostResourceUsage, Type type, JsonSerializationContext context) {
         return context.serialize(hostResourceUsage);
      }

      private static class HostResourceUsageView {
         protected HostResourceUsageInternal resource;
      }

      private static class HostResourceUsageInternal extends HostResourceUsage {

         @ConstructorProperties({
               "host", "project", "memory_mb", "cpu", "disk_gb"
         })
         protected HostResourceUsageInternal(String host, @Nullable String project, int memoryMb, int cpu, int diskGb) {
            super(host, project, memoryMb, cpu, diskGb);
         }
      }
   }

   @Singleton
   public static class ServerWithSecurityGroupsAdapter implements JsonDeserializer<ServerWithSecurityGroups> {
      @Override
      public ServerWithSecurityGroups deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         Server server = context.deserialize(jsonElement, Server.class);
         ServerWithSecurityGroups.Builder result = ServerWithSecurityGroups.builder().fromServer(server);
         Set<String> names = Sets.newLinkedHashSet();
         if (jsonElement.getAsJsonObject().get("security_groups") != null) {
            JsonArray x = jsonElement.getAsJsonObject().get("security_groups").getAsJsonArray();
            for (JsonElement y : x) {
               names.add(y.getAsJsonObject().get("name").getAsString());
            }
            result.securityGroupNames(names);
         }
         return result.build();
      }
   }

   @Singleton
   public static class ServerAdapter implements JsonDeserializer<Server> {
      @Override
      public Server deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         Server serverBase = apply((ServerInternal) context.deserialize(jsonElement, ServerInternal.class));
         Server.Builder result = Server.builder().fromServer(serverBase);
         ServerExtendedStatus extendedStatus = context.deserialize(jsonElement, ServerExtendedStatus.class);
         if (!Objects.equal(extendedStatus, ServerExtendedStatus.builder().build())) {
            result.extendedStatus(extendedStatus);
         }
         ServerExtendedAttributes extraAttributes = context.deserialize(jsonElement, ServerExtendedAttributes.class);
         if (!Objects.equal(extraAttributes, ServerExtendedAttributes.builder().build())) {
            result.extendedAttributes(extraAttributes);
         }
         return result.build();
      }

      public Server apply(ServerInternal in) {
         return in.toBuilder().build();
      }

      private static class ServerInternal extends Server {
         @ConstructorProperties({
               "id", "name", "links", "uuid", "tenant_id", "user_id", "updated", "created", "hostId", "accessIPv4", "accessIPv6", "status", "image", "flavor", "key_name", "config_drive", "addresses", "metadata", "extendedStatus", "extendedAttributes", "OS-DCF:diskConfig"
         })
         protected ServerInternal(String id, @Nullable String name, java.util.Set<Link> links, @Nullable String uuid, String tenantId,
                                  String userId, Date updated, Date created, @Nullable String hostId, @Nullable String accessIPv4,
                                  @Nullable String accessIPv6, Server.Status status, Resource image, Resource flavor, @Nullable String keyName,
                                  @Nullable String configDrive, Multimap<String, Address> addresses, Map<String, String> metadata,
                                  @Nullable ServerExtendedStatus extendedStatus, @Nullable ServerExtendedAttributes extendedAttributes, @Nullable String diskConfig) {
            super(id, name, links, uuid, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6, status, image, flavor, keyName, configDrive, addresses, metadata, extendedStatus, extendedAttributes, diskConfig);
         }
      }
   }
}
