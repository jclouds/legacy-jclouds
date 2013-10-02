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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Date;

import org.jclouds.date.DateCodec;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author aled
 * 
 */
@Test(testName = "DateServiceDateCodecFactoryTest")
public class DateServiceDateCodecFactoryTest {

   private DateServiceDateCodecFactory simpleDateCodecFactory;
   private DateCodec rfc822Codec;
   private DateCodec rfc1123Codec;
   private DateCodec iso8601Codec;
   private DateCodec iso8601SecondsCodec;

   @BeforeTest
   public void setUp() {
      simpleDateCodecFactory = new DateServiceDateCodecFactory(new SimpleDateFormatDateService());
      rfc822Codec = simpleDateCodecFactory.rfc822();
      rfc1123Codec = simpleDateCodecFactory.rfc1123();
      iso8601Codec = simpleDateCodecFactory.iso8601();
      iso8601SecondsCodec = simpleDateCodecFactory.iso8601Seconds();
   }
   
   @Test
   public void testCodecForRfc822() {
      Date date = new Date(1000);
      assertEquals(rfc822Codec.toDate(rfc822Codec.toString(date)), date);

      assertEquals(rfc822Codec.toDate("Thu, 01 Dec 1994 16:00:00 GMT"), new Date(786297600000L));
   }

   @Test
   public void testCodecForRfc822ThrowsParseExceptionWhenMalformed() {
      try {
         rfc822Codec.toDate("wrong");
         fail();
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void testCodecForRfc1123() {
      Date date = new Date(1000);
      assertEquals(rfc1123Codec.toDate(rfc1123Codec.toString(date)), date);

      assertEquals(rfc1123Codec.toDate("Thu, 01 Dec 1994 16:00:00 GMT"), new Date(786297600000L));
   }

   @Test
   public void testCodecForRfc1123ThrowsParseExceptionWhenMalformed() {
      try {
         rfc1123Codec.toDate("wrong");
         fail();
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void testCodecForIso8601() {
      Date date = new Date(1000);
      assertEquals(iso8601Codec.toDate(iso8601Codec.toString(date)), date);

      assertEquals(iso8601Codec.toDate("1994-12-01T16:00:00.000Z"), new Date(786297600000L));
   }

   @Test
   public void testCodecForIso8601ThrowsParseExceptionWhenMalformed() {
      try {
         iso8601Codec.toDate("-");
         fail();
      } catch (IllegalArgumentException e) {
      }
   }
   
   @Test
   public void testCodecForIso8601Seconds() {
      Date date = new Date(1000);
      assertEquals(iso8601SecondsCodec.toDate(iso8601SecondsCodec.toString(date)), date);

      assertEquals(iso8601SecondsCodec.toDate("2012-11-14T21:51:28UTC").getTime(), 1352929888000l);
   }

   @Test
   public void testCodecForIso8601SecondsThrowsParseExceptionWhenMalformed() {
      try {
         iso8601SecondsCodec.toDate("-");
         fail();
      } catch (IllegalArgumentException e) {
      }
   }
}
