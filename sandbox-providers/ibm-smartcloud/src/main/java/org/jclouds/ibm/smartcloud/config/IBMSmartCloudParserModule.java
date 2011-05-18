/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibm.smartcloud.config;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.http.HttpUtils;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.LongDateAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class IBMSmartCloudParserModule extends AbstractModule {
   @Singleton
   public static class CurlyBraceCapableURIAdapter implements JsonDeserializer<URI> {
      @Override
      public URI deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String toParse = jsonElement.getAsJsonPrimitive().getAsString();
         URI toReturn = HttpUtils.createUri(toParse);
         return toReturn;
      }
   }

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings(CurlyBraceCapableURIAdapter adapter) {
      return ImmutableMap.<Type, Object> of(URI.class, adapter);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(LongDateAdapter.class);
   }
}
