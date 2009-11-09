package org.jclouds.aws.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;

/**
 * Needed to sign and verify requests and responses.
 * 
 * @author Adrian Cole
 */
public class AWSUtils {

   @Inject
   RequestSigner signer;

   @Inject
   ParseSax.Factory factory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   public AWSError parseAWSErrorFromContent(HttpCommand command, HttpResponse response,
            InputStream content) throws HttpException {
      AWSError error = (AWSError) factory.create(errorHandlerProvider.get()).parse(content);
      if ("SignatureDoesNotMatch".equals(error.getCode())) {
         error.setStringSigned(signer.createStringToSign(command.getRequest()));
         error.setSignature(signer.signString(error.getStringSigned()));
      }
      return error;
   }

   public AWSError parseAWSErrorFromContent(HttpCommand command, HttpResponse response,
            String content) throws HttpException {
      return parseAWSErrorFromContent(command, response, new ByteArrayInputStream(content
               .getBytes()));
   }
}