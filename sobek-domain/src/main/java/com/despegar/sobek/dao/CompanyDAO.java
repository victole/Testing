package com.despegar.sobek.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;

import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadWriteObjectWLDDAO;
import com.despegar.framework.persistence.hibernate.dao.generic.query.OrderType;
import com.despegar.framework.persistence.hibernate.dao.generic.query.ProjectedQueryDefinition;
import com.despegar.framework.persistence.hibernate.dao.generic.query.PropertyValueComparator;
import com.despegar.framework.persistence.hibernate.dao.generic.query.QueryDefinition;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.model.Company;

public class CompanyDAO
    extends AbstractReadWriteObjectWLDDAO<Company> {

    public Company getCompanyByOID(Long companyOID) {
        QueryDefinition queryDefinition = new QueryDefinition();
        queryDefinition.addEntityFetchMode("contacts", FetchMode.JOIN);
        queryDefinition.addProperty("OID", companyOID);
        return this.findUnique(queryDefinition);
    }

    public List<Object[]> searchCompaniesByCriteria(String likeName, Integer pageSize, Integer pageNumber,
        String orderByField, OrderType orderType) {

        Integer maxResults = pageSize;
        Integer firstResult = ((pageNumber - 1) * pageSize);

        ProjectedQueryDefinition<Object[]> projectedQueryDefinition = new ProjectedQueryDefinition<Object[]>();
        QueryDefinition queryDefinition = projectedQueryDefinition.getQueryDefinition();
        projectedQueryDefinition.addProjectedProperty("firm");
        projectedQueryDefinition.addProjectedProperty("name");
        projectedQueryDefinition.addProjectedProperty("OID");
        projectedQueryDefinition.addProjectedProperty("creationDate");
        queryDefinition.addProperty("name", StringUtils.concat("%", likeName, "%"), PropertyValueComparator.IRLIKE);
        queryDefinition.addProperty("isActive", Boolean.TRUE);
        queryDefinition.addOrderBy(orderByField, orderType);
        queryDefinition.setMaxResults(maxResults);
        queryDefinition.setFirstResult(firstResult);

        return this.findByProjectedQueryDefinition(projectedQueryDefinition);
    }

    public List<Company> getAllCompanies() {
        QueryDefinition queryDef = new QueryDefinition();
        queryDef.addProperty("isActive", Boolean.TRUE);
        queryDef.addOrderBy("name");
        List<Company> companies = (List<Company>) this.find(queryDef);
        return companies;
    }


    public Integer countCompaniesByCriteria(String likeName) {
        ProjectedQueryDefinition<Integer> projectedQueryDefinition = new ProjectedQueryDefinition<Integer>();
        QueryDefinition queryDefinition = projectedQueryDefinition.getQueryDefinition();
        projectedQueryDefinition.addProjectedFunction(Projections.count("OID"), "benefitCount");
        queryDefinition.addProperty("name", StringUtils.concat("%", likeName, "%"), PropertyValueComparator.IRLIKE);
        queryDefinition.addProperty("isActive", Boolean.TRUE);
        return this.findByProjectedQueryDefinition(projectedQueryDefinition).get(0);
    }


    public void saveWithFlush(Company company) {
        super.save(company);
        this.getCurrentSession().flush();
    }

    public void deleteWithFlush(Company company) {
        super.delete(company);
        this.getCurrentSession().flush();
    }

}
