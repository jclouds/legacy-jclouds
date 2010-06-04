/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.chef.functions;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseErrorFromJsonOrNull implements Function<HttpResponse, String> {
   Pattern pattern = Pattern.compile(".*error\": *\"([^\"]+)\".*");

   @Override
   public String apply(HttpResponse response) {
      if (response.getContent() == null)
         return null;
      try {
         return parse(Utils.toStringAndClose(response.getContent()));
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         try {
            response.getContent().close();
         } catch (IOException e) {
            Throwables.propagate(e);
         }
      }
   }

   public String parse(String in) {
      Matcher matcher = pattern.matcher(in);
      while (matcher.find()) {
         return matcher.group(1);
      }
      return null;
   }
}