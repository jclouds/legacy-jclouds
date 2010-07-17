/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.gson.Gson;
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
   protected final Gson gson;
   protected final TypeLiteral<T> type;

   @Inject
   public ParseJson(Gson gson, TypeLiteral<T> type) {
      this.gson = gson;
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
         throw new HttpResponseException(message.toString() + "\n" + from,
               null, from, e);
      } finally {
         releasePayload(from);
      }

   }

   @SuppressWarnings("unchecked")
   public T apply(InputStream stream) {
      try {
         return (T) gson.fromJson(new InputStreamReader(stream, "UTF-8"), type
               .getType());
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}