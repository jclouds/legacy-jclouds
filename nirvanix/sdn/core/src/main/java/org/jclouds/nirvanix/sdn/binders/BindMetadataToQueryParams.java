package org.jclouds.nirvanix.sdn.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map.Entry;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class BindMetadataToQueryParams implements Binder {

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(request, "input") instanceof GeneratedHttpRequest,
               "this decorator is only valid for GeneratedHttpRequests!");
      checkArgument(checkNotNull(input, "input") instanceof Multimap,
               "this decorator is only valid for Multimaps!");
      Multimap<String, String> userMetadata = (Multimap<String, String>) input;
      List<String> metadata = Lists.newArrayList();
      for (Entry<String, String> entry : userMetadata.entries()) {
         metadata.add(String.format("%s:%s", entry.getKey().toLowerCase(), entry.getValue()));
      }
      ((GeneratedHttpRequest)request).replaceQueryParam("metadata", metadata.toArray());
   }

}
