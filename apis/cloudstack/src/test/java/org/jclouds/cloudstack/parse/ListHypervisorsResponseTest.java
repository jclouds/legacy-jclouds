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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.functions.ParseNamesFromHttpResponse;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.BaseSetParserTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListHypervisorsResponseTest extends BaseSetParserTest<String> {

   @Override
   public String resource() {
      return "/listhypervisorsresponse.json";
   }

   @Override
   public Set<String> expected() {
      return ImmutableSet.<String> of("XenServer", "KVM", "VMware");
   }

   @Override
   protected Function<HttpResponse, Set<String>> parser(Injector injector) {
      return injector.getInstance(ParseNamesFromHttpResponse.class);
   }
}
