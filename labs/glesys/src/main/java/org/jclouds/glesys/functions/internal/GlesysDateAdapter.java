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
package org.jclouds.glesys.functions.internal;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Singleton;

import org.jclouds.date.DateService;
import org.jclouds.json.config.GsonModule;

import com.google.common.base.Throwables;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.Inject;

/**
 * Parser for Glesys Date formats
 * 
 * @deprecated this should be replaced by standard ISO8601 parser in the next week or so
 * 
 * @author Adam Lowe
 */
@Singleton
public class GlesysDateAdapter extends GsonModule.DateAdapter {
   private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private final DateService standardDateService;

   @Inject
   public GlesysDateAdapter(DateService service) {
      this.standardDateService = service;
   }

   public void write(JsonWriter writer, Date value) throws IOException {
      try {
         writer.value(standardDateService.iso8601SecondsDateFormat(value));
      } catch (Exception ex) {
         synchronized (dateFormat) {
            writer.value(dateFormat.format(value));
         }
      }
   }

   public Date read(JsonReader reader) throws IOException {
      String toParse = reader.nextString();
      try {
         return standardDateService.iso8601SecondsDateParse(toParse);
      } catch (Exception ex) {
         try {
            synchronized (dateFormat) {
               return dateFormat.parse(toParse);
            }
         } catch (ParseException e) {
            throw Throwables.propagate(e);
         }
      }
   }
}
