package com.despegar.sobek.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.springframework.util.Assert;

import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadWriteObjectWLDDAO;
import com.despegar.framework.persistence.hibernate.dao.generic.query.JoinType;
import com.despegar.framework.persistence.hibernate.dao.generic.query.PropertyValueComparator;
import com.despegar.framework.persistence.hibernate.dao.generic.query.QueryDefinition;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.model.Benefit;

public class BenefitIndexDAO
    extends AbstractReadWriteObjectWLDDAO<Benefit> {

    public static final Integer BENEFIT_RESULT_SIZE_IN = 1000;

    public List<Benefit> getBenefitIndexes(List<Long> benefitListOIDs) {

        QueryDefinition queryDefinition = new QueryDefinition();
        queryDefinition.addEntityFetchMode("benefitDescriptionI18N", FetchMode.JOIN);
        queryDefinition.addSubCriteria("externalResource", "externalResource", JoinType.LEFT);
        queryDefinition.addEntityFetchMode("company", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("benefitType", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("benefitState", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("benefitCategory", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("appliance", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("appliance.brand", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("appliance.product", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("benefitStatus", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("appliance.destinationGeoArea", FetchMode.JOIN);
        queryDefinition.addEntityFetchMode("appliance.originGeoArea", FetchMode.JOIN);

        if (benefitListOIDs != null) {
            Assert.isTrue(benefitListOIDs.size() <= BENEFIT_RESULT_SIZE_IN, StringUtils.concat(
                "BenefitIndexDAO.getBenefitIndexes El valor para el in es mayor que: ", BENEFIT_RESULT_SIZE_IN));

            Assert.isTrue(!benefitListOIDs.isEmpty(), StringUtils
                .concat("BenefitIndexDAO.getBenefitIndexes la lista en vacia"));

            queryDefinition.addProperty("OID", benefitListOIDs, PropertyValueComparator.IN);
        }
        queryDefinition.addProperty("isActive", Boolean.TRUE);

        return (List<Benefit>) this.findEagerFetching(queryDefinition);
    }
}
