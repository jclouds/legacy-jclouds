/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.annotations.VisibleForTesting;

/**
 * Parses and formats the ISO8601 and RFC822 date formats found in XML responses and HTTP response
 * headers.
 * <p>
 * Either {@link SimpleDateFormat} or {@link DateTimeFormatter} classes are used internally,
 * depending on which version gives the best performance.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@ThreadSafe
public class DateService {
   /*
    * Use default Java Date/SimpleDateFormat classes for date manipulation, but be *very* careful to
    * guard against the lack of thread safety.
    */
   @GuardedBy("this")
   private static final SimpleDateFormat iso8601SecondsSimpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

   @GuardedBy("this")
   private static final SimpleDateFormat iso8601SimpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

   @GuardedBy("this")
   private static final SimpleDateFormat rfc822SimpleDateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

   private static final DateTimeFormatter rfc822DateTimeFormatter = DateTimeFormat.forPattern(
            "EEE, dd MMM yyyy HH:mm:ss 'GMT'").withLocale(Locale.US).withZone(
            DateTimeZone.forID("GMT"));

   @GuardedBy("this")
   private static final SimpleDateFormat cSimpleDateFormat = new SimpleDateFormat(
            "EEE MMM dd HH:mm:ss '+0000' yyyy", Locale.US);

   private static final DateTimeFormatter cDateTimeFormatter = DateTimeFormat.forPattern(
            "EEE MMM dd HH:mm:ss '+0000' yyyy").withLocale(Locale.US).withZone(
            DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter iso8601SecondsDateTimeFormatter = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withLocale(Locale.US).withZone(
                     DateTimeZone.forID("GMT"));

   private static final DateTimeFormatter iso8601DateTimeFormatter = DateTimeFormat.forPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withLocale(Locale.US).withZone(
            DateTimeZone.forID("GMT"));

   static {
      iso8601SimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      iso8601SecondsSimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      rfc822SimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      cSimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
   }

   public final DateTime fromSeconds(long seconds) {
      return new DateTime(seconds * 1000);
   }

   public final String cDateFormat(DateTime dateTime) {
      return cDateTimeFormatter.print(dateTime);
   }

   public final String cDateFormat(Date date) {
      return cDateFormat(new DateTime(date));
   }

   public final String cDateFormat() {
      return cDateFormat(new DateTime());
   }

   public final DateTime cDateParse(String toParse) {
      synchronized (cSimpleDateFormat) {
         try {
            return new DateTime(cSimpleDateFormat.parse(toParse));
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public final String rfc822DateFormat(DateTime dateTime) {
      return rfc822DateTimeFormatter.print(dateTime);
   }

   public final String rfc822DateFormat(Date date) {
      return rfc822DateFormat(new DateTime(date));
   }

   public final String rfc822DateFormat() {
      return rfc822DateFormat(new DateTime());
   }

   public final DateTime rfc822DateParse(String toParse) {
      synchronized (rfc822SimpleDateFormat) {
         try {
            return new DateTime(rfc822SimpleDateFormat.parse(toParse));
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public final String iso8601DateFormat(DateTime dateTime) {
      return iso8601DateTimeFormatter.print(dateTime);
   }

   public final String iso8601SecondsDateFormat(DateTime dateTime) {
      return iso8601SecondsDateTimeFormatter.print(dateTime);
   }

   public final String iso8601DateFormat(Date date) {
      return iso8601DateFormat(new DateTime(date));
   }

   public final String iso8601DateFormat() {
      return iso8601DateFormat(new DateTime());
   }

   public final DateTime iso8601DateParse(String toParse) {
      synchronized (iso8601SimpleDateFormat) {
         try {
            return new DateTime(iso8601SimpleDateFormat.parse(toParse));
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public final DateTime iso8601SecondsDateParse(String toParse) {
      synchronized (iso8601SecondsSimpleDateFormat) {
         try {
            return new DateTime(iso8601SecondsSimpleDateFormat.parse(toParse));
         } catch (ParseException e) {
            throw new RuntimeException(e);
         }
      }
   }

   /*
    * Alternative implementations of Format and Parse -- used to test relative speeds. TODO: Remove
    * methods below once sufficient performance testing is complete.
    */

   @VisibleForTesting
   public final DateTime jodaIso8601DateParse(String toParse) {
      return new DateTime(toParse);
   }

   @VisibleForTesting
   public final String sdfIso8601DateFormat(DateTime dateTime) {
      synchronized (iso8601SimpleDateFormat) {
         return iso8601SimpleDateFormat.format(dateTime.toDate());
      }
   }

   @VisibleForTesting
   public final String sdfIso8601SecondsDateFormat(DateTime dateTime) {
      synchronized (iso8601SecondsSimpleDateFormat) {
         return iso8601SecondsSimpleDateFormat.format(dateTime.toDate());
      }
   }
}