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
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
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
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

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
      bind(new TypeLiteral<Set<TypeAdapterFactory>>() {
      }).toInstance(ImmutableSet.<TypeAdapterFactory>of(new SetTypeAdapterFactory(), new MapTypeAdapterFactory(), new MultimapTypeAdapterFactory()));
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

   /**
    * Eliminates nulls from within a set
    * <p/>
    * Treats [null] as the empty set; [A, null] as [A]; etc.
    */
   public static class SetTypeAdapterFactory implements TypeAdapterFactory {
      @SuppressWarnings("unchecked")
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Type type = typeToken.getType();
         if (typeToken.getRawType() != Set.class || !(type instanceof ParameterizedType)) {
            return null;
         }

         Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
         TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
         return (TypeAdapter<T>) newSetAdapter(elementAdapter);
      }

      private <E> TypeAdapter<Set<E>> newSetAdapter(final TypeAdapter<E> elementAdapter) {
         return new TypeAdapter<Set<E>>() {
            public void write(JsonWriter out, Set<E> value) throws IOException {
               out.beginArray();
               for (E element : value) {
                  elementAdapter.write(out, element);
               }
               out.endArray();
            }

            public Set<E> read(JsonReader in) throws IOException {
               Set<E> result = Sets.newLinkedHashSet();
               in.beginArray();
               while (in.hasNext()) {
                  E element = elementAdapter.read(in);
                  if (element != null) result.add(element);
               }
               in.endArray();
               return result;
            }
         }.nullSafe();
      }
   }

   /**
    * Eliminates null values from incoming maps
    * <p/>
    * Treats ["a":null] as the empty map; ["a":1, "b":null] as ["a":1]; etc.
    */
   public static class MapTypeAdapterFactory implements TypeAdapterFactory {
      @SuppressWarnings("unchecked")
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Type type = typeToken.getType();
         if (typeToken.getRawType() != Map.class || !(type instanceof ParameterizedType)) {
            return null;
         }

         Type keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
         Type valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
         TypeAdapter<?> keyAdapter = gson.getAdapter(TypeToken.get(keyType));
         TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(valueType));
         return (TypeAdapter<T>) newMapAdapter(keyAdapter, valueAdapter);
      }

      private <K,V> TypeAdapter<Map<K, V>> newMapAdapter(final TypeAdapter<K> keyAdapter, final TypeAdapter<V> valueAdapter) {
         return new TypeAdapter<Map<K, V>>() {
            public void write(JsonWriter out, Map<K, V> value) throws IOException {
               out.beginObject();
               for (Map.Entry<K, V> element : value.entrySet()) {
                  out.name(keyAdapter.toJson(element.getKey()));
                  valueAdapter.write(out, element.getValue());
               }
               out.endObject();
            }

            public Map<K, V> read(JsonReader in) throws IOException {
               Map<K, V> result = Maps.newLinkedHashMap();
               in.beginObject();
               while (in.hasNext()) {
                  JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
                  K name = keyAdapter.read(in);
                  V value = valueAdapter.read(in);
                  if (value != null) result.put(name, value);
               }
               in.endObject();
               return result;
            }
         }.nullSafe();
      }
   }

   /**
    * Parses Multi-maps to/from json
    */
   public static class MultimapTypeAdapterFactory implements TypeAdapterFactory {
      @SuppressWarnings("unchecked")
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Type type = typeToken.getType();
         if ((typeToken.getRawType() != Multimap.class) || !(type instanceof ParameterizedType)) {
            return null;
         }

         Type keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
         Type valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
         TypeAdapter<?> keyAdapter = gson.getAdapter(TypeToken.get(keyType));
         TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(valueType));
         return (TypeAdapter<T>) newMapAdapter(keyAdapter, valueAdapter);
      }

      private <K,V> TypeAdapter<Multimap<K, V>> newMapAdapter(final TypeAdapter<K> keyAdapter, final TypeAdapter<V> valueAdapter) {
         return new TypeAdapter<Multimap<K, V>>() {
            public void write(JsonWriter out, Multimap<K, V> map) throws IOException {
               out.beginObject();
               for (K key : map.keySet()) {
                  out.name(keyAdapter.toJson(key));
                  out.beginArray();
                  for (V value : map.get(key)) {
                     valueAdapter.write(out, value);
                  }
                  out.endArray();
               }
               out.endObject();
            }

            public Multimap<K, V> read(JsonReader in) throws IOException {
               ImmutableMultimap.Builder<K, V> result = ImmutableListMultimap.builder();
               in.beginObject();
               while (in.hasNext()) {
                  JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
                  K name = keyAdapter.read(in);
                  in.beginArray();
                  while (in.hasNext()) {
                     V value = valueAdapter.read(in);
                     if (value != null) result.put(name, value);
                  }
                  in.endArray();
               }
               in.endObject();
               return result.build();
            }
         }.nullSafe();
      }
   }
}
