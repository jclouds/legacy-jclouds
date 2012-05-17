package org.jclouds.date.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.util.Date;

import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.date.DateService;

import com.google.inject.Inject;

public class SimpleDateCodecFactory implements DateCodecFactory {

   private final DateService dateService;

   private volatile DateCodec rfc1123Codec;
   
   @Inject
   public SimpleDateCodecFactory(final DateService dateService) {
      this.dateService = checkNotNull(dateService, "dateService");
   }

   public DateCodec rfc1123() {
      if (rfc1123Codec == null) {
         rfc1123Codec = new DateCodec() {
            @Override
            public Date toDate(String date) throws ParseException {
               return dateService.rfc1123DateParse(date);
            }
   
            @Override
            public String toString(Date date) {
               return dateService.rfc1123DateFormat(date);
            }
         };
      }
      return rfc1123Codec;
   }
}
