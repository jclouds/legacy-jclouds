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

import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.inject.internal.Maps;

/**
 * 
 * Gathers Ohai data from the JVM. Note that this is intended to be Google App
 * Engine compatible, so please do not access the network, JMX, or other classes
 * not on the whitelist.
 * 
 * @author Adrian Cole
 */
@Singleton
public class WhiteListCompliantJVM implements Supplier<Map<String, JsonBall>> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final Json json;
   private final Provider<Long> nanoTimeProvider;
   private final Provider<Properties> systemPropertiesProvider;

   @Inject
   public WhiteListCompliantJVM(Json json, @Named("nanoTime") Provider<Long> nanoTimeProvider,
         @Named("systemProperties") Provider<Properties> systemPropertiesProvider) {
      this.json = checkNotNull(json, "json");
      this.nanoTimeProvider = checkNotNull(nanoTimeProvider, "nanoTimeProvider");
      this.systemPropertiesProvider = checkNotNull(systemPropertiesProvider, "systemPropertiesProvider");
   }

   public Map<String, JsonBall> get() {
      Map<String, JsonBall> returnVal = Maps.newLinkedHashMap();
      Properties systemProperties = systemPropertiesProvider.get();
      String now = nanoTimeProvider.get() + "";
      StringBuilder nowBuilder = new StringBuilder(now).insert(now.length() - 6, '.');
      while (nowBuilder.lastIndexOf("0") != -1 && nowBuilder.lastIndexOf("0") == nowBuilder.length() - 1)
         nowBuilder.deleteCharAt(nowBuilder.length() - 1);
      now = nowBuilder.toString();

      returnVal.put("ohai_time", new JsonBall(now));
      returnVal.put("java", new JsonBall(json.toJson(systemProperties)));

      String platform = systemProperties.getProperty("os.name");
      platform = platform.replaceAll("[ -]", "").toLowerCase();

      returnVal.put("platform", new JsonBall(platform));
      returnVal.put("platform_version", new JsonBall(systemProperties.getProperty("os.version")));

      returnVal.put("ohai_time", new JsonBall(now));
      returnVal.put("current_user", new JsonBall(systemProperties.getProperty("user.name")));
      return returnVal;
   }
}