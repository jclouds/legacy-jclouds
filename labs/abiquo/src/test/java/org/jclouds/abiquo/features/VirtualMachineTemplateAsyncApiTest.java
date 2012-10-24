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

package org.jclouds.abiquo.features;

import static org.jclouds.abiquo.domain.DomainUtils.withHeader;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.abiquo.domain.TemplateResources;
import org.jclouds.abiquo.domain.cloud.options.ConversionOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.functions.ReturnTaskReferenceOrNull;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.appslibrary.ConversionDto;
import com.abiquo.server.core.appslibrary.ConversionsDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatePersistentDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code VirtualMachineTemplateAsyncApi}
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "unit", testName = "VirtualMachineTemplateAsyncApiTest")
public class VirtualMachineTemplateAsyncApiTest extends BaseAbiquoAsyncApiTest<VirtualMachineTemplateAsyncApi> {
   /*********************** Virtual Machine Template ***********************/

   public void testListVirtualMachineTemplates() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("listVirtualMachineTemplates", Integer.class,
            Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, 1, 1);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualMachineTemplatesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListVirtualMachineTemplatesWithOptions() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("listVirtualMachineTemplates", Integer.class,
            Integer.class, VirtualMachineTemplateOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, 1, 1, VirtualMachineTemplateOptions.builder()
            .hypervisorType(HypervisorType.XENSERVER).categoryName("Firewalls").build());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates"
                  + "?hypervisorTypeName=XENSERVER&categoryName=Firewalls HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualMachineTemplatesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetVirtualMachineTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("getVirtualMachineTemplate", Integer.class,
            Integer.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, 1, 1, 1);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualMachineTemplateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdateVirtualMachineTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("updateVirtualMachineTemplate",
            VirtualMachineTemplateDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, TemplateResources.virtualMachineTemplatePut());

      assertRequestLineEquals(request,
            "PUT http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualMachineTemplateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(TemplateResources.virtualMachineTemplatePutPayload()),
            VirtualMachineTemplateDto.class, VirtualMachineTemplateDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteVirtualMachineTemplate() throws SecurityException, NoSuchMethodException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("deleteVirtualMachineTemplate",
            VirtualMachineTemplateDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, TemplateResources.virtualMachineTemplatePut());

      assertRequestLineEquals(request,
            "DELETE http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreatePersistentVirtualMachineTemplate() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("createPersistentVirtualMachineTemplate",
            Integer.class, Integer.class, VirtualMachineTemplatePersistentDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, 1, 1, TemplateResources.persistentData());

      assertRequestLineEquals(request,
            "POST http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + AcceptedRequestDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(TemplateResources.persistentPayload()),
            VirtualMachineTemplatePersistentDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Conversions ***********************/

   public void testRequestConversion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("requestConversion",
            VirtualMachineTemplateDto.class, DiskFormatType.class, ConversionDto.class);

      GeneratedHttpRequest request = processor.createRequest(method, TemplateResources.virtualMachineTemplatePut(),
            DiskFormatType.VMDK_STREAM_OPTIMIZED, TemplateResources.conversionPut());

      assertRequestLineEquals(
            request,
            "PUT http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1/conversions/VMDK_STREAM_OPTIMIZED HTTP/1.1");

      assertNonPayloadHeadersEqual(request, "Accept: " + AcceptedRequestDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(TemplateResources.conversionPutPlayload()),
            ConversionDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ReturnTaskReferenceOrNull.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListConversions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualMachineTemplateAsyncApi.class
            .getMethod("listConversions", VirtualMachineTemplateDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, TemplateResources.virtualMachineTemplatePut());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1/conversions HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + ConversionsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListConversionsWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("listConversions",
            VirtualMachineTemplateDto.class, ConversionOptions.class);
      GeneratedHttpRequest request = processor.createRequest(
            method,
            TemplateResources.virtualMachineTemplatePut(),
            ConversionOptions.builder().hypervisorType(HypervisorType.XENSERVER)
                  .conversionState(ConversionState.FINISHED).build());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1/conversions"
                  + "?hypervisor=XENSERVER&state=FINISHED HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + ConversionsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetConversion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VirtualMachineTemplateAsyncApi.class.getMethod("getConversion", VirtualMachineTemplateDto.class,
            DiskFormatType.class);
      GeneratedHttpRequest request = processor.createRequest(method, TemplateResources.virtualMachineTemplatePut(),
            DiskFormatType.RAW);

      assertRequestLineEquals(
            request,
            "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1/conversions/RAW HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + ConversionDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VirtualMachineTemplateAsyncApi>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VirtualMachineTemplateAsyncApi>>() {
      };
   }
}
