/**
   * Licensed to jclouds, Inc. (jclouds) under one or more
   * contributor license agreements.  See the NOTICE file
   * distributed with this work for additional information
   * regarding copyright ownership.  jclouds licenses this file
   * to you under the Apache License, Version 2.0 (the
   * "License"); you may not use this file except in compliance
   * with the License.  You may obtain a copy of the License at
   *
   *   http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing,
   * software distributed under the License is distributed on an
   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   * KIND, either express or implied.  See the License for the
   * specific language governing permissions and limitations
   * under the License.
   */
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options for the Iso deleteIso method.
 *
 * @see org.jclouds.cloudstack.features.IsoClient#deleteIso
 * @see org.jclouds.cloudstack.features.IsoAsyncClient#deleteIso
 * @author Richard Downer
 */
public class DeleteIsoOptions extends BaseHttpRequestOptions {

   public static final DeleteIsoOptions NONE = new DeleteIsoOptions(); 

   /**
    * @param zoneId the ID of the zone of the ISO file. If not specified, the ISO will be deleted from all the zones
    */
   public DeleteIsoOptions zoneId(long zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param zoneId the ID of the zone of the ISO file. If not specified, the ISO will be deleted from all the zones
       */
      public static DeleteIsoOptions zoneId(long zoneId) {
         return new DeleteIsoOptions().zoneId(zoneId);
      }
   }

}
