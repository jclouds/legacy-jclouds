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
package org.jclouds.rimuhosting.miro.config;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.date.DateService;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.PropertiesAdapter;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.location.RimuHostingDefaultLocationSupplier;
import org.jclouds.rimuhosting.miro.location.RimuHostingLocationSupplier;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class RimuHostingRestClientModule extends RestClientModule<RimuHostingClient, RimuHostingAsyncClient> {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(RimuIso8601DateAdapter.class);
      super.configure();
   }

   @Singleton
   public static class RimuIso8601DateAdapter extends GsonModule.DateAdapter {
      private final DateService dateService;
      private final PropertiesAdapter propertiesAdapter;

      @Inject
      private RimuIso8601DateAdapter(DateService dateService, PropertiesAdapter propertiesAdapter) {
         this.dateService = dateService;
         this.propertiesAdapter = propertiesAdapter;
      }

      public void write(JsonWriter writer, Date value) throws IOException {
         throw new UnsupportedOperationException();
      }

      public Date read(JsonReader in) throws IOException {
         String isoFormat = propertiesAdapter.read(in).getProperty("iso_format");
         if (isoFormat != null)
            return dateService.iso8601SecondsDateParse(isoFormat);
         return null;
      }

   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(RimuHostingDefaultLocationSupplier.class).in(Scopes.SINGLETON);
      bind(LocationsSupplier.class).to(RimuHostingLocationSupplier.class).in(Scopes.SINGLETON);
   }
}
