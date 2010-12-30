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

package org.jclouds.vcloud.terremark.functions;

import static org.jclouds.util.Throwables2.propagateOrNull;

import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Function;

/**
 * There's no current way to determine if an IP is the default outbound one. In this case, we may
 * get errors on deleting an IP, which are ok.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnVoidOnDeleteDefaultIp implements Function<Exception, Void> {
   public static final Pattern MESSAGE_PATTERN = Pattern
            .compile(".*Cannot release this Public IP as it is default oubound IP.*");

   public Void apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException hre = (HttpResponseException) from;
         if (hre.getResponse().getStatusCode() == 503 || hre.getResponse().getStatusCode() == 401
                  || MESSAGE_PATTERN.matcher(hre.getMessage()).matches())
            return null;
      } else if (from instanceof AuthorizationException) {
         return null;
      }
      return Void.class.cast(propagateOrNull(from));
   }
}