/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jclouds.openstack.nova.v2_0.parse;

import java.util.Map;

import org.jclouds.json.BaseItemParserTest;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Leander Beernaert
 */
public class ParseServerDiagnostics extends BaseItemParserTest<Optional<Map<String,String>>> {


    @Override
    public Optional<Map<String,String>> expected() {
        return Optional.<Map<String,String>>of(
                new ImmutableMap.Builder<String,String>()
                .put("vnet0_tx_errors", "0")
                .put("vda_read","77364736")
                .put("vda_write","415446016")
                .put("vnet0_tx_packets","9701")
                .put("vda_write_req","47278")
                .put("cpu0_time","143150000000")
                .put("vnet0_tx","1691221")
                .put("vnet0_rx_drop","0")
                .put("vda_errors","-1")
                .put("vnet0_rx_errors","0")
                .put("memory","524288")
                .put("vnet0_rx_packets","11271")
                .put("vda_read_req","9551")
                .put("vnet0_rx","1805288")
                .put("vnet0_tx_drop","0").build());
    }
}
