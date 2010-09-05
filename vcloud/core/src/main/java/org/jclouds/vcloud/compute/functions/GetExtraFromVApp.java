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

package org.jclouds.vcloud.compute.functions;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetExtraFromVApp implements Function<VApp, Map<String, String>> {
   private final GetExtraFromVm getExtraFromVm;

   @Inject
   GetExtraFromVApp(GetExtraFromVm getExtraFromVm) {
      this.getExtraFromVm = getExtraFromVm;
   }

   @Resource
   protected Logger logger = Logger.NULL;

   public Map<String, String> apply(VApp vApp) {
      // TODO make this work with composite vApps
      return vApp.getChildren().size() == 0 ? ImmutableMap.<String, String> of() : getExtraFromVm.apply(Iterables.get(
               vApp.getChildren(), 0));
   }
}