package org.jclouds.openstack.nova.functions;

import org.jclouds.json.config.GsonModule;

/**
 * Created by IntelliJ IDEA.
 * User: VGalkin
 * Date: 4/16/11
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
class ParserModule extends GsonModule {

   @Override
   protected void configure() {
      super.configure();
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
   }
}
