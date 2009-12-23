package org.jclouds.vcloud.terremark.functions;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.internal.TaskImpl;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class ParseTaskFromLocationHeader implements Function<HttpResponse, Task> {

   public Task apply(HttpResponse from) {
      String location = from.getFirstHeaderOrNull(HttpHeaders.LOCATION);
      if (location == null)
         location = from.getFirstHeaderOrNull("location");
      if (location != null) {
         String taskId = location.substring(location.lastIndexOf('/') + 1);
         return new TaskImpl(taskId, URI.create(location), TaskStatus.QUEUED, new Date(), null,
                  null, null);
      } else {
         throw new HttpResponseException("no uri in headers or content", null, from);
      }

   }
}