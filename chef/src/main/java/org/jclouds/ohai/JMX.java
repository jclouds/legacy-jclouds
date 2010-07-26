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
package org.jclouds.ohai;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.management.RuntimeMXBean;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

/**
 * 
 * Gathers Ohai data from the JVM.
 * 
 * @author Adrian Cole
 */
@Singleton
public class JMX implements Supplier<Map<String, JsonBall>> {

   @Resource
   protected Logger logger = Logger.NULL;
   private final Provider<RuntimeMXBean> runtimeSupplier;

   @Inject
   public JMX(Provider<RuntimeMXBean> runtimeSupplier) {
      this.runtimeSupplier = checkNotNull(runtimeSupplier, "runtimeSupplier");
   }

   public Map<String, JsonBall> get() {
      RuntimeMXBean runtime = runtimeSupplier.get();
      Map<String, JsonBall> automatic = Maps.newLinkedHashMap();
      long uptimeInSeconds = runtime.getUptime() / 1000;
      automatic.put("uptime_seconds", new JsonBall(uptimeInSeconds));
      return automatic;
   }
}