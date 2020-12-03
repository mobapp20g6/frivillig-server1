/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.mobapp20g6.appsrv.resources;

import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

import static no.ntnu.mobapp20g6.appsrv.resources.DatasourceProducer.JNDI_NAME;

@Singleton
@DataSourceDefinition(
    name            = JNDI_NAME,
    className       = "org.postgresql.ds.PGSimpleDataSource", 
    serverName      = "${MPCONFIG=dataSource.serverName}",
    portNumber      = 5432,
    databaseName    = "${MPCONFIG=dataSource.databaseName}",
    user            = "${MPCONFIG=dataSource.user}",
    password        = "${MPCONFIG=dataSource.password}",
    minPoolSize     = 10,
    maxPoolSize     = 50
)

/**
 * @author nils
 */
public class DatasourceProducer {
    public static final String JNDI_NAME =  "java:app/jdbc/postgres-microprofile";

    @Resource(lookup=JNDI_NAME)
    DataSource ds;

    @Produces
    public DataSource getDatasource() {
        return ds;
    }
}