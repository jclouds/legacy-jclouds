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

package org.jclouds.date.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.jclouds.date.DateService;
import org.jclouds.logging.Logger;

/**
 * 
 * uses {@link SimpleDateFormat} internally.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class SimpleDateFormatDateService implements DateService {

   @Resource
   protected Logger logger = Logger.NULL;
   /*
    * Use default Java Date/SimpleDateFormat classes for date manipulation, but be *very* careful to
    * guard against the lack of thread safety.
    */
   // @GuardedBy("this")
   private static final SimpleDateFormat iso8601SecondsSimpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

   // @GuardedBy("this")
   private static final SimpleDateFormat iso8601SimpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

   // @GuardedBy("this")
   private static final SimpleDateFormat rfc822SimpleDateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

   // @GuardedBy("this")
   private static final SimpleDateFormat cSimpleDateFormat = new SimpleDateFormat(
            "EEE MMM dd HH:mm:ss '+0000' yyyy", Locale.US);

   static {
      iso8601SimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      iso8601SecondsSimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      rfc822SimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      cSimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
   }

   public final Date fromSeconds(long seconds) {
      return new Date(seconds * 1000);
   }

   public final String cDateFormat(Date date) {
      synchronized (cSimpleDateFormat) {
         return cSimpleDateFormat.format(date);
      }
   }

   public final String cDateFormat() {
      return cDateFormat(new Date());
   }

   public final Date cDateParse(String toParse) {
      synchronized (cSimpleDateFormat) {
         try {
            return cSimpleDateFormat.parse(toParse);
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public final String rfc822DateFormat(Date date) {
      synchronized (rfc822SimpleDateFormat) {
         return rfc822SimpleDateFormat.format(date);
      }
   }

   public final String rfc822DateFormat() {
      return rfc822DateFormat(new Date());
   }

   public final Date rfc822DateParse(String toParse) {
      synchronized (rfc822SimpleDateFormat) {
         try {
            return rfc822SimpleDateFormat.parse(toParse);
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public final String iso8601SecondsDateFormat() {
      return iso8601SecondsDateFormat(new Date());
   }

   public final String iso8601DateFormat(Date date) {
      synchronized (iso8601SimpleDateFormat) {
         return iso8601SimpleDateFormat.format(date);
      }
   }

   public final String iso8601DateFormat() {
      return iso8601DateFormat(new Date());
   }

   public final Date iso8601DateParse(String toParse) {
      toParse = trimTZ(toParse);
      toParse = trimNanosToMillis(toParse);
      synchronized (iso8601SimpleDateFormat) {
         try {
            return iso8601SimpleDateFormat.parse(toParse);
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static final Pattern NANOS_TO_MILLIS_PATTERN = Pattern
            .compile(".*[0-9][0-9][0-9][0-9][0-9][0-9]");

   public static final Pattern TZ_PATTERN = Pattern.compile(".*[+-][0-9][0-9]:?[0-9][0-9]");

   private String trimNanosToMillis(String toParse) {
      if (NANOS_TO_MILLIS_PATTERN.matcher(toParse).matches())
         toParse = toParse.substring(0, toParse.length() - 3) + 'Z';
      return toParse;
   }

   public static final Pattern SECOND_PATTERN = Pattern.compile(".*[0-2][0-9]:00");

   private String trimTZ(String toParse) {
      if (TZ_PATTERN.matcher(toParse).matches()) {
         logger.trace("trimming tz from %s", toParse);
         toParse = toParse.substring(0, toParse.length() - 6) + 'Z';
      }
      if (toParse.length() == 25 && SECOND_PATTERN.matcher(toParse).matches())
         toParse = toParse.substring(0, toParse.length() - 6) + 'Z';
      return toParse;
   }

   public final Date iso8601SecondsDateParse(String toParse) {
      toParse = trimTZ(toParse);
      synchronized (iso8601SecondsSimpleDateFormat) {
         try {
            return iso8601SecondsSimpleDateFormat.parse(toParse);
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   @Override
   public String iso8601SecondsDateFormat(Date date) {
      synchronized (iso8601SecondsSimpleDateFormat) {
         return iso8601SecondsSimpleDateFormat.format(date);
      }
   }

}