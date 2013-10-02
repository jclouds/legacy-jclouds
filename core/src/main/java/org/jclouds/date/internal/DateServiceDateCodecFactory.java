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

   private final DateCodec rfc822Codec;
   private final DateCodec rfc1123Codec;
   private final DateCodec iso8601Codec;
   private final DateCodec iso8601SecondsCodec;

   @Inject
   public DateServiceDateCodecFactory(DateService dateService) {
      checkNotNull(dateService, "dateService");
      this.rfc822Codec = new DateServiceRfc822Codec(dateService);
      this.rfc1123Codec = new DateServiceRfc1123Codec(dateService);
      this.iso8601Codec = new DateServiceIso8601Codec(dateService);
      this.iso8601SecondsCodec = new DateServiceIso8601SecondsCodec(dateService);
   }

   @Singleton
   public static class DateServiceRfc822Codec implements DateCodec {

      protected final DateService dateService;

      @Inject
      public DateServiceRfc822Codec(final DateService dateService) {
         this.dateService = checkNotNull(dateService, "dateService");
      }

      @Override
      public Date toDate(String date) throws IllegalArgumentException {
         return dateService.rfc822DateParse(date);
      }

      @Override
      public String toString(Date date) {
         return dateService.rfc822DateFormat(date);
      }

      @Override
      public String toString() {
         return "rfc822()";
      }

   }

   @Singleton
   public static class DateServiceRfc1123Codec implements DateCodec {

      protected final DateService dateService;

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
         return "rfc1123()";
      }

   }
   
   @Singleton
   public static class DateServiceIso8601Codec implements DateCodec {

      protected final DateService dateService;

      @Inject
      public DateServiceIso8601Codec(DateService dateService) {
         this.dateService = checkNotNull(dateService, "dateService");
      }

      @Override
      public Date toDate(String date) throws IllegalArgumentException {
         return dateService.iso8601DateParse(date);
      }

      @Override
      public String toString(Date date) {
         return dateService.iso8601DateFormat(date);
      }

      @Override
      public String toString() {
         return "iso8601()";
      }

   }
   
   @Singleton
   public static class DateServiceIso8601SecondsCodec implements DateCodec {

      protected final DateService dateService;

      @Inject
      public DateServiceIso8601SecondsCodec(DateService dateService) {
         this.dateService = checkNotNull(dateService, "dateService");
      }

      @Override
      public Date toDate(String date) throws IllegalArgumentException {
         return dateService.iso8601SecondsDateParse(date);
      }

      @Override
      public String toString(Date date) {
         return dateService.iso8601SecondsDateFormat(date);
      }

      @Override
      public String toString() {
         return "iso8601Seconds()";
      }

   }

   @Override
   public DateCodec rfc822() {
      return rfc822Codec;
   }

   @Override
   public DateCodec rfc1123() {
      return rfc1123Codec;
   }

   @Override
   public DateCodec iso8601() {
      return iso8601Codec;
   }

   @Override
   public DateCodec iso8601Seconds() {
      return iso8601SecondsCodec;
   }

}
