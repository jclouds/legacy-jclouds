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
package org.jclouds.cloudstack.config;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;

import com.google.gson.stream.JsonReader;

/**
 * Data adapter for the date formats used by CloudStack.
 *
 * Essentially this is a workaround for the CloudStack getUsage() API call returning a
 * corrupted form of ISO-8601 dates, which have an unexpected pair of apostrophes, like
 * 2011-12-12'T'00:00:00+00:00
 *
 * @author Richard Downer
 */
public class CloudStackDateAdapter extends Iso8601DateAdapter {

   @Inject
   private CloudStackDateAdapter(DateService dateService) {
      super(dateService);
   }

   public Date read(JsonReader reader) throws IOException {
      return parseDate(reader.nextString().replaceAll("'T'", "T"));
   }

}
