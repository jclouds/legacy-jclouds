/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.gogrid.functions.internal;

import com.google.gson.*;
import org.jclouds.gogrid.domain.*;

import java.lang.reflect.Type;

/**
 * @author Oleksiy Yarmula
 */
public class CustomDeserializers {

    public static class ObjectTypeAdapter implements JsonDeserializer<ObjectType> {
        @Override
        public ObjectType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String name = ((JsonObject) jsonElement).get("name").getAsString();
            return ObjectType.fromValue(name);
        }
    }

    public static class LoadBalancerOsAdapter implements JsonDeserializer<LoadBalancerOs> {
        @Override
        public LoadBalancerOs deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String name = ((JsonObject) jsonElement).get("name").getAsString();
            return LoadBalancerOs.fromValue(name);
        }
    }

    public static class LoadBalancerStateAdapter implements JsonDeserializer<LoadBalancerState> {
        @Override
        public LoadBalancerState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String name = ((JsonObject) jsonElement).get("name").getAsString();
            return LoadBalancerState.fromValue(name);
        }
    }

    public static class LoadBalancerPersistenceTypeAdapter implements JsonDeserializer<LoadBalancerPersistenceType> {
        @Override
        public LoadBalancerPersistenceType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String name = ((JsonObject) jsonElement).get("name").getAsString();
            return LoadBalancerPersistenceType.fromValue(name);
        }
    }

    public static class LoadBalancerTypeAdapter implements JsonDeserializer<LoadBalancerType> {
        @Override
        public LoadBalancerType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String name = ((JsonObject) jsonElement).get("name").getAsString();
            return LoadBalancerType.fromValue(name);
        }
    }

    public static class IpStateAdapter implements JsonDeserializer<IpState> {
        @Override
        public IpState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            String name = ((JsonObject) jsonElement).get("name").getAsString();
            return IpState.fromValue(name);
        }
    }

}
