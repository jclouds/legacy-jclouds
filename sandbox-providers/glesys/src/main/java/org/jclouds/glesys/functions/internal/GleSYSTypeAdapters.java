package org.jclouds.glesys.functions.internal;

import java.io.IOException;

import org.jclouds.glesys.domain.ServerState;
import org.jclouds.glesys.domain.ServerUptime;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * @author Adam Lowe
 */
public class GleSYSTypeAdapters {

   public static class ServerStateAdapter extends TypeAdapter<ServerState> {
      @Override
      public void write(JsonWriter writer, ServerState value) throws IOException {
         writer.value(value.value());
      }

      @Override
      public ServerState read(JsonReader reader) throws IOException {
         return ServerState.fromValue(reader.nextString());
      }
   }

   public static class ServerUptimeAdapter extends TypeAdapter<ServerUptime> {
      @Override
      public void write(JsonWriter writer, ServerUptime value) throws IOException {
         writer.value(value.toString());
      }

      @Override
      public ServerUptime read(JsonReader reader) throws IOException {
         return ServerUptime.fromValue(reader.nextString());
      }
   }
}