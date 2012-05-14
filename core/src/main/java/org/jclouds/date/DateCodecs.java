package org.jclouds.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateCodecs {

   // See http://stackoverflow.com/questions/10584647/simpledateformat-parse-is-one-hour-out-using-rfc-1123-gmt-in-summer
   // for why not using "zzz"
   public final static String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyyy HH:mm:ss Z";

   /*
    * Use default Java Date/SimpleDateFormat classes for date manipulation, but be *very* careful to
    * guard against the lack of thread safety.
    */
   // @GuardedBy("this")
   private static final SimpleDateFormat rfc1123SimpleDateFormat = new SimpleDateFormat(RFC1123_DATE_PATTERN, Locale.US);

   public static DateCodec rfc1123() {
      return new SimpleDateCodec(rfc1123SimpleDateFormat);
   }
   
   private static class SimpleDateCodec implements DateCodec {

      private final SimpleDateFormat dateFormat;

      SimpleDateCodec(SimpleDateFormat dateFormat) {
         this.dateFormat = dateFormat;
      }
      
      @Override
      public Date toDate(String date) throws ParseException {
         synchronized (dateFormat) {
            return dateFormat.parse(date);
         }
      }

      @Override
      public String toString(Date date) {
         synchronized (dateFormat) {
            return dateFormat.format(date);
         }
      }
   }
}
