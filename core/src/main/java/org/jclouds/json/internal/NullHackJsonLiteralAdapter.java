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
package org.jclouds.json.internal;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

import javax.inject.Singleton;

import com.google.common.base.Throwables;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * writes or reads the literal json directly
 * 
 * @see <a href="http://code.google.com/p/google-gson/issues/detail?id=326" />
 * 
 */
@Singleton
public abstract class NullHackJsonLiteralAdapter<L> extends TypeAdapter<L> {

   @Override
   public L read(JsonReader reader) throws IOException {
      return createJsonLiteralFromRawJson(TypeAdapters.JSON_ELEMENT.read(reader).toString());
   }


   /**
    * User supplied type that holds json literally. Ex. number as {@code 8}, boolean as {@code true}
    * , string as {@code "value"}, object as {@code , list {@code []}.
    */
   protected abstract L createJsonLiteralFromRawJson(String json);

   // the writer inside JsonWriter is not accessible currently
   private static final Field outField;
   static {
      try {
         outField = JsonWriter.class.getDeclaredField("out");
      } catch (SecurityException e) {
         throw Throwables.propagate(e);
      } catch (NoSuchFieldException e) {
         throw Throwables.propagate(e);
      }
      outField.setAccessible(true);
   }

   @Override
   public void write(JsonWriter jsonWriter, L value) throws IOException {

      Writer writer = getWriter(jsonWriter);
      boolean serializeNulls = jsonWriter.getSerializeNulls();
      try {
         // we are using an implementation hack which depends on replacing null with the raw json
         // supplied as a parameter. In this case, we must ensure we indeed serialize nulls.
         NullReplacingWriter nullReplacingWriter = new NullReplacingWriter(writer, toString(value));
         setWriter(jsonWriter, nullReplacingWriter);
         jsonWriter.setSerializeNulls(true);
         jsonWriter.nullValue();
      } finally {
         setWriter(jsonWriter, writer);
         jsonWriter.setSerializeNulls(serializeNulls);
      }

   }

   protected String toString(L value) {
      return value.toString();
   }

   protected Writer getWriter(JsonWriter arg0) {
      try {
         return Writer.class.cast(outField.get(arg0));
      } catch (IllegalAccessException e) {
         throw Throwables.propagate(e);
      }
   }

   private void setWriter(JsonWriter arg0, Writer arg1) {
      try {
         outField.set(arg0, arg1);
      } catch (IllegalAccessException e) {
         throw Throwables.propagate(e);
      }
   }

   public final class NullReplacingWriter extends Writer {
      private final Writer delegate;
      private final String nullReplacement;

      public NullReplacingWriter(Writer delegate, String nullReplacement) {
         this.delegate = delegate;
         this.nullReplacement = nullReplacement;
      }

      @Override
      public void write(char[] buffer, int offset, int count) throws IOException {
         delegate.write(buffer, offset, count);
      }

      @Override
      public void write(String s) throws IOException {
         if (nullReplacement != null && s.equals("null")) {
            s = nullReplacement;
         }
         super.write(s);
      }

      @Override
      public void flush() throws IOException {
         delegate.flush();
      }

      @Override
      public void close() throws IOException {
         delegate.close();
      }

   }
}
