/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark.domain;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InternetServiceConfiguration extends NodeConfiguration {

   private String timeout = null;

   /**
    * One factor that affects the load balancing is the timeout, or persistence. The timeout is the
    * number of minutes that the task can remain idle on a given node before the underlying software
    * attempts to rebalance the node by possibly assigning that task to a different node. The
    * default timeout is one minute. You can change the value using this call.
    */
   public InternetServiceConfiguration changeTimeoutTo(int timeout) {
      this.timeout = timeout + "";
      return this;
   }

   @Override
   public InternetServiceConfiguration changeDescriptionTo(String description) {
      return (InternetServiceConfiguration) super.changeDescriptionTo(description);
   }

   @Override
   public InternetServiceConfiguration changeNameTo(String name) {
      return (InternetServiceConfiguration) super.changeNameTo(name);
   }

   @Override
   public InternetServiceConfiguration disableTraffic() {
      return (InternetServiceConfiguration) super.disableTraffic();
   }

   @Override
   public InternetServiceConfiguration enableTraffic() {
      return (InternetServiceConfiguration) super.enableTraffic();
   }

   public static class Builder {
      /**
       * @see InternetServiceConfiguration#changeTimeoutTo(int)
       */
      public static InternetServiceConfiguration changeTimeoutTo(int timeout) {
         InternetServiceConfiguration options = new InternetServiceConfiguration();
         return options.changeTimeoutTo(timeout);
      }

      /**
       * @see InternetServiceConfiguration#changeNameTo(String)
       */
      public static InternetServiceConfiguration changeNameTo(String name) {
         InternetServiceConfiguration options = new InternetServiceConfiguration();
         return options.changeNameTo(name);
      }

      /**
       * @see InternetServiceConfiguration#changeDescriptionTo(String)
       */
      public static InternetServiceConfiguration changeDescriptionTo(String description) {
         InternetServiceConfiguration options = new InternetServiceConfiguration();
         return options.changeDescriptionTo(description);
      }

      /**
       * @see InternetServiceConfiguration#enableTraffic()
       */
      public static InternetServiceConfiguration enableTraffic() {
         InternetServiceConfiguration options = new InternetServiceConfiguration();
         return options.enableTraffic();
      }

      /**
       * @see InternetServiceConfiguration#disableTraffic()
       */
      public static InternetServiceConfiguration disableTraffic() {
         InternetServiceConfiguration options = new InternetServiceConfiguration();
         return options.disableTraffic();
      }
   }

   public String getTimeout() {
      return timeout;
   }

}
