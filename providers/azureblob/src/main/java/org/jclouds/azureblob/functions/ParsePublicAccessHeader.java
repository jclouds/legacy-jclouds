package org.jclouds.azureblob.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import javax.inject.Singleton;

import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParsePublicAccessHeader implements Function<HttpResponse, PublicAccess> {

   public PublicAccess apply(HttpResponse from) {
      releasePayload(from);
      String access = from.getFirstHeaderOrNull("x-ms-blob-public-access");
      if (access == null) {
         return PublicAccess.PRIVATE;
      }
      return PublicAccess.valueOf(access.toUpperCase());
   }

}