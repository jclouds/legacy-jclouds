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
package org.jclouds.dynect.v3.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.dynect.v3.domain.RecordId.recordIdBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * Records come back encoded in REST paths, such as
 * {@code /REST/NSRecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976583}
 * 
 * @author Adrian Cole
 * 
 */
public final class ToRecordIds implements Function<HttpResponse, FluentIterable<RecordId>> {
   private final ParseFirstJsonValueNamed<FluentIterable<String>> parser;

   @Inject
   public ToRecordIds(GsonWrapper gsonView) {
      this.parser = new ParseFirstJsonValueNamed<FluentIterable<String>>(checkNotNull(gsonView, "gsonView"),
            new TypeLiteral<FluentIterable<String>>() {
            }, "data");
   }

   public FluentIterable<RecordId> apply(HttpResponse response) {
      checkNotNull(response, "response");
      return parser.apply(response).transform(ParsePath.INSTANCE);
   }

   static enum ParsePath implements Function<String, RecordId> {
      INSTANCE;
      public static final Pattern DEFAULT_PATTERN = Pattern.compile("/REST/([a-zA-Z]+)Record/(.*)/(.*)/([0-9]+)");

      public RecordId apply(String in) {
         Matcher matcher = DEFAULT_PATTERN.matcher(in);
         checkState(matcher.find() && matcher.groupCount() == 4, "%s didn't match %s", in, DEFAULT_PATTERN);
         return recordIdBuilder().type(matcher.group(1)).zone(matcher.group(2)).fqdn(matcher.group(3))
               .id(Long.parseLong(matcher.group(4))).build();
      }
   }
}
