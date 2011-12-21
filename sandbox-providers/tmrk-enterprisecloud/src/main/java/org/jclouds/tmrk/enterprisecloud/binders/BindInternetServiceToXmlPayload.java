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
package org.jclouds.tmrk.enterprisecloud.binders;

import com.jamesmurty.utils.XMLBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.service.Protocol;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetServicePersistenceType;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * For use with {@see org.jclouds.tmrk.enterprisecloud.features.InternetServiceClient#editInternetService}
 * @author Jason King
 */
@Singleton
public class BindInternetServiceToXmlPayload implements Binder {

   private final BindToStringPayload stringBinder;
   private final String rootElement;
   
   @Inject
   BindInternetServiceToXmlPayload(BindToStringPayload stringBinder) {
      this(stringBinder,"InternetService");
   }
   
   protected BindInternetServiceToXmlPayload(BindToStringPayload stringBinder, String rootElement) {
      this.stringBinder = stringBinder;
      this.rootElement = rootElement;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object key) {
      checkArgument(checkNotNull(key, "key") instanceof InternetService, "this binder is only valid for InternetService instances");
      checkNotNull(request, "request");
      InternetService data = InternetService.class.cast(key);

      String payload = createXMLPayload(data);
      return stringBinder.bindToRequest(request, payload);
   }
   
   private String createXMLPayload(InternetService data) {
      try {
         Properties outputProperties = new Properties();
         outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
         
         final String name = checkNotNull(data.getName(), "name");
         final Protocol protocol = data.getProtocol();
         final int port = data.getPort();
         final String enabled = Boolean.toString(data.isEnabled());
         final String description = data.getDescription();
         final InternetServicePersistenceType persistence = data.getPersistence();
         final String redirectUrl = data.getRedirectUrl();
         final NamedResource trustedNetworkGroup = data.getTrustedNetworkGroup();
         final NamedResource backupInternetService = data.getBackupInternetService();

         XMLBuilder builder = XMLBuilder.create(rootElement).a("name", name);
         
         if(protocol!=null) {
            builder = builder.e("Protocol").t(protocol.value()).up();
         }

         if(port>0) {
            builder = builder.e("Port").t(Integer.toString(port)).up();
         }
         
         builder = builder.e("Enabled").t(enabled).up();

         if(description!=null) {
            builder = builder.e("Description").t(description).up();
         }
         //TODO: Public IP
         builder = persistence(builder,persistence);

         if(redirectUrl!=null) {
            builder = builder.e("RedirectUrl").t(redirectUrl);
         }
         
         //TODO: Monitor

         if(trustedNetworkGroup!=null) {
            final String href = trustedNetworkGroup.getHref().toString();
            String groupName = trustedNetworkGroup.getName();
            String type = trustedNetworkGroup.getType();
            builder = builder.e("TrustedNetworkGroup").a("href",href).a("name",groupName).a("type", type).up();
         }

         if(backupInternetService!=null) {
            final String href = backupInternetService.getHref().toString();
            String groupName = backupInternetService.getName();
            String type = backupInternetService.getType();
            builder = builder.e("BackupInternetService").a("href",href).a("name",groupName).a("type",type).up();
         }
         
         //TODO: NodeServices
         
         return builder.asString(outputProperties);
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (TransformerException t) {
         throw new RuntimeException(t);
      }
   }

   private XMLBuilder persistence(XMLBuilder in, InternetServicePersistenceType persistenceType) {
      checkNotNull(persistenceType,"persistenceType");

      final InternetServicePersistenceType.PersistenceType type = persistenceType.getPersistenceType();
      final int timeout = persistenceType.getTimeout();

      in = in.e("Persistence").e("Type").t(type.value()).up();
      if(!type.equals(InternetServicePersistenceType.PersistenceType.NONE) && timeout > -1 ) {
         in = in.e("Timeout").t(Integer.toString(timeout)).up();
      }
      return in.up();
   }
}
