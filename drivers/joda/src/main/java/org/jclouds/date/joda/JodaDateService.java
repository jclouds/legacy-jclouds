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
package org.jclouds.date.joda;

import static org.jclouds.date.internal.DateUtils.findTZ;
import static org.jclouds.date.internal.DateUtils.trimTZ;
import static org.jclouds.date.internal.DateUtils.trimToMillis;

import java.util.Date;
import java.util.Locale;

import javax.inject.Singleton;

import org.jclouds.date.DateService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@Singleton
public class JodaDateService implements DateService {
   
   private static final DateTimeFormatter rfc822DateFormatter = DateTimeFormat.forPattern(
            "EEE, dd MMM yyyy HH:mm:ss 'GMT'").withLocale(Locale.US).withZone(DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter cDateFormatter = DateTimeFormat
            .forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(Locale.US).withZone(DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter iso8601SecondsDateFormatter = DateTimeFormat.forPattern(
            "yyyy-MM-dd'T'HH:mm:ssZ").withLocale(Locale.US).withZone(DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter iso8601DateFormatter = DateTimeFormat.forPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ").withLocale(Locale.US).withZone(DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter rfc1123DateFormat = DateTimeFormat.forPattern(
            "EEE, dd MMM yyyyy HH:mm:ss Z").withLocale(Locale.US).withZone(DateTimeZone.forID("GMT"));

   public final Date fromSeconds(long seconds) {
      return new Date(seconds * 1000);
   }

   public final String cDateFormat(Date dateTime) {
      return cDateFormatter.print(new DateTime(dateTime));
   }

   public final String cDateFormat() {
      return cDateFormat(new Date());
   }

   public final Date cDateParse(String toParse) {
      return cDateFormatter.parseDateTime(toParse).toDate();
   }

   public final String rfc822DateFormat(Date dateTime) {
      return rfc822DateFormatter.print(new DateTime(dateTime));
   }

   public final String rfc822DateFormat() {
      return rfc822DateFormat(new Date());
   }

   public final Date rfc822DateParse(String toParse) {
      return rfc822DateFormatter.parseDateTime(toParse).toDate();
   }

   public final String iso8601SecondsDateFormat(Date dateTime) {
      String parsed = iso8601SecondsDateFormatter.print(new DateTime(dateTime));
      String tz = findTZ(parsed);
      if (tz.equals("+0000")) {
         parsed = trimTZ(parsed) + "Z";
      }
      return parsed;
   }

   public final String iso8601SecondsDateFormat() {
      return iso8601SecondsDateFormat(new Date());
   }

   public final String iso8601DateFormat(Date date) {
      String parsed = iso8601DateFormatter.print(new DateTime(date));
      String tz = findTZ(parsed);
      if (tz.equals("+0000")) {
         parsed = trimTZ(parsed) + "Z";
      }
      return parsed;
   }

   public final String iso8601DateFormat() {
      return iso8601DateFormat(new Date());
   }

   public final Date iso8601DateParse(String toParse) {
      if (toParse.length() < 10)
         throw new IllegalArgumentException("incorrect date format " + toParse);
      toParse = adjustTz(toParse);
      if (toParse.charAt(10) == ' ')
         toParse = new StringBuilder(toParse).replace(10, 11, "T").toString();
      return iso8601DateFormatter.parseDateTime(toParse).toDate();
   }

   private String adjustTz(String toParse) {
      String tz = findTZ(toParse);
      toParse = trimToMillis(toParse);
      toParse = trimTZ(toParse);
      toParse += tz;
      return toParse.replace("UTC", "");
   }

   public final Date iso8601SecondsDateParse(String toParse) {
      if (toParse.length() < 10)
         throw new IllegalArgumentException("incorrect date format " + toParse);
      toParse = adjustTz(toParse);
      return iso8601SecondsDateFormatter.parseDateTime(toParse).toDate();
   }
   
   @Override
   public final String rfc1123DateFormat(Date dateTime) {
      return rfc1123DateFormat.print(new DateTime(dateTime));
   }

   @Override
   public final String rfc1123DateFormat() {
      return rfc1123DateFormat(new Date());
   }

   @Override
   public final Date rfc1123DateParse(String toParse) {
      return rfc1123DateFormat.parseDateTime(toParse).toDate();
   }
}
