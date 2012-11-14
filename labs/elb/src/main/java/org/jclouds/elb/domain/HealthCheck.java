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
package org.jclouds.elb.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Elastic Load Balancing routinely checks the health of each load-balanced Amazon EC2 instance
 * based on the configurations that you specify. If Elastic Load Balancing finds an unhealthy
 * instance, it stops sending traffic to the instance and reroutes traffic to healthy instances.
 * 
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/DeveloperGuide/ConfigureHealthCheck.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class HealthCheck {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromListener(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected int healthyThreshold = -1;
      protected int interval = -1;
      protected String target;
      protected int timeout = -1;
      protected int unhealthyThreshold = -1;

      /**
       * @see HealthCheck#getHealthyThreshold()
       */
      public T healthyThreshold(int healthyThreshold) {
         this.healthyThreshold = healthyThreshold;
         return self();
      }

      /**
       * @see HealthCheck#getInterval()
       */
      public T interval(int interval) {
         this.interval = interval;
         return self();
      }

      /**
       * @see HealthCheck#getTarget()
       */
      public T target(String target) {
         this.target = target;
         return self();
      }

      /**
       * @see HealthCheck#getTimeout()
       */
      public T timeout(int timeout) {
         this.timeout = timeout;
         return self();
      }

      /**
       * @see HealthCheck#getUnhealthyThreshold()
       */
      public T unhealthyThreshold(int unhealthyThreshold) {
         this.unhealthyThreshold = unhealthyThreshold;
         return self();
      }

      public HealthCheck build() {
         return new HealthCheck(healthyThreshold, interval, target, timeout, unhealthyThreshold);
      }

      public T fromListener(HealthCheck in) {
         return this.healthyThreshold(in.getHealthyThreshold()).interval(in.getInterval()).target(in.getTarget())
                  .timeout(in.getTimeout()).unhealthyThreshold(in.getUnhealthyThreshold());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final int healthyThreshold;
   protected final int interval;
   protected final String target;
   protected final int timeout;
   protected final int unhealthyThreshold;

   protected HealthCheck(int healthyThreshold, int interval, String target, int timeout, int unhealthyThreshold) {
      this.healthyThreshold = checkNonNegative(healthyThreshold, "healthyThreshold");
      this.interval = checkNonNegative(interval, "interval");
      this.target = checkNotNull(target, "target");
      this.timeout = checkNonNegative(timeout, "timeout");
      this.unhealthyThreshold = checkNonNegative(unhealthyThreshold, "unhealthyThreshold");
   }

   static int checkNonNegative(int in, String name) {
      checkArgument(in > 0, "%s must be non-negative", name);
      return in;
   }
   
   /**
    * Specifies the number of consecutive health probe successes required before moving the instance
    * to the Healthy state.
    */
   public int getHealthyThreshold() {
      return healthyThreshold;
   }

   /**
    * Specifies the approximate interval, in seconds, between health checks of an individual
    * instance.
    */
   public int getInterval() {
      return interval;
   }

   /**
    * Specifies the instance being checked. The timeout is either TCP, HTTP, HTTPS, or SSL. The
    * range of valid ports is one (1) through 65535.
    * 
    * <h3>Note</h3>
    * 
    * TCP is the default, specified as a TCP: port pair, for example "TCP:5000". In this case a
    * healthcheck simply attempts to open a TCP connection to the instance on the specified port.
    * Failure to connect within the configured timeout is considered unhealthy. <br/>
    * SSL is also specified as SSL: port pair, for example, SSL:5000. <br/>
    * 
    * For HTTP or HTTPS timeout, the situation is different. You have to include a ping path in the
    * string. HTTP is specified as a HTTP:port;/;PathToPing; grouping, for example
    * "HTTP:80/weather/us/wa/seattle". In this case, a HTTP GET request is issued to the instance on
    * the given port and path. Any answer other than "200 OK" within the timeout period is
    * considered unhealthy.
    * 
    * The total length of the HTTP ping target needs to be 1024 16-bit Unicode characters or less.
    */
   public String getTarget() {
      return target;
   }

   /**
    * Specifies the amount of time, in seconds, during which no response means a failed health
    * probe.
    * 
    * <h3>Note</h3> This value must be less than the Interval value.
    */
   public int getTimeout() {
      return timeout;
   }

   /**
    * Specifies the number of consecutive health probe failures required before moving the instance
    * to the Unhealthy state.
    */
   public int getUnhealthyThreshold() {
      return unhealthyThreshold;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(healthyThreshold, interval, target, timeout, unhealthyThreshold);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      HealthCheck other = (HealthCheck) obj;
      return Objects.equal(this.healthyThreshold, other.healthyThreshold)
               && Objects.equal(this.interval, other.interval) && Objects.equal(this.target, other.target)
               && Objects.equal(this.timeout, other.timeout)
               && Objects.equal(this.unhealthyThreshold, other.unhealthyThreshold);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("healthyThreshold", healthyThreshold).add("interval",
               interval).add("target", target).add("timeout", timeout).add("unhealthyThreshold", unhealthyThreshold);
   }

}
