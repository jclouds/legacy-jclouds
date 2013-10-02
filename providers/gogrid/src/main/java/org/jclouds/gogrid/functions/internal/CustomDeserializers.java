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
package org.jclouds.gogrid.functions.internal;

import java.lang.reflect.Type;

import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.JobState;
import org.jclouds.gogrid.domain.LoadBalancerOs;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerState;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.domain.ObjectType;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.domain.ServerState;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @author Oleksiy Yarmula
 */
public class CustomDeserializers {

   public static class ServerStateAdapter implements JsonDeserializer<ServerState> {
      @Override
      public ServerState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return ServerState.fromValue(name);
      }
   }

   public static class ObjectTypeAdapter implements JsonDeserializer<ObjectType> {
      @Override
      public ObjectType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return ObjectType.fromValue(name);
      }
   }

   public static class LoadBalancerOsAdapter implements JsonDeserializer<LoadBalancerOs> {
      @Override
      public LoadBalancerOs deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return LoadBalancerOs.fromValue(name);
      }
   }

   public static class LoadBalancerStateAdapter implements JsonDeserializer<LoadBalancerState> {
      @Override
      public LoadBalancerState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return LoadBalancerState.fromValue(name);
      }
   }

   public static class LoadBalancerPersistenceTypeAdapter implements JsonDeserializer<LoadBalancerPersistenceType> {
      @Override
      public LoadBalancerPersistenceType deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext context) throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return LoadBalancerPersistenceType.fromValue(name);
      }
   }

   public static class LoadBalancerTypeAdapter implements JsonDeserializer<LoadBalancerType> {
      @Override
      public LoadBalancerType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return LoadBalancerType.fromValue(name);
      }
   }

   public static class IpStateAdapter implements JsonDeserializer<IpState> {
      @Override
      public IpState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return IpState.fromValue(name);
      }
   }

   public static class JobStateAdapter implements JsonDeserializer<JobState> {
      @Override
      public JobState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return JobState.fromValue(name);
      }
   }

   public static class ServerImageStateAdapter implements JsonDeserializer<ServerImageState> {
      @Override
      public ServerImageState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return ServerImageState.fromValue(name);
      }
   }

   public static class ServerImageTypeAdapter implements JsonDeserializer<ServerImageType> {
      @Override
      public ServerImageType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String name = ((JsonObject) jsonElement).get("name").getAsString();
         return ServerImageType.fromValue(name);
      }
   }

}
