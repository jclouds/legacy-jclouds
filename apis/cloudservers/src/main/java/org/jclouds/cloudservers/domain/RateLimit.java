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
package org.jclouds.cloudservers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
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

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromRateLimit(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String uri;
      protected String regex;
      protected int remaining;
      protected long resetTime;
      protected RateLimitUnit unit;
      protected int value;
      protected String verb;
   
      /** 
       * @see RateLimit#getUri()
       */
      public T uri(String uri) {
         this.uri = uri;
         return self();
      }

      /** 
       * @see RateLimit#getRegex()
       */
      public T regex(String regex) {
         this.regex = regex;
         return self();
      }

      /** 
       * @see RateLimit#getRemaining()
       */
      public T remaining(int remaining) {
         this.remaining = remaining;
         return self();
      }

      /** 
       * @see RateLimit#getResetTime()
       */
      public T resetTime(long resetTime) {
         this.resetTime = resetTime;
         return self();
      }

      /** 
       * @see RateLimit#getUnit()
       */
      public T unit(RateLimitUnit unit) {
         this.unit = unit;
         return self();
      }

      /** 
       * @see RateLimit#getValue()
       */
      public T value(int value) {
         this.value = value;
         return self();
      }

      /** 
       * @see RateLimit#getVerb()
       */
      public T verb(String verb) {
         this.verb = verb;
         return self();
      }

      public RateLimit build() {
         return new RateLimit(uri, regex, remaining, resetTime, unit, value, verb);
      }
      
      public T fromRateLimit(RateLimit in) {
         return this
                  .uri(in.getUri())
                  .regex(in.getRegex())
                  .remaining(in.getRemaining())
                  .resetTime(in.getResetTime())
                  .unit(in.getUnit())
                  .value(in.getValue())
                  .verb(in.getVerb());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String uri;
   private final String regex;
   private final int remaining;
   private final long resetTime;
   private final RateLimitUnit unit;
   private final int value;
   private final String verb;

   @ConstructorProperties({
      "uri", "regex", "remaining", "resetTime", "unit", "value", "verb"
   })
   protected RateLimit(String uri, @Nullable String regex, int remaining, long resetTime, @Nullable RateLimitUnit unit,
                       int value, @Nullable String verb) {
      this.uri = checkNotNull(uri, "uri");
      this.regex = regex;
      this.remaining = remaining;
      this.resetTime = resetTime;
      this.unit = unit;
      this.value = value;
      this.verb = verb;
   }

   public String getUri() {
      return this.uri;
   }

   @Nullable
   public String getRegex() {
      return this.regex;
   }

   public int getRemaining() {
      return this.remaining;
   }

   public long getResetTime() {
      return this.resetTime;
   }

   @Nullable
   public RateLimitUnit getUnit() {
      return this.unit;
   }

   public int getValue() {
      return this.value;
   }

   @Nullable
   public String getVerb() {
      return this.verb;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(uri, regex, remaining, resetTime, unit, value, verb);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      RateLimit that = RateLimit.class.cast(obj);
      return Objects.equal(this.uri, that.uri)
               && Objects.equal(this.regex, that.regex)
               && Objects.equal(this.remaining, that.remaining)
               && Objects.equal(this.resetTime, that.resetTime)
               && Objects.equal(this.unit, that.unit)
               && Objects.equal(this.value, that.value)
               && Objects.equal(this.verb, that.verb);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("uri", uri).add("regex", regex).add("remaining", remaining).add("resetTime", resetTime).add("unit", unit).add("value", value).add("verb", verb);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
