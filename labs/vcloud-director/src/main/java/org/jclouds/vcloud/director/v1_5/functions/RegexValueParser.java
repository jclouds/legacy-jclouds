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
package org.jclouds.vcloud.director.v1_5.functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;

import com.google.common.base.Function;
import com.google.inject.Singleton;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegexValueParser implements Function<HttpResponse, String> {
   Pattern pattern = Pattern.compile("<Value>([^<]+)</Value>");
   private final ReturnStringIf2xx returnStringIf200;

   @Inject
   RegexValueParser(ReturnStringIf2xx returnStringIf200) {
      this.returnStringIf200 = returnStringIf200;
   }

   @Override
   public String apply(HttpResponse response) {
      String content = returnStringIf200.apply(response);
      if (content != null) {
         Matcher matcher = pattern.matcher(content);
         if (matcher.find()) {
            return matcher.group(1);
         }
      }
      return null;
   }

}
