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

package org.jclouds.aws.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.io.Payloads;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.ResourceNotFoundException;
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
public class ParseAWSErrorFromXmlContentTest {

   @Test
   public void test400WithNotFoundSetsResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "",
               "<Error><Code>Monster.NotFound</Code></Error>", ResourceNotFoundException.class);
   }

   @Test
   public void test400WithInvalidIdIllegalArgumentException() {
      assertCodeMakes("POST", URI.create("https://ec2.us-east-1.amazonaws.com"), 400, "HTTP/1.1 400", "",
               "Invalid id: \"asdaasdsa\" (expecting \"ami-...\")", IllegalArgumentException.class);
   }

   @Test
   public void test400WithLoadBalancerNotFoundSetsResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "",
               "<Error><Code>LoadBalancerNotFound</Code></Error>", ResourceNotFoundException.class);
   }

   @Test
   public void test400WithUnsupportedCodeMakesUnsupportedOperationException() {
      assertCodeMakes("POST", URI.create("https://ec2.us-west-1.amazonaws.com/"), 400, "",
               "<Error><Code>UnsupportedOperation</Code></Error>", UnsupportedOperationException.class);
   }

   @Test
   public void test400WithInUseCodeSetsIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "",
               "<Error><Code>InvalidPlacementGroup.InUse</Code></Error>", IllegalStateException.class);
   }

   @Test
   public void test400WithUnknownSetsResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "",
               "<Error><Code>InvalidPlacementGroup.Unknown</Code></Error>", ResourceNotFoundException.class);
   }

   @Test
   public void test400WithIncorrectStateSetsIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "",
               "<Error><Code>IncorrectState</Code></Error>", IllegalStateException.class);
   }

   @Test
   public void test400WithInUseSetsIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "", "text/plain",
               "The placement group 'jclouds#adriancoleec2cccluster#us-east-1' is in use and may not be deleted.",
               IllegalStateException.class);
   }

   @Test
   public void test409SetsIllegalStateException() {
      assertCodeMakes(
               "PUT",
               URI.create("https://adriancole-blobstore011.s3.amazonaws.com/"),
               409,
               "",
               "<Error><Code>OperationAborted</Code><Message>A conflicting conditional operation is currently in progress against this resource. Please try again.</Message><RequestId>F716E81C3D814E59</RequestId><HostId>SDprHxWzG/YXzanVnV7VTz/wP+6fRt1dS+q00kH1rz248YOOSddkFiTXF04XtqNO</HostId></Error>",
               IllegalStateException.class);
   }

   @Test
   public void test400WithInvalidGroupDuplicateIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "Bad Request", "application/unknown",
               "<Error><Code>InvalidGroup.Duplicate</Code></Error>", IllegalStateException.class);
   }

   @Test
   public void test400WithInvalidKeyPairGroupDuplicateIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "Bad Request", "application/unknown",
               "<Error><Code>InvalidKeyPair.Duplicate</Code></Error>", IllegalStateException.class);
   }

   @Test
   public void test400WithTextPlainIllegalArgumentException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "Bad Request", "text/plain",
               "Failure: 400 Bad Request\nFailed to bind the following fields\nMonitoring.Enabled = true\n\n\n",
               IllegalArgumentException.class);
   }

   @Test
   public void test400WithGroupAlreadyExistsEucalyptusIllegalStateException() {
      assertCodeMakes(
               "GET",
               URI.create("https://amazonaws.com/foo"),
               400,
               "",
               "<?xml version=\"1.0\"?><Response><Errors><Error><Code>Groups</Code><Message>\nError adding network group: group named jclouds#eucrun#Eucalyptus already exists\nError adding network group: group named jclouds#eucrun#Eucalyptus already exists</Message></Error></Errors><RequestID>e0133975-3bc5-456d-9753-1d61b27e07e9</RequestID></Response>",
               IllegalStateException.class);
   }

   @Test
   public void test400WithAuthFailureSetsAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "",
               "<Error><Code>AuthFailure</Code></Error>", AuthorizationException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
            Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "text/xml", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
            String content, Class<? extends Exception> expected) {

      ParseAWSErrorFromXmlContent function = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(RequestSigner.class).toInstance(createMock(RequestSigner.class));
            bindConstant().annotatedWith(Names.named(PROPERTY_HEADER_TAG)).to("amz");
         }

      }).getInstance(ParseAWSErrorFromXmlContent.class);

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