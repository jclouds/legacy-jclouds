/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.date.joda;

import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import net.jcip.annotations.ThreadSafe;

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
@ThreadSafe
@Singleton
public class JodaDateService implements DateService {

   private static final DateTimeFormatter rfc822DateFormatter = DateTimeFormat.forPattern(
            "EEE, dd MMM yyyy HH:mm:ss 'GMT'").withLocale(Locale.US).withZone(
            DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter cDateFormatter = DateTimeFormat.forPattern(
            "EEE MMM dd HH:mm:ss '+0000' yyyy").withLocale(Locale.US).withZone(
            DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter iso8601SecondsDateFormatter = DateTimeFormat.forPattern(
            "yyyy-MM-dd'T'HH:mm:ss'Z'").withLocale(Locale.US).withZone(DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter iso8601DateFormatter = DateTimeFormat.forPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withLocale(Locale.US).withZone(
            DateTimeZone.forID("GMT"));

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
      return iso8601SecondsDateFormatter.print(new DateTime(dateTime));
   }

   public final String iso8601SecondsDateFormat() {
      return iso8601SecondsDateFormat(new Date());
   }

   public final String iso8601DateFormat(Date date) {
      return iso8601DateFormatter.print(new DateTime(date));
   }

   public final String iso8601DateFormat() {
      return iso8601DateFormat(new Date());
   }

   public final Date iso8601DateParse(String toParse) {
      toParse = trimNanosToMillis(toParse);
      return iso8601DateFormatter.parseDateTime(toParse).toDate();
   }

   public static final Pattern NANOS_TO_MILLIS_PATTERN = Pattern
            .compile(".*[0-9][0-9][0-9][0-9][0-9][0-9]");

   private String trimNanosToMillis(String toParse) {
      if (NANOS_TO_MILLIS_PATTERN.matcher(toParse).matches())
         toParse = toParse.substring(0, toParse.length() - 3) + 'Z';
      return toParse;
   }

   public static final Pattern SECOND_PATTERN = Pattern.compile(".*[0-2][0-9]:00");

   private String trimTZ(String toParse) {
      if (toParse.length() == 25 && SECOND_PATTERN.matcher(toParse).matches())
         toParse = toParse.substring(0, toParse.length() - 6) + 'Z';
      return toParse;
   }

   public final Date iso8601SecondsDateParse(String toParse) {
      toParse = trimTZ(toParse);
      return iso8601SecondsDateFormatter.parseDateTime(toParse).toDate();
   }
}