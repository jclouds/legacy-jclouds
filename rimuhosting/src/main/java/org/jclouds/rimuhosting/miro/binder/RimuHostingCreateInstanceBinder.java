package org.jclouds.rimuhosting.miro.binder;

import org.jclouds.http.HttpRequest;
import org.jclouds.rimuhosting.miro.data.NewServerData;
import org.jclouds.rimuhosting.miro.data.CreateOptions;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.util.Map;

/**
 * @author Ivan Meredith
 */
public class RimuHostingCreateInstanceBinder extends RimuHostingJsonBinder{
   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      String name = checkNotNull(postParams.get("name"));
      String imageId = checkNotNull(postParams.get("imageId"));
      String planId = checkNotNull(postParams.get("planId"));
      //There will be cases when the password is null.
      String password = postParams.get("password");
      NewServerData newServerData = new NewServerData(new CreateOptions(name, password, imageId), planId);
      bindToRequest(request, newServerData);
   }   
}
