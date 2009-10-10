package org.jclouds.mezeo.pcs2.decorators;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.decorators.RequestDecorator;

public class AddDataAndLength implements RequestDecorator {

   public HttpRequest decorateRequest(HttpRequest request, Object entity) {
      Blob<?> object = (Blob<?>) entity;
      request.setEntity(checkNotNull(object.getData(), "object.getContent()"));
      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, object.getMetadata().getSize() + "");
      return request;
   }
}
