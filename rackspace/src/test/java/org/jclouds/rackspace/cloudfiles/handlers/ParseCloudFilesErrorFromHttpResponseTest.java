package org.jclouds.rackspace.cloudfiles.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ParseCloudFilesErrorFromHttpResponseTest {

   @Test
   public void test404SetsKeyNotFoundExceptionMosso() {
      assertCodeMakes("HEAD",
            URI.create("http://host/v1/MossoCloudFS_7064cdb1d49d4dcba3c899ac33e8409d/adriancole-blobstore1/key"), 404,
            "Not Found", "", KeyNotFoundException.class);
   }

   @Test
   public void test404SetsKeyNotFoundExceptionSwift() {
      assertCodeMakes("HEAD",
            URI.create("http://67.202.39.175:8080/v1/AUTH_7064cdb1d49d4dcba3c899ac33e8409d/adriancole-blobstore1/key"),
            404, "Not Found", "", KeyNotFoundException.class);
   }

   @Test
   public void test404SetsContainerNotFoundExceptionMosso() {
      assertCodeMakes("HEAD",
            URI.create("http://host/v1/MossoCloudFS_7064cdb1d49d4dcba3c899ac33e8409d/adriancole-blobstore1"), 404,
            "Not Found", "", ContainerNotFoundException.class);
   }

   @Test
   public void test404SetsContainerNotFoundExceptionSwift() {
      assertCodeMakes("HEAD",
            URI.create("http://67.202.39.175:8080/v1/AUTH_7064cdb1d49d4dcba3c899ac33e8409d/adriancole-blobstore1"),
            404, "Not Found", "", ContainerNotFoundException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
         Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "text/plain", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
         String content, Class<? extends Exception> expected) {

      ParseCloudFilesErrorFromHttpResponse function = new ParseCloudFilesErrorFromHttpResponse();

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = new HttpRequest(method, uri);
      HttpResponse response = new HttpResponse(statusCode, message, Payloads.newInputStreamPayload(Utils
            .toInputStream(content)));
      response.getPayload().getContentMetadata().setContentType(contentType);

      expect(command.getRequest()).andReturn(request).atLeastOnce();
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