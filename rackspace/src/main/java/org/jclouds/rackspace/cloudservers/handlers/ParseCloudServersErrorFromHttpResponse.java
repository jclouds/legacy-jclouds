package org.jclouds.rackspace.cloudservers.handlers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Utils;

import com.google.common.io.Closeables;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
public class ParseCloudServersErrorFromHttpResponse implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;
   public static final Pattern RESOURCE_PATTERN = Pattern
            .compile("^/v1[^/]*/[0-9]+/([^/]+)/([0-9]+)");

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         switch (response.getStatusCode()) {
            case 401:
               exception = new AuthorizationException(command.getRequest().getRequestLine());
               break;
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
            default:
               if (response.getContent() != null) {
                  try {
                     String content = Utils.toStringAndClose(response.getContent());
                     exception = new HttpResponseException(command, response, content);
                  } catch (IOException e) {
                     logger.warn(e, "exception reading error from response", response);
                     exception = new HttpResponseException(command, response);
                  }
               }
         }
      } finally {
         Closeables.closeQuietly(response.getContent());
         command.setException(exception);
      }
   }
}