/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.glesys.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

/**
 * In GleSYS, the server responses are returned without the serverid. in this
 * case, it is in a different json ball called arguments. We need to merge this
 * jsonball before attempting to parse the server, otherwise the server will be
 * without an id.
 * 
 * @author Adrian Cole
 */
@Singleton
public class MergeArgumentsAndParse<T> implements Function<HttpResponse, T> {

   private final GsonWrapper jsonParser;
   private final ParseFirstJsonValueNamed<Map<String, JsonBall>> parseArguments;
   private final ParseFirstJsonValueNamed<Map<String, JsonBall>> parseServer;
   private final TypeLiteral<T> type;

   @Inject
   public MergeArgumentsAndParse(GsonWrapper json, TypeLiteral<T> type, String name) {
      this.jsonParser = checkNotNull(json, "json");
      this.parseArguments = new ParseFirstJsonValueNamed<Map<String, JsonBall>>(json,
            new TypeLiteral<Map<String, JsonBall>>() {
            }, "arguments");
      this.parseServer = new ParseFirstJsonValueNamed<Map<String, JsonBall>>(json,
            new TypeLiteral<Map<String, JsonBall>>() {
            }, name);
      this.type = checkNotNull(type, "type");
   }

   @SuppressWarnings("unchecked")
   @Override
   public T apply(HttpResponse arg0) {
      try {
         if (arg0.getPayload() == null)
            return null;

         arg0 = makePayloadReplayable(arg0);

         Map<String, JsonBall> server = parseServer.apply(arg0);
         if (server == null)
            return null;
         Map<String, JsonBall> arguments = parseArguments.apply(arg0);
         if (arguments != null)
            server = ImmutableMap.<String, JsonBall> builder().putAll(server).putAll(arguments).build();
         return (T) jsonParser.fromJson(jsonParser.toJson(server), type.getRawType());
      } catch (IOException e) {
         return null;
      }
   }

   // inputStreams are not replayable, yet we need to
   public HttpResponse makePayloadReplayable(HttpResponse arg0) throws IOException {
      String json = Strings2.toStringAndClose(arg0.getPayload().getInput());
      arg0 = arg0.toBuilder().payload(new StringPayload(json)).build();
      return arg0;
   }
}