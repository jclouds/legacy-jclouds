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
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindNetworkConnectionSectionToXmlPayload extends BindToStringPayload {
   @Resource
   protected Logger logger = Logger.NULL;

   protected final String ns;
   protected final String schema;

   @Inject
   public BindNetworkConnectionSectionToXmlPayload(BindToStringPayload stringBinder,
         @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      this.ns = ns;
      this.schema = schema;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      checkArgument(checkNotNull(payload, "NetworkConnectionSection") instanceof NetworkConnectionSection,
            "this binder is only valid for NetworkConnectionSection!");
      NetworkConnectionSection net = NetworkConnectionSection.class.cast(payload);
      XMLBuilder networkConnectionSection;
      try {
         networkConnectionSection = XMLBuilder.create("NetworkConnectionSection").a("xmlns", ns)
               .a("xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1").a("type", net.getType())
               .a("href", net.getHref().toASCIIString()).a("ovf:required", "false");
         networkConnectionSection.e("ovf:Info").t(net.getInfo());

         if (net.getPrimaryNetworkConnectionIndex() != null)
            networkConnectionSection.e("PrimaryNetworkConnectionIndex").t(
                  net.getPrimaryNetworkConnectionIndex().toString());
         for (NetworkConnection networkConnection : net.getConnections()) {
            XMLBuilder networkConnectionSectionChild = networkConnectionSection.e("NetworkConnection").a("network",
                  networkConnection.getNetwork());
            networkConnectionSectionChild.e("NetworkConnectionIndex").t(
                  networkConnection.getNetworkConnectionIndex() + "");
            if (networkConnection.getExternalIpAddress() != null)
               networkConnectionSectionChild.e("ExternalIpAddress").t(networkConnection.getExternalIpAddress());
            if (networkConnection.getIpAddress() != null)
               networkConnectionSectionChild.e("IpAddress").t(networkConnection.getIpAddress());
            networkConnectionSectionChild.e("IsConnected").t(networkConnection.isConnected() + "");
            if (networkConnection.getMACAddress() != null)
               networkConnectionSectionChild.e("MACAddress").t(networkConnection.getMACAddress());
            if (networkConnection.getIpAddressAllocationMode() != null)
               networkConnectionSectionChild.e("IpAddressAllocationMode").t(
                     networkConnection.getIpAddressAllocationMode().toString());
         }

         if (net.getEdit() != null)
            networkConnectionSection.e("Link").a("rel", "edit").a("type", net.getType())
                  .a("href", net.getHref().toASCIIString());

         Properties outputProperties = new Properties();
         outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
         request = super.bindToRequest(request, networkConnectionSection.asString(outputProperties));
         request.getPayload().getContentMetadata().setContentType(net.getType());
      } catch (Exception e) {
         Throwables.propagate(e);
      }
      return request;
   }

}
