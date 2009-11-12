package org.jclouds.vcloud.terremark.options;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.terremark.binders.BindInstantiateVAppTemplateParamsToXmlEntity;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions extends BindInstantiateVAppTemplateParamsToXmlEntity {
   @Inject
   @Network
   private URI defaultNetwork;

   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      Map<String, String> copy = Maps.newHashMap();
      copy.putAll(postParams);
      if (postParams.get("count") == null)
         copy.put("count", "1");
      if (postParams.get("megabytes") == null)
         copy.put("megabytes", "512");
      if (postParams.get("network") == null)
         copy.put("network", defaultNetwork.toASCIIString());

      super.bindToRequest(request, copy);
   }

}
