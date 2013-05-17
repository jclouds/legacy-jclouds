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
package org.jclouds.http.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;

/**
 * Simply returns true when the http response code is in the range 200-299.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnTrueIf2xx implements Function<HttpResponse, Boolean> {

   public Boolean apply(HttpResponse from) {
      releasePayload(from);
      int code = from.getStatusCode();
      if (code >= 300 || code < 200) {
         throw new IllegalStateException("incorrect code for this operation: " + from);
      }
      return true;
   }
}
