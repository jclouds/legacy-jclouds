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
package org.jclouds.vcloud.terremark.functions;

import java.lang.reflect.Constructor;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponseException;

import com.google.common.base.Function;

/**
 * There's no current way to determine if an IP is the default outbound one. In this case, we may
 * get errors on deleting an IP, which are ok.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnVoidOnDeleteDefaultIp implements Function<Exception, Void> {

   static final Void v;
   static {
      Constructor<Void> cv;
      try {
         cv = Void.class.getDeclaredConstructor();
         cv.setAccessible(true);
         v = cv.newInstance();
      } catch (Exception e) {
         throw new Error("Error setting up class", e);
      }
   }

   public Void apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException hre = (HttpResponseException) from;
         if (hre.getResponse().getStatusCode() == 503
                  || hre.getResponse().getStatusCode() == 401
                  || hre.getMessage().matches(
                           ".*Cannot release this Public IP as it is default oubound IP.*"))
            return v;
      }
      return null;
   }
}