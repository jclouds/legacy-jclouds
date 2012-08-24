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
package org.jclouds.vcloud.director.v1_5.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Resource;
import org.jclouds.xml.XMLParser;

/**
 * Changes a String to the crufty {@link MetadataValue type}
 */
@Singleton
public class BindStringAsMetadataValue extends BindToXMLPayload {
   @XmlRootElement(name = "MetadataValue")
   public static class MetadataValue extends Resource {
      public static final String MEDIA_TYPE = VCloudDirectorMediaType.METADATA_VALUE;

      public MetadataValue() {

      }

      public MetadataValue(String value) {
         super(Resource.builder());
         this.value = value;
      }

      @XmlElement(name = "Value", required = true)
      private String value;

   }

   @Inject
   public BindStringAsMetadataValue(final XMLParser xmlParser) {
      super(xmlParser);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      return super.bindToRequest(request, new MetadataValue(checkNotNull(input, "input").toString()));
   }
}
