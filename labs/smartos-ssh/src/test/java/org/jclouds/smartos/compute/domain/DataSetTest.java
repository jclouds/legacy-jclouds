package org.jclouds.smartos.compute.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "DataSetTest")
public class DataSetTest {

   @Test
   public void testParse() throws IOException {
      // Response from console from a 'dsadm list'
      InputStream is = getClass().getResourceAsStream("dsadm-list-response.txt");

      BufferedReader r = new BufferedReader(new InputStreamReader(is));
      String line = r.readLine(); // skip line
      ImmutableList.Builder<DataSet> resultBuilder = ImmutableList.builder();
      while ((line = r.readLine()) != null) {
         DataSet ds = DataSet.builder().fromDsadmString(line).build();

         resultBuilder.add(ds);
      }
      List<DataSet> dataSetList = resultBuilder.build();

      Assert.assertEquals(10, dataSetList.size());

      Assert.assertEquals(UUID.fromString("c0ffee88-883e-47cf-80d1-ad71cc872180"), dataSetList.get(0).getUuid());
      Assert.assertEquals("nrm:nrm:realtime-jenkins:1.7", dataSetList.get(0).getUrn());

   }
}
