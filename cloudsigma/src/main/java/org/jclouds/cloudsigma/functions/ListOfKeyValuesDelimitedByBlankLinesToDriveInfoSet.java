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
import org.jclouds.http.functions.ReturnStringIf2xx;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet implements Function<HttpResponse, Set<DriveInfo>> {
   private final ReturnStringIf2xx returnStringIf200;
   private final ListOfKeyValuesDelimitedByBlankLinesToListOfMaps mapConverter;
   private final MapToDriveInfo mapToDrive;

   @Inject
   ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet(ReturnStringIf2xx returnStringIf200,
         ListOfKeyValuesDelimitedByBlankLinesToListOfMaps mapConverter, MapToDriveInfo mapToDrive) {
      this.returnStringIf200 = returnStringIf200;
      this.mapConverter = mapConverter;
      this.mapToDrive = mapToDrive;
   }

   @Override
   public Set<DriveInfo> apply(HttpResponse response) {
      String text = returnStringIf200.apply(response);
      if (text == null || text.trim().equals(""))
         return ImmutableSet.<DriveInfo> of();
      return ImmutableSet.copyOf(Iterables.transform(mapConverter.apply(text), mapToDrive));
   }
}