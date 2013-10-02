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
package org.jclouds.http.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * This object will parse the body of an HttpResponse and return the result of
 * type <T> back to the caller.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseJson<T> implements Function<HttpResponse, T> {

   @Resource
   protected Logger logger = Logger.NULL;
   protected final Json json;
   protected final TypeLiteral<T> type;

   @Inject
   public ParseJson(Json json, TypeLiteral<T> type) {
      this.json = json;
      this.type = type;
   }

   /**
    * parses the http response body to create a new {@code <T>}.
    */
   public T apply(HttpResponse from) {
      InputStream gson = from.getPayload().getInput();
      try {
         return apply(gson);
      } catch (Exception e) {
         StringBuilder message = new StringBuilder();
         message.append("Error parsing input");
         logger.error(e, message.toString());
         throw new HttpResponseException(message.toString() + "\n" + from, null, from, e);
      } finally {
         releasePayload(from);
      }

   }

   @SuppressWarnings("unchecked")
   public T apply(InputStream stream) throws IOException {
      return (T) apply(stream, type.getType());
   }

   @SuppressWarnings("unchecked")
   public <V> V apply(InputStream stream, Type type) throws IOException {
      try {
         return (V) json.fromJson(Strings2.toStringAndClose(stream), type);
      } finally {
         if (stream != null)
            stream.close();
      }
   }
}
