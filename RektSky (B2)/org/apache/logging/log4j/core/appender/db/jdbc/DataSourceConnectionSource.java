package org.apache.logging.log4j.core.appender.db.jdbc;

import org.apache.logging.log4j.*;
import javax.sql.*;
import java.sql.*;
import org.apache.logging.log4j.core.helpers.*;
import javax.naming.*;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.status.*;

@Plugin(name = "DataSource", category = "Core", elementType = "connectionSource", printObject = true)
public final class DataSourceConnectionSource implements ConnectionSource
{
    private static final Logger LOGGER;
    private final DataSource dataSource;
    private final String description;
    
    private DataSourceConnectionSource(final String dataSourceName, final DataSource dataSource) {
        this.dataSource = dataSource;
        this.description = "dataSource{ name=" + dataSourceName + ", value=" + dataSource + " }";
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
    
    @Override
    public String toString() {
        return this.description;
    }
    
    @PluginFactory
    public static DataSourceConnectionSource createConnectionSource(@PluginAttribute("jndiName") final String jndiName) {
        if (Strings.isEmpty(jndiName)) {
            DataSourceConnectionSource.LOGGER.error("No JNDI name provided.");
            return null;
        }
        try {
            final InitialContext context = new InitialContext();
            final DataSource dataSource = (DataSource)context.lookup(jndiName);
            if (dataSource == null) {
                DataSourceConnectionSource.LOGGER.error("No data source found with JNDI name [" + jndiName + "].");
                return null;
            }
            return new DataSourceConnectionSource(jndiName, dataSource);
        }
        catch (NamingException e) {
            DataSourceConnectionSource.LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
    
    static {
        LOGGER = StatusLogger.getLogger();
    }
}
