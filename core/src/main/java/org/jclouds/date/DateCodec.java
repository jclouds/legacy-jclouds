package org.jclouds.date;

import java.text.ParseException;
import java.util.Date;

public interface DateCodec {

   public Date toDate(String date) throws ParseException;
   
   public String toString(Date date);
   
}
