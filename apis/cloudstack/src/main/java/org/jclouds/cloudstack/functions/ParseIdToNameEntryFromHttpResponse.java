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

import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseIdToNameEntryFromHttpResponse implements Function<HttpResponse, Map.Entry<String, String>> {
   private final ParseIdToNameFromHttpResponse parser;

   @Inject
   public ParseIdToNameEntryFromHttpResponse(ParseIdToNameFromHttpResponse parser) {
      this.parser = checkNotNull(parser, "parser");
   }

   public Map.Entry<String, String> apply(HttpResponse response) {
      checkNotNull(response, "response");
      Map<String, String> toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      return Iterables.getFirst(toParse.entrySet(), null);
   }
}
