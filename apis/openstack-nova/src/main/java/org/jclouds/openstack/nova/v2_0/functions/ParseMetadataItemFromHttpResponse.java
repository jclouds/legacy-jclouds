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
package org.jclouds.openstack.nova.v2_0.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * @author Jeremy Daggett
 */
@Singleton
public class ParseMetadataItemFromHttpResponse implements Function<HttpResponse, String> {
   private final ParseFirstJsonValueNamed<Map<String, String>> parser;

   @Inject
   public ParseMetadataItemFromHttpResponse(GsonWrapper gsonView) {
      this.parser = new ParseFirstJsonValueNamed<Map<String, String>>(checkNotNull(gsonView,
               "gsonView"), new TypeLiteral<Map<String, String>>() {
      }, "metadata");
   }

   public String apply(HttpResponse response) {
      checkNotNull(response, "response");
      Map<String, String> toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      System.out.println(toParse.values().toString());
      return toParse.values().toString();
   }
}
