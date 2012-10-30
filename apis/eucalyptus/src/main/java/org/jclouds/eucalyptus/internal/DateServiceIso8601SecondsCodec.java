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
package org.jclouds.eucalyptus.internal;

import java.util.Date;

import javax.inject.Singleton;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.DateServiceDateCodecFactory.DateServiceIso8601Codec;

import com.google.inject.Inject;

@Singleton
public class DateServiceIso8601SecondsCodec extends DateServiceIso8601Codec {

   @Inject
   public DateServiceIso8601SecondsCodec(DateService dateService) {
      super(dateService);
   }

   @Override
   public Date toDate(String date) throws IllegalArgumentException {
      try {
         return super.toDate(date);
      } catch (RuntimeException e) {
         // Eucalyptus <3.0 didn't include milliseconds
         // TODO: see if this is still a problem
         return dateService.iso8601SecondsDateParse(date);
      }
   }

   @Override
   public String toString() {
      return "iso8601Seconds()";
   }

}
