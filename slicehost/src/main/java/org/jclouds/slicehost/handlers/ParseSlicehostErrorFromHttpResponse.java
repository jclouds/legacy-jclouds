package org.jclouds.slicehost.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.slicehost.xml.ErrorHandler;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseSlicehostErrorFromHttpResponse implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;
   public static final Pattern RESOURCE_PATTERN = Pattern.compile("^/v1[^/]*/[0-9]+/([^/]+)/([0-9]+)");

   private final ErrorParser errorParser;

   @Inject
   ParseSlicehostErrorFromHttpResponse(ErrorParser errorParser) {
      this.errorParser = errorParser;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         String content = response.getStatusCode() != 401 ? parseErrorFromContentOrNull(command, response) : null;
         switch (response.getStatusCode()) {
         case 401:
            exception = new AuthorizationException(command.getRequest(), content);
            break;
         case 403:
         case 404:
            if (!command.getRequest().getMethod().equals("DELETE")) {
               String path = command.getRequest().getEndpoint().getPath();
               Matcher matcher = RESOURCE_PATTERN.matcher(path);
               String message;
               if (matcher.find()) {
                  message = String.format("%s %s not found", matcher.group(1), matcher.group(2));
               } else {
                  message = path;
               }
               exception = new ResourceNotFoundException(message);
            }
            break;
         case 422:
            exception = new IllegalStateException(content);
            break;
         default:
            exception = new HttpResponseException(command, response, content);
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

   @Singleton
   static class ErrorParser {
      final ParseSax.Factory factory;
      final Provider<ErrorHandler> errorHandlerProvider;
      @Resource
      protected Logger logger = Logger.NULL;

      @Inject
      ErrorParser(Factory factory, Provider<ErrorHandler> errorHandlerProvider) {
         this.factory = factory;
         this.errorHandlerProvider = errorHandlerProvider;
      }

      String parse(Payload in) {
         try {
            return factory.create(errorHandlerProvider.get()).parse(in.getInput());
         } catch (HttpException e) {
            logger.warn(e, "error parsing error");
            return null;
         } finally {
            in.release();
         }
      }

   }

   String parseErrorFromContentOrNull(HttpCommand command, HttpResponse response) {
      // slicehost returns " " which is unparsable
      if (response.getPayload() != null && response.getPayload().getContentLength() != 1) {
         return errorParser.parse(response.getPayload());
      }
      return null;
   }
}
