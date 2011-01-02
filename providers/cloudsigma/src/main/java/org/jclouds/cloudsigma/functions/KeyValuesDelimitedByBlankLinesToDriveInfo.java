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

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class KeyValuesDelimitedByBlankLinesToDriveInfo implements Function<HttpResponse, DriveInfo> {
   private final ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet setParser;

   @Inject
   public KeyValuesDelimitedByBlankLinesToDriveInfo(ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet setParser) {
      this.setParser = setParser;
   }

   @Override
   public DriveInfo apply(HttpResponse response) {
      Set<DriveInfo> drives = setParser.apply(response);
      if (drives.size() == 0)
         return null;
      return Iterables.get(drives, 0);
   }
}