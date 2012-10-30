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
package org.jclouds.rimuhosting.miro.domain;

import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

/**
 * @author Ivan Meredith
 */
public class ResizeResponse extends RimuHostingResponse {
   private Server about_order;
   private ResizeResponse resource_change_result;

   public ResizeResponse getResourceChangeResult() {
      return resource_change_result;
   }

   public void setResourceChangeResult(ResizeResponse resource_change_result) {
      this.resource_change_result = resource_change_result;
   }

   public Server getAboutOrder() {
      return about_order;
   }

   public void setAboutOrder(Server about_orders) {
      this.about_order = about_orders;
   }
}
