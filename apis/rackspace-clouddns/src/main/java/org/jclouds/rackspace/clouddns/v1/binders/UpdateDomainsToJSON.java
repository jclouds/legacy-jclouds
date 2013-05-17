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
package org.jclouds.rackspace.clouddns.v1.binders;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @author Everett Toews
 */
public class UpdateDomainsToJSON implements MapBinder {
   private static final String template = "{\"domains\":[%s]}";

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Iterable<Integer> ids = Iterable.class.cast(postParams.get("ids"));
      String key, value, updateTemplate;
      
      if (postParams.get("emailAddress") != null) {
         updateTemplate = "{\"id\":%s,\"%s\":\"%s\"}";
         key = "emailAddress";
      } else if (postParams.get("ttl") != null) {
         updateTemplate = "{\"id\":%s,\"%s\":%s}";
         key = "ttl";
      } else {
         throw new IllegalStateException("emailAddress or ttl not found in " + postParams);
      }
      
      value = postParams.get(key).toString();
      return (R) request.toBuilder().payload(toJSON(ids, updateTemplate, key, value)).build();
   }

   private String toJSON(Iterable<Integer> ids, String updateTemplate, String key, String value) {
      List<String> json = Lists.newArrayList();

      for (Integer id: ids) {
         json.add(format(updateTemplate, id, key, value));
      }
      
      String contentsAsOneString = Joiner.on(",").join(json);

      return format(template, contentsAsOneString);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("use map form");
   }
}
