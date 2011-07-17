package org.jclouds.terremark.ecloud.domain;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.terremark.domain.TerremarkOrg;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkOrgImpl;
import org.jclouds.vcloud.terremark.endpoints.DataCenters;
import org.jclouds.vcloud.terremark.endpoints.Tags;
import org.jclouds.vcloud.terremark.endpoints.VAppCatalog;

import com.google.inject.ImplementedBy;

/**
 * @author Adrian Cole
 */
@org.jclouds.vcloud.endpoints.Org
@ImplementedBy(TerremarkOrgImpl.class)
public interface TerremarkECloudOrg extends TerremarkOrg {

   @DataCenters
   ReferenceType getDataCenters();

   @Tags
   ReferenceType getTags();

   @VAppCatalog
   ReferenceType getVAppCatalog();
}