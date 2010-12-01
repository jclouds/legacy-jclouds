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

import static org.jclouds.util.Utils.renameKey;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elastichosts.domain.DriveData;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DriveDataToMap implements Function<DriveData, Map<String, String>> {
   private final org.jclouds.elastichosts.functions.DriveDataToMap baseDriveToMap;

   @Inject
   public DriveDataToMap(org.jclouds.elastichosts.functions.DriveDataToMap baseDriveToMap) {
      this.baseDriveToMap = baseDriveToMap;
   }

   @Override
   public Map<String, String> apply(DriveData from) {
      return Maps.transformEntries(renameKey(baseDriveToMap.apply(from), "tags", "use"),
            new Maps.EntryTransformer<String, String, String>() {

               @Override
               public String transformEntry(String arg0, String arg1) {
                  return "use".equals(arg0) ? arg1.replace(' ', ',') : arg1;
               }

            });
   }
}