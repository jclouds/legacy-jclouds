package org.jclouds.glesys.functions.internal;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Singleton;

import org.jclouds.json.config.GsonModule;

import com.google.common.base.Throwables;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Parser for Glesys Date formats
 *
 * @author Adam Lowe
 */
@Singleton
public class GlesysDateAdapter extends GsonModule.DateAdapter {
   private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   public void write(JsonWriter writer, Date value) throws IOException {
      synchronized (dateFormat) {
         writer.value(dateFormat.format(value));
      }
   }

   public Date read(JsonReader reader) throws IOException {
      String toParse = reader.nextString();
      try {
         synchronized (dateFormat) {
            return dateFormat.parse(toParse);
         }
      } catch (ParseException e) {
         throw Throwables.propagate(e);
      }
   }
}
