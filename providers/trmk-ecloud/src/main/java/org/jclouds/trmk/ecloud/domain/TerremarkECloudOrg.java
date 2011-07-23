package org.jclouds.trmk.ecloud.domain;

import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.TerremarkOrg;
import org.jclouds.trmk.vcloud_0_8.domain.internal.TerremarkOrgImpl;
import org.jclouds.trmk.vcloud_0_8.endpoints.DataCenters;
import org.jclouds.trmk.vcloud_0_8.endpoints.Tags;
import org.jclouds.trmk.vcloud_0_8.endpoints.VAppCatalog;

import com.google.inject.ImplementedBy;

/**
 * @author Adrian Cole
 */
@org.jclouds.trmk.vcloud_0_8.endpoints.Org
@ImplementedBy(TerremarkOrgImpl.class)
public interface TerremarkECloudOrg extends TerremarkOrg {

   @DataCenters
   ReferenceType getDataCenters();

   @Tags
   ReferenceType getTags();

   @VAppCatalog
   ReferenceType getVAppCatalog();
}