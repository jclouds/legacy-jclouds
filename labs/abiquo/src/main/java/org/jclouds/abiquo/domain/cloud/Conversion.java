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

package org.jclouds.abiquo.domain.cloud;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWithTasksWrapper;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.abiquo.rest.internal.ExtendedUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.ConversionDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.google.inject.TypeLiteral;

/**
 * Adds high level functionality to {@link ConversionDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/Conversion+Resource">
 *      http://community.abiquo.com/display/ABI20/Conversion+Resource</a>
 */
public class Conversion extends DomainWithTasksWrapper<ConversionDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected Conversion(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final ConversionDto target) {
      super(context, target);
   }

   // Domain methods

   public void refresh() {
      RESTLink link = checkNotNull(target.searchLink("edit"), ValidationErrors.MISSING_REQUIRED_LINK + "edit");

      ExtendedUtils utils = (ExtendedUtils) context.getUtils();
      HttpResponse response = checkNotNull(utils.getAbiquoHttpClient().get(link), "conversion");

      ParseXMLWithJAXB<ConversionDto> parser = new ParseXMLWithJAXB<ConversionDto>(utils.getXml(),
            TypeLiteral.get(ConversionDto.class));

      target = parser.apply(response);
   }

   // Parent access

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Machine+Template+Resource"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Machine+Template+
    *      Resource</a>
    */
   public VirtualMachineTemplate getVirtualMachineTemplate() {
      RESTLink link = checkNotNull(target.searchLink(ParentLinkName.VIRTUAL_MACHINE_TEMPLATE),
            ValidationErrors.MISSING_REQUIRED_LINK + " " + ParentLinkName.VIRTUAL_MACHINE_TEMPLATE);

      ExtendedUtils utils = (ExtendedUtils) context.getUtils();
      HttpResponse response = utils.getAbiquoHttpClient().get(link);

      ParseXMLWithJAXB<VirtualMachineTemplateDto> parser = new ParseXMLWithJAXB<VirtualMachineTemplateDto>(
            utils.getXml(), TypeLiteral.get(VirtualMachineTemplateDto.class));

      return wrap(context, VirtualMachineTemplate.class, parser.apply(response));
   }

   /**
    * Starts a new BPM task to regenerate a failed conversion.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Conversion+Resource#ConversionResource-UpdateConversion"
    *      > http://community.abiquo.com/display/ABI20/Conversion+Resource#
    *      ConversionResource- UpdateConversion</a>
    * @return The task reference to track its progress
    */
   public AsyncTask restartFailedConversion() {
      return getVirtualMachineTemplate().requestConversion(getTargetFormat());
   }

   // Delegate methods

   public String getSourcePath() {
      return target.getSourcePath();
   }

   public ConversionState getState() {
      return target.getState();
   }

   public String getTargetPath() {
      return target.getTargetPath();
   }

   public Long getTargetSizeInBytes() {
      return target.getTargetSizeInBytes();
   }

   public DiskFormatType getSourceFormat() {
      return target.getSourceFormat();
   }

   public DiskFormatType getTargetFormat() {
      return target.getTargetFormat();
   }

   public Date getStartTimestamp() {
      return target.getStartTimestamp();
   }

   @Override
   public String toString() {
      return "Conversion [sourcePath=" + getSourcePath() + ", sourceFormat=" + getSourceFormat() + ", targetPath="
            + getTargetPath() + ", targetFormat=" + getTargetFormat() + ", targetSizeInBytes=" + getTargetSizeInBytes()
            + ", startTimestamp=" + getStartTimestamp() + ", state=" + getState() + "]";
   }
}
