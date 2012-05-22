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
package org.jclouds.date.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.inject.Singleton;

import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.date.DateService;

import com.google.inject.Inject;

@Singleton
public class DateServiceDateCodecFactory implements DateCodecFactory {

   private final DateCodec rfc1123Codec;

   @Inject
   public DateServiceDateCodecFactory(DateServiceRfc1123Codec rfc1123Codec) {
      this.rfc1123Codec = checkNotNull(rfc1123Codec, "rfc1123Codec");
   }

   @Singleton
   public static class DateServiceRfc1123Codec implements DateCodec {

      private final DateService dateService;

      @Inject
      public DateServiceRfc1123Codec(final DateService dateService) {
         this.dateService = checkNotNull(dateService, "dateService");
      }

      @Override
      public Date toDate(String date) throws IllegalArgumentException {
         return dateService.rfc1123DateParse(date);
      }

      @Override
      public String toString(Date date) {
         return dateService.rfc1123DateFormat(date);
      }

      @Override
      public String toString() {
         return "rfc1123Codec [dateService=" + dateService + "]";
      }

   }

   public DateCodec rfc1123() {
      return rfc1123Codec;
   }
  
}
