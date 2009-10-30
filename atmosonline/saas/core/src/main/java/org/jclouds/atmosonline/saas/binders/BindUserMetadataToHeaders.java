package org.jclouds.atmosonline.saas.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

public class BindUserMetadataToHeaders implements Binder {

   public void bindToRequest(HttpRequest request, Object entity) {
      UserMetadata md = (UserMetadata) checkNotNull(entity, "entity");
      if (md.getMetadata().size() > 0) {
         String header = join(md.getMetadata());
         request.getHeaders().put("x-emc-meta", header);
      }
      if (md.getListableMetadata().size() > 0) {
         String header = join(md.getListableMetadata());
         request.getHeaders().put("x-emc-listable-meta", header);
      }
      if (md.getTags().size() > 0) {
         String header = join(md.getTags());
         request.getHeaders().put("x-emc-tags", header);
      }
      if (md.getListableTags().size() > 0) {
         String header = join(md.getListableTags());
         request.getHeaders().put("x-emc-listable-tags", header);
      }
   }

   private String join(Set<String> set) {
      StringBuffer header = new StringBuffer();
      for (String entry : set) {
         header.append(entry).append(",");
      }
      header.deleteCharAt(header.length() - 1);
      return header.toString();
   }

   private String join(Map<String, String> map) {
      StringBuffer header = new StringBuffer();
      for (Entry<String, String> entry : map.entrySet()) {
         header.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
      }
      header.deleteCharAt(header.length() - 1);
      return header.toString();
   }
}
