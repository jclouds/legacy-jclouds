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
package org.jclouds.dynect.v3.parse;

import static com.google.common.base.Functions.compose;

import org.jclouds.dynect.v3.functions.ExtractLastPathComponent;
import org.jclouds.dynect.v3.internal.BaseDynECTParseTest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListGeoRegionGroupsResponseTest extends BaseDynECTParseTest<FluentIterable<String>> {

   @Override
   public String resource() {
      return "/list_geo_regiongroups.json";
   }

   @Override
   @SelectJson("data")
   public FluentIterable<String> expected() {
      return FluentIterable.from(ImmutableSet.of("Everywhere Else", "Europe", "Fallback"));
   }

   // TODO: currently our parsing of annotations on expected() ignores
   // @Transform
   @Override
   protected Function<HttpResponse, FluentIterable<String>> parser(Injector i) {
      return compose(new ExtractLastPathComponent(), super.parser(i));
   }
}
