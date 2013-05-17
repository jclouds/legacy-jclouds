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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * The load balancing service includes a health monitoring operation which periodically checks your back-end nodes to 
 * ensure they are responding correctly. If a node is not responding, it is removed from rotation until the health 
 * monitor determines that the node is functional. In addition to being performed periodically, the health check also 
 * is performed against every node that is added to ensure that the node is operating properly before allowing it to 
 * service traffic. Only one health monitor is allowed to be enabled on a load balancer at a time.
 * </p>
 * As part of your strategy for monitoring connections, you should consider defining secondary nodes that provide 
 * failover for effectively routing traffic in case the primary node fails. This is an additional feature that will 
 * ensure you remain up in case your primary node fails.
 * <p/>
 * @author Everett Toews
 */
public class HealthMonitor {

   private final Type type;
   private final int delay;
   private final int timeout;
   private final int attemptsBeforeDeactivation;
   private final Optional<String> bodyRegex;
   private final Optional<String> statusRegex;
   private final Optional<String> path;
   private final Optional<String> hostHeader;

   @ConstructorProperties({
      "type", "delay", "timeout", "attemptsBeforeDeactivation", "bodyRegex", "statusRegex", "path", "hostHeader"
   })
   protected HealthMonitor(Type type, int delay, int timeout, int attemptsBeforeDeactivation, 
         @Nullable String bodyRegex, @Nullable String statusRegex, @Nullable String path, 
         @Nullable String hostHeader) {
      this.type = checkNotNull(type, "type");
      this.delay = delay;
      this.timeout = timeout;
      this.attemptsBeforeDeactivation = attemptsBeforeDeactivation;
      this.bodyRegex = Optional.fromNullable(bodyRegex);
      this.statusRegex = Optional.fromNullable(statusRegex);
      this.path = Optional.fromNullable(path);
      this.hostHeader = Optional.fromNullable(hostHeader);
      
      if (!isValid())
         if (type.equals(Type.CONNECT))
            throw new IllegalArgumentException("Only delay, timeout, and attemptsBeforeDeactivation must be set.");
         else
            throw new IllegalArgumentException("At least delay, timeout, attemptsBeforeDeactivation, path and " +
            		"one or both of bodyRegex and statusRegex must be set.");
   }

   public Type getType() {
      return type;
   }

   public int getDelay() {
      return delay;
   }

   public int getTimeout() {
      return timeout;
   }

   public int getAttemptsBeforeDeactivation() {
      return attemptsBeforeDeactivation;
   }

   public Optional<String> getBodyRegex() {
      return bodyRegex;
   }

   public Optional<String> getStatusRegex() {
      return statusRegex;
   }

   public Optional<String> getPath() {
      return path;
   }

   public Optional<String> getHostHeader() {
      return hostHeader;
   }
   
   /**
    * @return true if this HealthMonitor is valid, false otherwise
    */
   public boolean isValid() {
      boolean required = delay != 0 && timeout != 0 && attemptsBeforeDeactivation != 0;
      
      if (type.equals(Type.CONNECT))
         return required && !path.isPresent() && !statusRegex.isPresent() 
                && !bodyRegex.isPresent() && !hostHeader.isPresent();
      else
         return required && path.isPresent() && (statusRegex.isPresent() || bodyRegex.isPresent());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(type, delay, timeout, attemptsBeforeDeactivation, bodyRegex, statusRegex, path, 
            hostHeader);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      HealthMonitor that = HealthMonitor.class.cast(obj);

      return Objects.equal(this.type, that.type) && Objects.equal(this.delay, that.delay)
            && Objects.equal(this.timeout, that.timeout)
            && Objects.equal(this.attemptsBeforeDeactivation, that.attemptsBeforeDeactivation)
            && Objects.equal(this.bodyRegex, that.bodyRegex) && Objects.equal(this.statusRegex, that.statusRegex)
            && Objects.equal(this.path, that.path) && Objects.equal(this.hostHeader, that.hostHeader);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("type", type).add("delay", delay)
            .add("timeout", timeout).add("attemptsBeforeDeactivation", attemptsBeforeDeactivation)
            .add("bodyRegex", bodyRegex.orNull()).add("statusRegex", statusRegex.orNull()).add("path", path.orNull())
            .add("hostHeader", hostHeader.orNull());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * Every health monitor has a type attribute to signify what kind of monitor it is.
    */
   public static enum Type {
      CONNECT, HTTP, HTTPS, UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         }
         catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static class Builder {
      private Type type;
      private int delay;
      private int timeout;
      private int attemptsBeforeDeactivation;
      private String bodyRegex;
      private String statusRegex;
      private String path;
      private String hostHeader;

      /** 
       * Type of the health monitor. Must be specified as CONNECT to monitor connections.
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /** 
       * Required. The minimum number of seconds to wait before executing the health monitor. 
       * Must be a number between 1 and 3600.
       */
      public Builder delay(int delay) {
         this.delay = delay;
         return this;
      }

      /** 
       * Required. Maximum number of seconds to wait for a connection to be established before timing out. 
       * Must be a number between 1 and 300.
       */
      public Builder timeout(int timeout) {
         this.timeout = timeout;
         return this;
      }

      /** 
       * Required. Number of permissible monitor failures before removing a node from rotation. 
       * Must be a number between 1 and 10.
       */
      public Builder attemptsBeforeDeactivation(int attemptsBeforeDeactivation) {
         this.attemptsBeforeDeactivation = attemptsBeforeDeactivation;
         return this;
      }

      /**
       * Required (if using HTTP/S). A regular expression that will be used to evaluate the contents of the body of 
       * the response.
       */
      public Builder bodyRegex(String bodyRegex) {
         this.bodyRegex = bodyRegex;
         return this;
      }

      /**
       * Required (if using HTTP/S). A regular expression that will be used to evaluate the HTTP status code returned
       * in the response.
       */
      public Builder statusRegex(String statusRegex) {
         this.statusRegex = statusRegex;
         return this;
      }

      /**
       * Required (if using HTTP/S). The HTTP path that will be used in the sample request.
       */
      public Builder path(String path) {
         this.path = path;
         return this;
      }

      /**
       * Optional (if using HTTP/S). The name of a host for which the health monitors will check.
       */
      public Builder hostHeader(String hostHeader) {
         this.hostHeader = hostHeader;
         return this;
      }

      public HealthMonitor build() {
         return new HealthMonitor(type, delay, timeout, attemptsBeforeDeactivation, bodyRegex, statusRegex, path,
               hostHeader);
      }

      public Builder from(HealthMonitor in) {
         return this.type(in.getType()).delay(in.getDelay()).timeout(in.getTimeout())
               .attemptsBeforeDeactivation(in.getAttemptsBeforeDeactivation()).bodyRegex(in.getBodyRegex().orNull())
               .statusRegex(in.getStatusRegex().orNull()).path(in.getPath().orNull())
               .hostHeader(in.getHostHeader().orNull());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
