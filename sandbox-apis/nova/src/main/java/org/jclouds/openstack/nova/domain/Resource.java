package org.jclouds.openstack.nova.domain;

import com.google.common.collect.Lists;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitri Babaev
 */
public class Resource {
   private List<Map<String, String>> links = Lists.newArrayList();

   public URI getURI() {
      for (Map<String, String> linkProperties : links) {
         try {
            return new URI(linkProperties.get("href"));
         } catch (URISyntaxException e) {
            throw new RuntimeException(e);
         }
      }

      throw new IllegalStateException("URI is not available");
   }
}
