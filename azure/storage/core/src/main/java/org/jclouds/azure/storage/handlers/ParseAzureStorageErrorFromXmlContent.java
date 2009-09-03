package org.jclouds.azure.storage.handlers;

import javax.annotation.Resource;

import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.util.AzureStorageUtils;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.inject.Inject;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @see AzureStorageError
 * @author Adrian Cole
 * 
 */
public class ParseAzureStorageErrorFromXmlContent implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final AzureStorageParserFactory parserFactory;
   private final AzureStorageUtils utils;

   @Inject
   public ParseAzureStorageErrorFromXmlContent(AzureStorageUtils utils, AzureStorageParserFactory parserFactory) {
      this.utils = utils;
      this.parserFactory = parserFactory;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      String content;
      try {
         content = response.getContent() != null ? Utils.toStringAndClose(response.getContent())
                  : null;
         if (content != null) {
            try {
               if (content.indexOf('<') >= 0) {
                  AzureStorageError error = utils.parseAzureStorageErrorFromContent(parserFactory, command, response,
                           content);
                  command.setException(new AzureStorageResponseException(command, response, error));
               } else {
                  command.setException(new HttpResponseException(command, response, content));
               }
            } catch (Exception he) {
               command.setException(new HttpResponseException(command, response, content));
               Utils.rethrowIfRuntime(he);
            }
         } else {
            command.setException(new HttpResponseException(command, response));
         }
      } catch (Exception e) {
         command.setException(new HttpResponseException(command, response));
         Utils.rethrowIfRuntime(e);
      }
   }

}