package org.jclouds.nirvanix.sdn.decorators;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.decorators.RequestDecorator;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class AddMetadataAsQueryParams implements RequestDecorator {

   @SuppressWarnings("unchecked")
   public HttpRequest decorateRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Multimap,
               "this decorator is only valid for Multimaps!");

      UriBuilder builder = UriBuilder.fromUri(request.getEndpoint());
      Multimap<String, String> userMetadata = (Multimap<String, String>) input;
      List<String> metadata = Lists.newArrayList();
      for (Entry<String, String> entry : userMetadata.entries()) {
         metadata.add(String.format("%s:%s", entry.getKey().toLowerCase(), entry.getValue()));
      }
      builder.replaceQueryParam("metadata", metadata.toArray());
      List<HttpRequestFilter> oldFilters = request.getFilters();
      request = new HttpRequest(request.getMethod(), builder.build(), request.getHeaders(), request
               .getEntity());
      request.getFilters().addAll(oldFilters);
      return request;
   }

}
