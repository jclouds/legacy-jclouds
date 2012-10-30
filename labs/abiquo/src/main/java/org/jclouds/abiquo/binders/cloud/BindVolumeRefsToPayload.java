/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.binders.cloud;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.binders.BindRefsToPayload;
import org.jclouds.xml.XMLParser;

/**
 * Bind multiple {@link VolumeManagementDto} objects to the payload of the
 * request as a list of links.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class BindVolumeRefsToPayload extends BindRefsToPayload {
   @Inject
   public BindVolumeRefsToPayload(final XMLParser xmlParser) {
      super(xmlParser);
   }

   @Override
   protected String getRelToUse(final Object input) {
      return "volume";
   }

}
