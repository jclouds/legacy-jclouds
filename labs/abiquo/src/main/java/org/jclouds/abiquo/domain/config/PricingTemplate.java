/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.Date;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.PricingPeriod;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.pricing.PricingTemplateDto;

/**
 * A pricing template is a complete pricing schema that can be assigned to an {@link Enterprise}.
 * <p>
 * This pricing schema will provide detailed billing information for each resource consumed by the
 * users of the enterprise.
 * 
 * @author Ignasi Barrera
 * @author Susana Acedo
 */

public class PricingTemplate extends DomainWrapper<PricingTemplateDto>
{

    /** The currency used by the pricing template. */
    protected Currency currency;

    /**
     * Constructor to be used only by the builder. This resource cannot be created.
     */
    private PricingTemplate(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
        final PricingTemplateDto target)
    {
        super(context, target);
    }

    // Domain operations

    public void delete()
    {
        context.getApi().getPricingApi().deletePricingTemplate(target);
        target = null;
    }

    public void save()
    {
        target = context.getApi().getPricingApi().createPricingTemplate(target);
    }

    public void update()
    {
        target = context.getApi().getPricingApi().updatePricingTemplate(target);
    }

    // Builder

    public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
        final Currency currency)
    {
        return new Builder(context, currency);
    }

    public static class Builder
    {
        private RestContext<AbiquoApi, AbiquoAsyncApi> context;

        private Currency currency;

        private String name;

        private String description;

        private BigDecimal hdGB;

        private BigDecimal standingChargePeriod;

        private BigDecimal vlan;

        private PricingPeriod chargingPeriod;

        private BigDecimal minimumChargePeriod;

        private boolean showChangesBefore;

        private boolean showMinimumCharge;

        private PricingPeriod minimumCharge;

        private BigDecimal publicIp;

        private BigDecimal vcpu;

        private BigDecimal memoryGB;

        private boolean defaultTemplate;

        private Date lastUpdate;

        public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Currency currency)
        {
            super();
            this.currency = checkNotNull(currency, ValidationErrors.NULL_RESOURCE + Currency.class);
            this.context = context;
        }

        public Builder name(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder description(final String description)
        {
            this.description = description;
            return this;
        }

        public Builder hdGB(final BigDecimal hdGB)
        {
            this.hdGB = hdGB;
            return this;
        }

        public Builder standingChargePeriod(final BigDecimal standingChargePeriod)
        {
            this.standingChargePeriod = standingChargePeriod;
            return this;
        }

        public Builder chargingPeriod(final PricingPeriod chargingPeriod)
        {
            this.chargingPeriod = chargingPeriod;
            return this;
        }

        public Builder vlan(final BigDecimal vlan)
        {
            this.vlan = vlan;
            return this;
        }

        public Builder minimumChargePeriod(final BigDecimal minimumChargePeriod)
        {
            this.minimumChargePeriod = minimumChargePeriod;
            return this;
        }

        public Builder minimumCharge(final PricingPeriod minimumCharge)
        {
            this.minimumCharge = minimumCharge;
            return this;
        }

        public Builder showChangesBefore(final boolean showChangesBefore)
        {
            this.showChangesBefore = showChangesBefore;
            return this;
        }

        public Builder showMinimumCharge(final boolean showMinimumCharge)
        {
            this.showMinimumCharge = showMinimumCharge;
            return this;
        }

        public Builder publicIp(final BigDecimal publicIp)
        {
            this.publicIp = publicIp;
            return this;
        }

        public Builder vcpu(final BigDecimal vcpu)
        {
            this.vcpu = vcpu;
            return this;
        }

        public Builder memoryGB(final BigDecimal memoryGB)
        {
            this.memoryGB = memoryGB;
            return this;
        }

        public Builder defaultTemplate(final boolean defaultTemplate)
        {
            this.defaultTemplate = defaultTemplate;
            return this;
        }

        public Builder lastUpdate(final Date lastUpdate)
        {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public Builder currency(final Currency currency)
        {
            this.currency = checkNotNull(currency, ValidationErrors.NULL_RESOURCE + Currency.class);
            return this;
        }

        public PricingTemplate build()
        {
            PricingTemplateDto dto = new PricingTemplateDto();
            dto.setName(name);
            dto.setDescription(description);
            dto.setHdGB(hdGB);
            dto.setStandingChargePeriod(standingChargePeriod);
            dto.setVlan(vlan);
            dto.setChargingPeriod(chargingPeriod.ordinal());
            dto.setMinimumCharge(minimumCharge.ordinal());
            dto.setMinimumChargePeriod(minimumChargePeriod);
            dto.setShowChangesBefore(showChangesBefore);
            dto.setShowMinimumCharge(showMinimumCharge);
            dto.setPublicIp(publicIp);
            dto.setVcpu(vcpu);
            dto.setMemoryGB(memoryGB);
            dto.setDefaultTemplate(defaultTemplate);
            dto.setLastUpdate(lastUpdate);

            RESTLink link = currency.unwrap().searchLink("edit");
            checkNotNull(link, ValidationErrors.MISSING_REQUIRED_LINK);
            dto.addLink(new RESTLink("currency", link.getHref()));

            PricingTemplate pricingTemplate = new PricingTemplate(context, dto);
            pricingTemplate.currency = currency;

            return pricingTemplate;
        }

        public static Builder fromPricingTemplate(final PricingTemplate in)
        {
            Builder builder =
                PricingTemplate.builder(in.context, in.currency).name(in.getName())
                    .description(in.getDescription()).hdGB(in.getHdGB())
                    .standingChargePeriod(in.getStandingChargePeriod()).vcpu(in.getVlan())
                    .chargingPeriod(in.getChargingPeriod()).minimumCharge(in.getMinimumCharge())
                    .minimumChargePeriod(in.getMinimumChargePeriod())
                    .showChangesBefore(in.isShowChangesBefore())
                    .showMinimumCharge(in.isShowMinimumCharge()).publicIp(in.getPublicIp())
                    .vcpu(in.getVcpu()).memoryGB(in.getMemoryGB())
                    .defaultTemplate(in.isDefaultTemplate()).lastUpdate(in.getLastUpdate());
            return builder;
        }
    }

    // Delegate methods

    public Integer getId()
    {
        return target.getId();
    }

    public String getName()
    {
        return target.getName();
    }

    public void setName(final String name)
    {
        target.setName(name);
    }

    public String getDescription()
    {
        return target.getDescription();
    }

    public void setDescription(final String description)
    {
        target.setDescription(description);
    }

    public BigDecimal getHdGB()
    {
        return target.getHdGB();
    }

    public void setHdGB(final BigDecimal hdGB)
    {
        target.setHdGB(hdGB);
    }

    public BigDecimal getStandingChargePeriod()
    {
        return target.getStandingChargePeriod();
    }

    public void setStandingChargePeriod(final BigDecimal standingChargePeriod)
    {
        target.setStandingChargePeriod(standingChargePeriod);
    }

    public BigDecimal getVlan()
    {
        return target.getVlan();
    }

    public void setVlan(final BigDecimal vlan)
    {
        target.getVlan();
    }

    public BigDecimal getMinimumChargePeriod()
    {
        return target.getMinimumChargePeriod();
    }

    public void setMinimumChargePeriod(final BigDecimal minimumChargePeriod)
    {
        target.setMinimumChargePeriod(minimumChargePeriod);
    }

    public boolean isShowChangesBefore()
    {
        return target.isShowChangesBefore();
    }

    public void setShowChangesBefore(final boolean showChangesBefore)
    {
        target.setShowChangesBefore(showChangesBefore);
    }

    public boolean isShowMinimumCharge()
    {
        return target.isShowMinimumCharge();
    }

    public void setShowMinimumCharge(final boolean showMinimumCharge)
    {
        target.setShowMinimumCharge(showMinimumCharge);
    }

    public PricingPeriod getMinimumCharge()
    {
        return PricingPeriod.fromId(target.getMinimumCharge());
    }

    public void setMinimumCharge(final PricingPeriod minimumCharge)
    {
        target.setMinimumCharge(minimumCharge.ordinal());
    }

    public PricingPeriod getChargingPeriod()
    {
        return PricingPeriod.fromId(target.getChargingPeriod());
    }

    public void setChargingPeriod(final PricingPeriod chargingPeriod)
    {
        target.setChargingPeriod(chargingPeriod.ordinal());
    }

    public BigDecimal getPublicIp()
    {
        return target.getPublicIp();
    }

    public void setPublicIp(final BigDecimal publicIp)
    {
        target.setPublicIp(publicIp);
    }

    public BigDecimal getVcpu()
    {
        return target.getVcpu();
    }

    public void setVcpu(final BigDecimal vcpu)
    {
        target.setVcpu(vcpu);
    }

    public BigDecimal getMemoryGB()
    {
        return target.getMemoryGB();
    }

    public void setMemoryGB(final BigDecimal memoryGB)
    {
        target.setMemoryGB(memoryGB);
    }

    public boolean isDefaultTemplate()
    {
        return target.isDefaultTemplate();
    }

    public void setDefaultTemplate(final boolean defaultTemplate)
    {
        target.setDefaultTemplate(defaultTemplate);
    }

    public Date getLastUpdate()
    {
        return target.getLastUpdate();
    }

    public void setLastUpdate(final Date lastUpdate)
    {
        target.setLastUpdate(lastUpdate);
    }

    @Override
    public String toString()
    {
        return "PricingTemplate [id=" + getId() + ", name=" + getName() + ", description="
            + getDescription() + ", hdGB =" + getHdGB() + ", standingChargePeriod ="
            + getStandingChargePeriod() + ", vlan = " + getVlan() + ",  chargingPeriod ="
            + getChargingPeriod() + ", minimumChargePeriod=" + getMinimumChargePeriod()
            + ", showChangesBefore =" + isShowChangesBefore() + ", showMinimumCharge= "
            + isShowMinimumCharge() + ", minimumCharge = " + getMinimumCharge() + ", publicIp = "
            + getPublicIp() + ", vcpu =" + getVcpu() + ", memoryGB= " + getMemoryGB()
            + ", defaultTemplate= " + isDefaultTemplate() + ", lastUpdate = " + getLastUpdate()
            + "]";
    }
}
