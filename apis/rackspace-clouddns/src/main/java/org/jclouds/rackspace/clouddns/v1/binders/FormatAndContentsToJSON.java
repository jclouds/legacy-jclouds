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

/**
 * @author Everett Toews
 */
public class FormatAndContentsToJSON implements MapBinder {
   private static final String template = "{\"domains\":[{\"contentType\":\"%s\",\"contents\":\"%s\"}]}";

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      String format = postParams.get("format").toString();
      List<String> contents = List.class.cast(postParams.get("contents"));

      return (R) request.toBuilder().payload(toJSON(format, contents)).build();
   }

   private String toJSON(String format, List<String> contents) {
      String contentsAsOneString = Joiner.on("\\n").join(contents);

      return format(template, format, contentsAsOneString);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("use map form");
   }
}
