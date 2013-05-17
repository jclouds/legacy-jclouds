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
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;

import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseAsyncJobFromHttpResponse implements Function<HttpResponse, AsyncJob<?>> {
   private final UnwrapOnlyJsonValue<AsyncJob<Map<String, JsonBall>>> parser;
   private final ParseTypedAsyncJob parseTyped;

   @Inject
   public ParseAsyncJobFromHttpResponse(ParseTypedAsyncJob parseTyped,
         UnwrapOnlyJsonValue<AsyncJob<Map<String, JsonBall>>> parser) {
      this.parseTyped = checkNotNull(parseTyped, "parseTyped");
      this.parser = checkNotNull(parser, "parser");
   }

   public AsyncJob<?> apply(HttpResponse response) {
      checkNotNull(response, "response");
      AsyncJob<Map<String, JsonBall>> toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      return parseTyped.apply(toParse);
   }
}
