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
package org.jclouds.vcloud.binders;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.options.CloneVAppOptions;

import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCloneVAppParamsToXmlPayload extends BindCloneParamsToXmlPayload<CloneVAppOptions> {

   @Inject
   public BindCloneVAppParamsToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      super(stringBinder, ns, schema);
   }

   @Override
   protected Class<CloneVAppOptions> getOptionClass() {
      return CloneVAppOptions.class;
   }

   @Override
   protected String getRootElement() {
      return "CloneVAppParams";
   }

   @Override
   protected String getSourceMediaType() {
      return VCloudMediaType.VAPP_XML;
   }

   protected XMLBuilder buildRoot(String name, CloneVAppOptions options) {
      return super.buildRoot(name, options).a("deploy", options.isDeploy() + "").a("powerOn", options.isPowerOn() + "");
   }

}
