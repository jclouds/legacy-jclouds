package org.jclouds.atmosonline.saas.functions;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpResponse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * Parses response headers and creates a new AtmosObject from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
@Singleton
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, AtmosObject> {

   private final ParseSystemMetadataFromHeaders systemMetadataParser;
   private final ParseUserMetadataFromHeaders userMetadataParser;
   private final AtmosObject.Factory objectProvider;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseSystemMetadataFromHeaders metadataParser,
            ParseUserMetadataFromHeaders userMetadataParser, AtmosObject.Factory objectProvider) {
      this.systemMetadataParser = metadataParser;
      this.userMetadataParser = userMetadataParser;
      this.objectProvider = objectProvider;
   }

   /**
    * First, calls {@link ParseSystemAndUserMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link AtmosObject} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public AtmosObject apply(HttpResponse from) {
      AtmosObject object = objectProvider.create(systemMetadataParser.apply(from),
               userMetadataParser.apply(from));
      addAllHeadersTo(from, object);
      object.setData(from.getContent());
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      if (contentLength != null) {
         object.getContentMetadata().setContentLength(Long.parseLong(contentLength));
      }
      String contentType = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE);
      if (contentType != null) {
         object.getContentMetadata().setContentType(contentType);
      }
      return object;
   }

   @VisibleForTesting
   void addAllHeadersTo(HttpResponse from, AtmosObject object) {
      object.getAllHeaders().putAll(from.getHeaders());
   }

}