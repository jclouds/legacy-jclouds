package org.jclouds.mezeo.pcs2.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

public class BindDataToEntity implements Binder {

   public void bindToRequest(HttpRequest request, Object entity) {
      Blob<?> object = (Blob<?>) entity;
      request.setEntity(checkNotNull(object.getData(), "object.getContent()"));
      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, object.getMetadata().getSize() + "");
   }
}
