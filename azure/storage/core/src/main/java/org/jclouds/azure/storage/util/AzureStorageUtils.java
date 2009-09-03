package org.jclouds.azure.storage.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.filters.SharedKeyAuthentication;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;

import com.google.inject.Inject;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify Azure Storage requests and
 * responses.
 * 
 * @author Adrian Cole
 */
public class AzureStorageUtils {

   @Inject
   SharedKeyAuthentication signer;

   public AzureStorageError parseAzureStorageErrorFromContent(
            AzureStorageParserFactory parserFactory, HttpCommand command, HttpResponse response,
            InputStream content) throws HttpException {
      AzureStorageError error = parserFactory.createErrorParser().parse(content);
      error.setRequestId(response.getFirstHeaderOrNull(AzureStorageHeaders.REQUEST_ID));
      if ("AuthenticationFailed".equals(error.getCode())) {
         error.setStringSigned(signer.createStringToSign(command.getRequest()));
         error.setSignature(signer.signString(error.getStringSigned()));
      }
      return error;

   }

   public AzureStorageError parseAzureStorageErrorFromContent(
            AzureStorageParserFactory parserFactory, HttpCommand command, HttpResponse response,
            String content) throws HttpException {
      return parseAzureStorageErrorFromContent(parserFactory, command, response,
               new ByteArrayInputStream(content.getBytes()));
   }

}