/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.cloudservers.domain;

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
 * event you exceed the thresholds established for your identity, a 413 Rate Control HTTP response
 * will be returned with a Reply-After header to notify the client when theyagain.
 * 
 * @author Adrian Cole
 */
public class RateLimit {

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((regex == null) ? 0 : regex.hashCode());
      result = prime * result + remaining;
      result = prime * result + (int) (resetTime ^ (resetTime >>> 32));
      result = prime * result + ((unit == null) ? 0 : unit.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
      result = prime * result + value;
      result = prime * result + ((verb == null) ? 0 : verb.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      RateLimit other = (RateLimit) obj;
      if (regex == null) {
         if (other.regex != null)
            return false;
      } else if (!regex.equals(other.regex))
         return false;
      if (remaining != other.remaining)
         return false;
      if (resetTime != other.resetTime)
         return false;
      if (unit != other.unit)
         return false;
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      if (value != other.value)
         return false;
      if (verb == null) {
         if (other.verb != null)
            return false;
      } else if (!verb.equals(other.verb))
         return false;
      return true;
   }

   private String uri;
   private String regex;
   private int remaining;
   private long resetTime;
   private RateLimitUnit unit;
   private int value;
   private String verb;

   // for deserializer
   public RateLimit() {

   }

   public RateLimit(String uri, String regex, int remaining, long resetTime, RateLimitUnit unit, int value, String verb) {
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

   public String getVerb() {
      return verb;
   }

   @Override
   public String toString() {
      return "[uri=" + uri + ", regex=" + regex + ", remaining=" + remaining + ", resetTime=" + resetTime + ", unit="
            + unit + ", value=" + value + ", verb=" + verb + "]";
   }

}
