/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.ohai.suppliers;

import java.lang.management.RuntimeMXBean;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class UptimeSecondsSupplier implements Supplier<JsonBall> {

   @Inject
   UptimeSecondsSupplier(RuntimeMXBean runtime) {
      this.runtime = runtime;
   }

   private final RuntimeMXBean runtime;

   @Override
   public JsonBall get() {
      long uptimeInSeconds = runtime.getUptime() / 1000;
      return new JsonBall(uptimeInSeconds);
   }

}