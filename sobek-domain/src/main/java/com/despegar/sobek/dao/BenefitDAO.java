package com.despegar.sobek.dao;

import java.util.List;

import org.hibernate.FetchMode;

import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadWriteObjectWLDDAO;
import com.despegar.framework.persistence.hibernate.dao.generic.query.JoinType;
import com.despegar.framework.persistence.hibernate.dao.generic.query.ProjectedQueryDefinition;
import com.despegar.framework.persistence.hibernate.dao.generic.query.PropertyValueComparator;
import com.despegar.framework.persistence.hibernate.dao.generic.query.QueryDefinition;
import com.despegar.sobek.model.Benefit;

public class BenefitDAO extends AbstractReadWriteObjectWLDDAO<Benefit> {

	public Benefit getBenefit(Long OID) {
		return this.getBenefit(OID, true);
	}

	public Benefit getInactiveBenefit(Long OID) {
		return this.getBenefit(OID, false);
	}

	private Benefit getBenefit(Long OID, Boolean isActive) {
		QueryDefinition queryDefinition = new QueryDefinition();
		queryDefinition.addProperty("isActive", isActive, PropertyValueComparator.EQ);

		queryDefinition.addProperty("OID", OID, PropertyValueComparator.EQ);

		queryDefinition.addEntityFetchMode("appliance", FetchMode.JOIN);
		queryDefinition.addEntityFetchMode("externalResource", FetchMode.JOIN);
		queryDefinition.addEntityFetchMode("benefitDescriptionI18N", FetchMode.JOIN);
		queryDefinition.addEntityFetchMode("benefitStatus", FetchMode.JOIN);
		queryDefinition.addEntityFetchMode("benefitCategory", FetchMode.JOIN);
		queryDefinition.addEntityFetchMode("company", FetchMode.JOIN);
		queryDefinition.addEntityFetchMode("company.picture", FetchMode.JOIN);
		return this.findUnique(queryDefinition);
	}

	public List<Long> getBenefitOIDsByCompanyOID(Long companyOID) {
		ProjectedQueryDefinition<Long> projectedQueryDefinition = new ProjectedQueryDefinition<Long>();
		QueryDefinition queryDefinition = new QueryDefinition();
		queryDefinition.addSubCriteria("company", "company", JoinType.INNER);
		projectedQueryDefinition.addGroupProperty("OID");
		projectedQueryDefinition.setQueryDefinition(queryDefinition);
		queryDefinition.addProperty("company.OID", companyOID, PropertyValueComparator.EQ);
		return this.findByProjectedQueryDefinition(projectedQueryDefinition);

	}

	public List<Long> getApplianceOIDsByCompanyOID(Long companyOID) {
		ProjectedQueryDefinition<Long> projectedQueryDefinition = new ProjectedQueryDefinition<Long>();
		QueryDefinition queryDefinition = new QueryDefinition();
		queryDefinition.addSubCriteria("company", "company", JoinType.INNER);
		queryDefinition.addSubCriteria("appliance", "appliance", JoinType.INNER);
		projectedQueryDefinition.addGroupProperty("appliance.OID");
		projectedQueryDefinition.setQueryDefinition(queryDefinition);
		queryDefinition.addProperty("company.OID", companyOID, PropertyValueComparator.EQ);
		return this.findByProjectedQueryDefinition(projectedQueryDefinition);

	}

	public void saveWithFlush(Benefit benefit) {
		super.save(benefit);
		this.getCurrentSession().flush();
	}

	public void deleteWithFlush(Benefit benefit) {
		super.delete(benefit);
		this.getCurrentSession().flush();
	}
}
