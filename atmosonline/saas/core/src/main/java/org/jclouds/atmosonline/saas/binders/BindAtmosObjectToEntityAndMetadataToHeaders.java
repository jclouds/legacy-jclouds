package org.jclouds.atmosonline.saas.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.rest.Binder;

public class BindAtmosObjectToEntityAndMetadataToHeaders implements Binder {
   private final BindUserMetadataToHeaders metaBinder;

   @Inject
   protected BindAtmosObjectToEntityAndMetadataToHeaders(BindUserMetadataToHeaders metaBinder) {
      this.metaBinder = metaBinder;
   }

   public void bindToRequest(HttpRequest request, Object entity) {
      AtmosObject object = (AtmosObject) entity;

      request.setEntity(checkNotNull(object.getData(), "object.getContent()"));
      request.getHeaders().put(
               HttpHeaders.CONTENT_TYPE,
               checkNotNull(object.getContentMetadata().getContentType(),
                        "object.metadata.contentType()"));

      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH,
               object.getContentMetadata().getContentLength() + "");

      if (object.getContentMetadata().getContentMD5() != null) {
         request.getHeaders().put("Content-MD5",
                  HttpUtils.toBase64String(object.getContentMetadata().getContentMD5()));
      }
      metaBinder.bindToRequest(request, object.getUserMetadata());
   }
}
