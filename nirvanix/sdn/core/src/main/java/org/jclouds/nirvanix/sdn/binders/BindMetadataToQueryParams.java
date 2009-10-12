package org.jclouds.nirvanix.sdn.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.collect.Lists;

public class BindMetadataToQueryParams implements Binder {

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(request, "input") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      checkArgument(checkNotNull(input, "input") instanceof Map,
               "this binder is only valid for Maps!");
      Map<String, String> userMetadata = (Map<String, String>) input;
      List<String> metadata = Lists.newArrayList();
      for (Entry<String, String> entry : userMetadata.entrySet()) {
         metadata.add(String.format("%s:%s", entry.getKey().toLowerCase(), entry.getValue()));
      }
      ((GeneratedHttpRequest) request).replaceQueryParam("metadata", metadata.toArray());
   }

}
