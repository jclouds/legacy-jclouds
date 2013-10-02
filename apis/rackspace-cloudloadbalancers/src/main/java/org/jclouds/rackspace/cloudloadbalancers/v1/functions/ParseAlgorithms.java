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
package org.jclouds.rackspace.cloudloadbalancers.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Everett Toews
 */
public class ParseAlgorithms implements Function<HttpResponse, List<String>>, InvocationContext<ParseAlgorithms> {

   private final ParseJson<Map<String, List<Map<String, String>>>> json;

   @Inject
   ParseAlgorithms(ParseJson<Map<String, List<Map<String, String>>>> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public List<String> apply(HttpResponse response) {
      Map<String, List<Map<String, String>>> map = json.apply(response);
      
      if (map == null || map.size() == 0)
         throw new HttpResponseException("Unexpected JSON format returned.", null, response);
      
      List<Map<String, String>> list = Iterables.get(map.values(), 0);
      List<String> algorithms = Lists.newArrayList();
      
      for (Map<String, String> nameAlgorithmPair: list) {
         algorithms.add(Iterables.get(nameAlgorithmPair.values(), 0));
      }
      
      return algorithms;
   }

   @Override
   public ParseAlgorithms setContext(HttpRequest request) {
      return this;
   }
}
