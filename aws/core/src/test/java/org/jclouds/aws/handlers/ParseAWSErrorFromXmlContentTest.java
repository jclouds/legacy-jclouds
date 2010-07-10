package org.jclouds.aws.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.Payloads;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

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
   public void test400WithIncorrectStateSetsIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "",
               "<Error><Code>IncorrectState</Code></Error>", IllegalStateException.class);
   }

   @Test
   public void test400WithAuthFailureSetsAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://amazonaws.com/foo"), 400, "",
               "<Error><Code>AuthFailure</Code></Error>", AuthorizationException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message,
            String content, Class<? extends Exception> expected) {

      ParseAWSErrorFromXmlContent function = Guice.createInjector(new ParserModule(),
               new AbstractModule() {

                  @Override
                  protected void configure() {
                     bind(RequestSigner.class).toInstance(createMock(RequestSigner.class));

                  }

               }).getInstance(ParseAWSErrorFromXmlContent.class);

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = new HttpRequest(method, uri);
      HttpResponse response = new HttpResponse(statusCode, message, Payloads
               .newInputStreamPayload(Utils.toInputStream(content)));

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