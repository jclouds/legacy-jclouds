package org.jclouds.glesys.functions.internal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.jclouds.glesys.domain.ServerAllowedArguments;
import org.jclouds.glesys.domain.ServerState;
import org.jclouds.glesys.domain.ServerUptime;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

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
    
   public static class ServerAllowedArgumentsAdaptor implements JsonDeserializer<Set<ServerAllowedArguments>> {

       @Override
       public Set<ServerAllowedArguments> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
           Set<ServerAllowedArguments> result = new HashSet<ServerAllowedArguments>();
           for(JsonElement e : jsonElement.getAsJsonObject().get("OpenVZ").getAsJsonArray()) {
               result.add(jsonDeserializationContext.<ServerAllowedArguments>deserialize(e, ServerAllowedArguments.class));
           }
           return result;
       }
   }

}