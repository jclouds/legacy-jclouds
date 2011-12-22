package org.jclouds.glesys.functions.internal;

import com.google.gson.*;
import org.jclouds.json.config.GsonModule;

import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Parser for Glesys Date formats
 *
 * @author Adam Lowe
 */
@Singleton
public class GlesysDateAdapter implements GsonModule.DateAdapter {
   private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
      synchronized (dateFormat) {
         return new JsonPrimitive(dateFormat.format(src));
      }
   }

   public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
         throws JsonParseException {
      String toParse = json.getAsJsonPrimitive().getAsString();
      try {
         synchronized (dateFormat) {
            return dateFormat.parse(toParse);
         }
      } catch (ParseException e) {
         throw new RuntimeException(e);
      }
   }
}
