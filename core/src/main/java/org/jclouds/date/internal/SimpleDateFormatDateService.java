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
import static org.jclouds.date.internal.DateUtils.findTZ;
import static org.jclouds.date.internal.DateUtils.trimTZ;
import static org.jclouds.date.internal.DateUtils.trimToMillis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import org.jclouds.date.DateService;

/**
 * 
 * uses {@link SimpleDateFormat} internally.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class SimpleDateFormatDateService implements DateService {

   /*
    * Use default Java Date/SimpleDateFormat classes for date manipulation, but be *very* careful to
    * guard against the lack of thread safety.
    */
   // @GuardedBy("this")
   private static final SimpleDateFormat iso8601SecondsSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);

   // @GuardedBy("this")
   private static final SimpleDateFormat iso8601SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);

   // @GuardedBy("this")
   private static final SimpleDateFormat rfc822SimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

   // See http://stackoverflow.com/questions/10584647/simpledateformat-parse-is-one-hour-out-using-rfc-1123-gmt-in-summer
   // for why not using "zzz"
   // @GuardedBy("this")
   private static final SimpleDateFormat rfc1123SimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss Z", Locale.US);

   // @GuardedBy("this")
   private static final SimpleDateFormat cSimpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);

   static {
      iso8601SimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      iso8601SecondsSimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      rfc822SimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      cSimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
   }

   @Override
   public final String cDateFormat(Date date) {
      synchronized (cSimpleDateFormat) {
         return cSimpleDateFormat.format(date);
      }
   }

   @Override
   public final String cDateFormat() {
      return cDateFormat(new Date());
   }

   @Override
   public final Date cDateParse(String toParse) {
      synchronized (cSimpleDateFormat) {
         try {
            return cSimpleDateFormat.parse(toParse);
         } catch (ParseException pe) {
            throw new IllegalArgumentException("Error parsing data at " + pe.getErrorOffset(), pe);
         }
      }
   }

   @Override
   public final String rfc822DateFormat(Date date) {
      synchronized (rfc822SimpleDateFormat) {
         return rfc822SimpleDateFormat.format(date);
      }
   }

   @Override
   public final String rfc822DateFormat() {
      return rfc822DateFormat(new Date());
   }

   @Override
   public final Date rfc822DateParse(String toParse) {
      synchronized (rfc822SimpleDateFormat) {
         try {
            return rfc822SimpleDateFormat.parse(toParse);
         } catch (ParseException pe) {
            throw new IllegalArgumentException("Error parsing data at " + pe.getErrorOffset(), pe);
         }
      }
   }

   @Override
   public final String iso8601SecondsDateFormat() {
      return iso8601SecondsDateFormat(new Date());
   }

   @Override
   public final String iso8601DateFormat(Date date) {
      synchronized (iso8601SimpleDateFormat) {
         String parsed = iso8601SimpleDateFormat.format(date);
         String tz = findTZ(parsed);
         if (tz.equals("+0000")) {
            parsed = trimTZ(parsed) + "Z";
         }
         return parsed;
      }
   }

   @Override
   public final String iso8601DateFormat() {
      return iso8601DateFormat(new Date());
   }

   @Override
   public final Date iso8601DateParse(String toParse) {
      if (toParse.length() < 10)
         throw new IllegalArgumentException("incorrect date format " + toParse);
      String tz = findTZ(toParse);
      toParse = trimToMillis(toParse);
      toParse = trimTZ(toParse);
      toParse += tz;
      if (toParse.charAt(10) == ' ')
         toParse = new StringBuilder(toParse).replace(10, 11, "T").toString();
      synchronized (iso8601SimpleDateFormat) {
         try {
            return iso8601SimpleDateFormat.parse(toParse);
         } catch (ParseException pe) {
            throw new IllegalArgumentException("Error parsing data at " + pe.getErrorOffset(), pe);
         }
      }
   }

   @Override
   public final Date iso8601SecondsDateParse(String toParse) {
      if (toParse.length() < 10)
         throw new IllegalArgumentException("incorrect date format " + toParse);
      String tz = findTZ(toParse);
      toParse = trimToMillis(toParse);
      toParse = trimTZ(toParse);
      toParse += tz;
      if (toParse.charAt(10) == ' ')
         toParse = new StringBuilder(toParse).replace(10, 11, "T").toString();
      synchronized (iso8601SecondsSimpleDateFormat) {
         try {
            return iso8601SecondsSimpleDateFormat.parse(toParse);
         } catch (ParseException pe) {
            throw new IllegalArgumentException("Error parsing data at " + pe.getErrorOffset(), pe);
         }
      }
   }

   @Override
   public String iso8601SecondsDateFormat(Date date) {
      synchronized (iso8601SecondsSimpleDateFormat) {
         String parsed = iso8601SecondsSimpleDateFormat.format(date);
         String tz = findTZ(parsed);
         if (tz.equals("+0000")) {
            parsed = trimTZ(parsed) + "Z";
         }
         return parsed;
      }
   }

   @Override
   public final String rfc1123DateFormat(Date date) {
      synchronized (rfc1123SimpleDateFormat) {
         return rfc1123SimpleDateFormat.format(date);
      }
   }

   @Override
   public final String rfc1123DateFormat() {
      return rfc1123DateFormat(new Date());
   }

   @Override
   public final Date rfc1123DateParse(String toParse) throws IllegalArgumentException {
      synchronized (rfc1123SimpleDateFormat) {
         try {
            return rfc1123SimpleDateFormat.parse(toParse);
         } catch (ParseException pe) {
            throw new IllegalArgumentException("Error parsing data at " + pe.getErrorOffset(), pe);
         }
      }
   }
}
