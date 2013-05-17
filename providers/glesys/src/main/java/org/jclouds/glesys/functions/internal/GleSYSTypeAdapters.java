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
package org.jclouds.glesys.functions.internal;

import java.io.IOException;

import org.jclouds.glesys.domain.GleSYSBoolean;
import org.jclouds.glesys.domain.Server;

import com.google.common.base.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author Adam Lowe
 */
public class GleSYSTypeAdapters {

   public static class ServerStateAdapter extends TypeAdapter<Server.State> {
      @Override
      public void write(JsonWriter writer, Server.State value) throws IOException {
         writer.value(value.value());
      }

      @Override
      public Server.State read(JsonReader reader) throws IOException {
         if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return Server.State.UNRECOGNIZED;
         }
         return Server.State.fromValue(reader.nextString());
      }
   }

   public static class GleSYSBooleanAdapter extends TypeAdapter<GleSYSBoolean> {

      @Override
      public void write(JsonWriter writer, GleSYSBoolean value) throws IOException {
         writer.value(value.getValue() ? "yes" : "no");
      }

      @Override
      public GleSYSBoolean read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.BOOLEAN) {
            return new GleSYSBoolean(in.nextBoolean());
         } else if (in.peek() == JsonToken.NULL) {
            return GleSYSBoolean.FALSE;
         } else {
            return new GleSYSBoolean(Objects.equal(in.nextString(), "yes"));
         }
      }

   }

}
