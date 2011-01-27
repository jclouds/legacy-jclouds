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

package org.jclouds.cloudsigma.functions;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.ProfileInfo;
import org.jclouds.cloudsigma.domain.ProfileType;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MapToProfileInfo implements Function<Map<String, String>, ProfileInfo> {

   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public ProfileInfo apply(Map<String, String> from) {
      if (from.size() == 0)
         return null;
      if (from.size() == 0)
         return null;
      ProfileInfo.Builder builder = new ProfileInfo.Builder();
      builder.uuid(from.get("uuid"));
      builder.email(from.get("email"));
      builder.firstName(from.get("first_name"));
      builder.lastName(from.get("last_name"));
      builder.nickName(from.get("nick_name"));
      builder.type(ProfileType.fromValue(from.get("type")));
      try {
         return builder.build();
      } catch (NullPointerException e) {
         logger.trace("entry missing data: %s; %s", e.getMessage(), from);
         return null;
      }
   }
}