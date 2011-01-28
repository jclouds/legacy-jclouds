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

package org.jclouds.s3.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.io.Payloads;
import org.jclouds.rest.RequestSigner;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ParseS3ErrorFromXmlContentTest {
   private static final String SERVICE_PATH = "/services/Walrus";

   @Test
   public void test404ContainerNotFoundExceptionPath() {
      assertCodeMakes("GET", URI
               .create("http://partnercloud.eucalyptus.com:8773/services/Walrus/adriancole-blobstore58/"), 404, "HTTP/1.1 404 Not Found", false,
               "<Error><Code>Monster.NotFound</Code></Error>", ContainerNotFoundException.class);
   }
   

   @Test
   public void test404KeyNotFoundExceptionPath() {
      assertCodeMakes("GET", URI
               .create("http://partnercloud.eucalyptus.com:8773/services/Walrus/adriancole-blobstore58/apples"), 404, "HTTP/1.1 404 Not Found", false,
               "<Error><Code>Monster.NotFound</Code></Error>", KeyNotFoundException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, final boolean virtualHost,
            String content, Class<? extends Exception> expected) {

      ParseS3ErrorFromXmlContent function = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(RequestSigner.class).toInstance(createMock(RequestSigner.class));
            bindConstant().annotatedWith(Names.named(PROPERTY_HEADER_TAG)).to("amz");
            bindConstant().annotatedWith(Names.named(PROPERTY_S3_SERVICE_PATH)).to(SERVICE_PATH);
            bindConstant().annotatedWith(Names.named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS)).to(virtualHost);
         }

      }).getInstance(ParseS3ErrorFromXmlContent.class);

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = new HttpRequest(method, uri);
      HttpResponse response = new HttpResponse(statusCode, message, Payloads.newInputStreamPayload(Strings2
               .toInputStream(content)));
      response.getPayload().getContentMetadata().setContentType("application/xml");

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