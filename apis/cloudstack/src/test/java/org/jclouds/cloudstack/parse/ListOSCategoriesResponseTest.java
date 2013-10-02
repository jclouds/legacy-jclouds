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

import java.util.Map;

import org.jclouds.cloudstack.functions.ParseIdToNameFromHttpResponse;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.BaseItemParserTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListOSCategoriesResponseTest extends BaseItemParserTest<Map<String, String>> {

   @Override
   public String resource() {
      return "/listoscategoriesresponse.json";
   }

   @Override
   public Map<String, String> expected() {
       return ImmutableMap.<String, String> builder().put("1", "CentOS").put("10", "Ubuntu").put("2", "Debian").put("3", "Oracle")
            .put("4", "RedHat").put("5", "SUSE").put("6", "Windows").put("7", "Other").put("8", "Novel").put("9", "Unix")
            .build();
   }

   @Override
   protected Function<HttpResponse, Map<String, String>> parser(Injector injector) {
      return injector.getInstance(ParseIdToNameFromHttpResponse.class);
   }
}
