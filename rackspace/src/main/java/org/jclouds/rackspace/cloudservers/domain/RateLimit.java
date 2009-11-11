/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudservers.domain;

import javax.ws.rs.HttpMethod;

/**
 * 
 * RateLimit.
 * <p/>
 * we specify rate limits in terms of both a human readable wild-card URI and a machine processable
 * regular expression. The regular expression boundary matcher '^' takes affect after the root URI
 * path. For example, the regular expression ^/servers would match the bolded portion of the
 * following URI: https://servers.api.rackspacecloud.com/v1.0/3542812 /servers .
 * <p/>
 * Rate limits are applied in order relative to the verb, going from least to most specific. For
 * example, although the threshold for POST to /servers is 25 per day, one cannot POST to /servers
 * more than 10 times within a single minute because the rate limits for any POST is 10/min. In the
 * event you exceed the thresholds established for your account, a 413 Rate Control HTTP response
 * will be returned with a Reply-After header to notify the client when theyagain.
 * 
 * @author Adrian Cole
 */
public class RateLimit {

   private final String uri;
   private final String regex;
   private final int remaining;
   private final long resetTime;
   private final RateLimitUnit unit;
   private final int value;
   private final HttpMethod verb;

   public RateLimit(String uri, String regex, int remaining, long resetTime, RateLimitUnit unit,
            int value, HttpMethod verb) {
      this.uri = uri;
      this.regex = regex;
      this.remaining = remaining;
      this.resetTime = resetTime;
      this.unit = unit;
      this.value = value;
      this.verb = verb;
   }

   public String getUri() {
      return uri;
   }

   public String getRegex() {
      return regex;
   }

   public int getRemaining() {
      return remaining;
   }

   public long getResetTime() {
      return resetTime;
   }

   public RateLimitUnit getUnit() {
      return unit;
   }

   public int getValue() {
      return value;
   }

   public HttpMethod getVerb() {
      return verb;
   }

}
