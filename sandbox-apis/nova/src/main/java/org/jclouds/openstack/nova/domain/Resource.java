package org.jclouds.openstack.nova.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;

/**
 * @author Dmitri Babaev
 */
public class Resource {

   private List<Map<String, String>> links = Lists.newArrayList();

   public URI getURI() {
      for (Map<String, String> linkProperties : links) {
         try {
            if (!Functions.forMap(linkProperties, "").apply("rel").equals("bookmark"))
               continue;
            if (!Functions.forMap(linkProperties, "").apply("type").contains("json"))
               continue;
            
            return new URI(linkProperties.get("href"));
         } catch (URISyntaxException e) {
            throw new RuntimeException(e);
         }
      }

      throw new IllegalStateException("URI is not available");
   }
}
