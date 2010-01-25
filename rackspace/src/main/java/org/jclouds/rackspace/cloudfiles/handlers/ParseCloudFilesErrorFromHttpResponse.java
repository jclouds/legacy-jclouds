package org.jclouds.rackspace.cloudfiles.handlers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.util.Utils;

import com.google.common.io.Closeables;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
public class ParseCloudFilesErrorFromHttpResponse implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;
   public static final String MOSSO_PREFIX = "^/v1[^/]*/MossoCloudFS_[^/]+/";
   public static final Pattern CONTAINER_PATH = Pattern.compile(MOSSO_PREFIX + "([^/]+)$");
   public static final Pattern CONTAINER_KEY_PATH = Pattern.compile(MOSSO_PREFIX + "([^/]+)/(.*)");

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
                  Matcher matcher = CONTAINER_PATH.matcher(path);
                  if (matcher.find()) {
                     exception = new ContainerNotFoundException(matcher.group(1));
                  } else {
                     matcher = CONTAINER_KEY_PATH.matcher(path);
                     if (matcher.find()) {
                        exception = new KeyNotFoundException(matcher.group(1), matcher.group(2));
                     }
                  }
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