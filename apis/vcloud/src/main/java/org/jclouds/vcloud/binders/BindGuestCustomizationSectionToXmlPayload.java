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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.util.Properties;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.logging.Logger;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.vcloud.domain.GuestCustomizationSection;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindGuestCustomizationSectionToXmlPayload extends BindToStringPayload {
   @Resource
   protected Logger logger = Logger.NULL;

   protected final String ns;
   protected final String schema;

   @Inject
   public BindGuestCustomizationSectionToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      this.ns = ns;
      this.schema = schema;
   }
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      checkArgument(checkNotNull(payload, "GuestCustomizationSection") instanceof GuestCustomizationSection,
               "this binder is only valid for GuestCustomizationSection!");
      GuestCustomizationSection guest = GuestCustomizationSection.class.cast(payload);
      XMLBuilder guestCustomizationSection;
      try {
         guestCustomizationSection = XMLBuilder.create("GuestCustomizationSection").a("xmlns", ns).a("xmlns:ovf",
                  "http://schemas.dmtf.org/ovf/envelope/1").a("type", guest.getType()).a("href",
                  guest.getHref().toASCIIString()).a("ovf:required", "false");
         guestCustomizationSection.e("ovf:Info").t(guest.getInfo());

         if (guest.isEnabled() != null)
            guestCustomizationSection.e("Enabled").t(guest.isEnabled().toString());
         if (guest.shouldChangeSid() != null)
            guestCustomizationSection.e("ChangeSid").t(guest.shouldChangeSid().toString());
         if (guest.getVirtualMachineId() != null)
            guestCustomizationSection.e("VirtualMachineId").t(guest.getVirtualMachineId().toString());
         if (guest.isJoinDomainEnabled() != null)
            guestCustomizationSection.e("JoinDomainEnabled").t(guest.isJoinDomainEnabled().toString());
         if (guest.shouldUseOrgSettings() != null)
            guestCustomizationSection.e("UseOrgSettings").t(guest.shouldUseOrgSettings().toString());
         if (guest.getDomainName() != null)
            guestCustomizationSection.e("DomainName").t(guest.getDomainName().toString());
         if (guest.getDomainUserName() != null)
            guestCustomizationSection.e("DomainUserName").t(guest.getDomainUserName().toString());
         if (guest.getDomainUserPassword() != null)
            guestCustomizationSection.e("DomainUserPassword").t(guest.getDomainUserPassword().toString());
         if (guest.isAdminPasswordEnabled() != null)
            guestCustomizationSection.e("AdminPasswordEnabled").t(guest.isAdminPasswordEnabled().toString());
         if (guest.isAdminPasswordAuto() != null)
            guestCustomizationSection.e("AdminPasswordAuto").t(guest.isAdminPasswordAuto().toString());
         // if (guest.getAdminPassword() != null)
         // guestCustomizationSection.e("AdminPassword").t(guest.getAdminPassword().toString());
         if (guest.isResetPasswordRequired() != null)
            guestCustomizationSection.e("ResetPasswordRequired").t(guest.isResetPasswordRequired().toString());
         if (guest.getCustomizationScript() != null)
            guestCustomizationSection.e("CustomizationScript").t(guest.getCustomizationScript());
         if (guest.getComputerName() != null)
            guestCustomizationSection.e("ComputerName").t(guest.getComputerName().toString());
         if (guest.getEdit() != null)
            guestCustomizationSection.e("Link").a("rel", "edit").a("type", guest.getType()).a("href",
                     guest.getHref().toASCIIString());

         Properties outputProperties = new Properties();
         outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
         request =  super.bindToRequest(request, guestCustomizationSection.asString(outputProperties));
         request.getPayload().getContentMetadata().setContentType(guest.getType());
      } catch (Exception e) {
         Throwables.propagate(e);
      }
      return request;
   }

}
