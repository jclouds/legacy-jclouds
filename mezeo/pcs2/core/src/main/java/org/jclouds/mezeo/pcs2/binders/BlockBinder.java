package org.jclouds.mezeo.pcs2.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.EntityBinder;

public class BlockBinder implements EntityBinder {

   public void addEntityToRequest(Object entity, HttpRequest request) {
      Blob<?> object = (Blob<?>) entity;
      request.setEntity(checkNotNull(object.getData(), "object.getContent()"));
      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, object.getMetadata().getSize() + "");
   }
}
