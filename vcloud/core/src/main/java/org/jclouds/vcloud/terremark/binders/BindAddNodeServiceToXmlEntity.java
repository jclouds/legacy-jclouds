package org.jclouds.vcloud.terremark.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringEntity;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindAddNodeServiceToXmlEntity implements MapBinder {

   @Inject
   @Named("CreateNodeService")
   private String xmlTemplate;
   @Inject
   private BindToStringEntity stringBinder;

   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      String ipAddress = checkNotNull(postParams.get("ipAddress"),
               "ipAddress parameter not present");
      String name = checkNotNull(postParams.get("name"), "name parameter not present");
      String port = checkNotNull(postParams.get("port"), "port parameter not present");
      String enabled = checkNotNull(postParams.get("enabled"), "enabled parameter not present");
      String description = postParams.get("description");

      String entity = xmlTemplate.replaceAll("\\{ipAddress\\}", ipAddress);
      entity = entity.replaceAll("\\{name\\}", name);
      entity = entity.replaceAll("\\{port\\}", port);
      entity = entity.replaceAll("\\{enabled\\}", enabled);
      entity = entity.replaceAll("\\{description\\}", description == null ? "" : String.format(
               "%n    <Description>%s</Description>", description));

      stringBinder.bindToRequest(request, entity);
   }

   public void bindToRequest(HttpRequest request, Object input) {
      throw new IllegalStateException("CreateNodeService needs parameters");

   }

}
