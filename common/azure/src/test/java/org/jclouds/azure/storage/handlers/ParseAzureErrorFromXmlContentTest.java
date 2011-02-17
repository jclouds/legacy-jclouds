/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.azure.storage.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.io.Payloads;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ParseAzureErrorFromXmlContentTest {

   @Test
   public void test411WithTextHtmlIllegalArgumentException() {
      assertCodeMakes("PUT",
            URI.create("https://jclouds.blob.core.windows.net/adriancole-azureblob-413790770?restype=container"), 411,
            "Length Required", "text/html; charset=us-ascii", "<HTML><HEAD><TITLE>Length Required</TITLE>\r\n",
            IllegalArgumentException.class);
   }

   @Test
   public void test304WithNoContentIllegalArgumentException() {
      assertCodeMakes("GET", URI.create("https://jclouds.blob.core.windows.net/adriancole-blobstore0/apples"), 411,
            "HTTP/1.1 304 The condition specified using HTTP conditional header(s) is not met.", "application/unknown",
            "", IllegalArgumentException.class);
   }

   
   @Test
   public void test412WithTextHtmlHttpResponseException() {
      assertCodeMakes(
            "GET",
            URI.create("https://jclouds.blob.core.windows.net/adriancole-blobstore2?restype=container&comp=list&prefix=apps/apps/apps/&include=metadata"),
            412,
            "HTTP/1.1 412 The condition specified using HTTP conditional header(s) is not met.",
            "application/xml",
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><Error><Code>ConditionNotMet</Code><Message>The condition specified using HTTP conditional header(s) is not met.\nRequestId:921efcad-84bc-4e0a-863d-24810d1096e1\nTime:2010-11-04T15:03:07.8694513Z</Message></Error>",
            AzureStorageResponseException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
         String content, Class<? extends Exception> expected) {

      ParseAzureStorageErrorFromXmlContent function = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(SharedKeyLiteAuthentication.class).toInstance(createMock(SharedKeyLiteAuthentication.class));
         }

      }).getInstance(ParseAzureStorageErrorFromXmlContent.class);

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = new HttpRequest(method, uri);
      HttpResponse response = new HttpResponse(statusCode, message, Payloads.newInputStreamPayload(Strings2
            .toInputStream(content)));
      response.getPayload().getContentMetadata().setContentType(contentType);

      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();
      command.setException(classEq(expected));

      replay(command);

      function.handleError(command, response);

      verify(command);
   }

   public static Exception classEq(final Class<? extends Exception> in) {
      reportMatcher(new IArgumentMatcher() {

         @Override
         public void appendTo(StringBuffer buffer) {
            buffer.append("classEq(");
            buffer.append(in);
            buffer.append(")");
         }

         @Override
         public boolean matches(Object arg) {
            return arg.getClass() == in;
         }

      });
      return null;
   }

}