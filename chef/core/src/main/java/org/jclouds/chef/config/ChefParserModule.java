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
package org.jclouds.chef.config;

import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.chef.domain.DataBagItem;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonLiteral;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.ImplementedBy;
import com.google.inject.Provides;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class ChefParserModule extends AbstractModule {
   @ImplementedBy(DataBagItemAdapterImpl.class)
   public static interface DataBagItemAdapter extends JsonSerializer<DataBagItem>, JsonDeserializer<DataBagItem> {

   }

   @Singleton
   public static class DataBagItemAdapterImpl implements DataBagItemAdapter {

      @Override
      public JsonElement serialize(DataBagItem src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonLiteral(src.toString());
      }

      @Override
      public DataBagItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         IdHolder idHolder = context.deserialize(json, IdHolder.class);
         return new DataBagItem(idHolder.id, json.toString());
      }
   }

   private static class IdHolder {
      private String id;
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_GSON_ADAPTERS)
   public Map<Type, Object> provideCustomAdapterBindings(DataBagItemAdapter adapter) {
      return ImmutableMap.<Type, Object> of(DataBagItem.class, adapter);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
   }
}