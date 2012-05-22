package org.jclouds.date.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.text.ParseException;
import java.util.Date;

import org.jclouds.date.DateCodec;
import org.jclouds.util.Throwables2;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SimpleDateCodecFactoryTest {

   private SimpleDateCodecFactory simpleDateCodecFactory;
   private DateCodec rfc1123Codec;

   @BeforeMethod
   public void setUp() throws Exception {
      simpleDateCodecFactory = new SimpleDateCodecFactory(new SimpleDateFormatDateService());
      rfc1123Codec = simpleDateCodecFactory.rfc1123();
   }
   
   @Test
   public void testCodecForRfc1123() throws Exception {
      Date date = new Date(1000);
      assertEquals(rfc1123Codec.toDate(rfc1123Codec.toString(date)), date);
      
      assertEquals(rfc1123Codec.toDate("Thu, 01 Dec 1994 16:00:00 GMT"), new Date(786297600000L));
   }
   
   @Test
   public void testCodecForRfc1123ThrowsParseExceptionWhenMalformed() throws Exception {
      try {
         rfc1123Codec.toDate("wrong");
         fail();
      } catch (Exception e) {
         if (Throwables2.getFirstThrowableOfType(e, ParseException.class) == null) {
            throw e;
         }
      }
   }
}
