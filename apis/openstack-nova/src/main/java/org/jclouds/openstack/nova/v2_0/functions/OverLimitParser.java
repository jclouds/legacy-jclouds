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
package org.jclouds.openstack.nova.v2_0.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.json.Json;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * The expected body contains the time as in this (real) response
 * 
 * <pre>
 *   {
 * "overLimit" : {
 *  "code" : 413,
 *  "message" : "OverLimit Retry...",
 *  "details" : "Error Details...",
 *  "retryAt" : "2012-11-14T21:51:28UTC"
 *  }
 * }
 * </pre>
 * 
 * or
 * 
 * <pre>
 *    {
 *      "overLimit": {
 *        "message": "This request was rate-limited.",
 *        "code": 413,
 *        "retryAfter": "54",
 *        "details": "Only 1 POST request(s) can be made to \"*\" every minute."
 *      }
 *    }
 * </pre>
 * 
 * @author Adrian Cole, Steve Loughran
 * 
 */
public class OverLimitParser implements Function<String, Map<String, String>> {
   
   @Resource
   private Logger logger = Logger.NULL;
   private final Json json;

   @Inject
   public OverLimitParser(Json json) {
      this.json = checkNotNull(json, "json");
   }

   private static class Holder {
      Map<String, String> overLimit = ImmutableMap.of();
   }

   /**
    * parses or returns an empty map.
    */
   @Override
   public Map<String, String> apply(String in) {
      try {
         return json.fromJson(in, OverLimitParser.Holder.class).overLimit;
      } catch (RuntimeException e) {
         // an error was raised during parsing -which can include badly
         // formatted fields.
         logger.error("Failed to parse " + in + "", e);
         return ImmutableMap.of();
      }
   }
}
