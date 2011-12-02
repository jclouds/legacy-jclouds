package org.jclouds.glesys.functions.internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.jclouds.glesys.domain.ServerState;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.domain.ServerUptime;

import java.lang.reflect.Type;

/**
 * @Author Adam Lowe
 */
public class CustomDeserializers {

   public static class ServerStateAdapter implements JsonDeserializer<ServerState> {
      @Override
      public ServerState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String toParse = jsonElement.getAsJsonPrimitive().getAsString();
         return ServerState.fromValue(toParse);
      }
   }

   public static class ServerUptimeAdaptor implements JsonDeserializer<ServerUptime> {
      @Override
      public ServerUptime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
         String toParse = jsonElement.getAsJsonPrimitive().getAsString();
         return ServerUptime.fromValue(toParse);
      }
   }

}