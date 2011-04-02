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

package org.jclouds.rimuhosting.miro.config;

import java.lang.reflect.Type;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.date.DateService;
import org.jclouds.http.RequiresHttp;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

/**
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class RimuHostingRestClientModule extends RestClientModule<RimuHostingClient, RimuHostingAsyncClient> {

   public RimuHostingRestClientModule() {
      super(RimuHostingClient.class, RimuHostingAsyncClient.class);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(RimuIso8601DateAdapter.class);
      super.configure();
   }

   @Singleton
   public static class RimuIso8601DateAdapter implements DateAdapter {
      private final DateService dateService;
      private final Json json;

      private static class DateHolder {
         String iso_format;
      }

      @Inject
      private RimuIso8601DateAdapter(DateService dateService, Json json) {
         this.dateService = dateService;
         this.json = json;
      }

      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
         throw new UnsupportedOperationException();
      }

      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         String toParse = json.toString();
         DateHolder dateHolder = this.json.fromJson(toParse, DateHolder.class);
         return (dateHolder.iso_format != null) ? dateService.iso8601SecondsDateParse(dateHolder.iso_format) : null;
      }

   }
}
