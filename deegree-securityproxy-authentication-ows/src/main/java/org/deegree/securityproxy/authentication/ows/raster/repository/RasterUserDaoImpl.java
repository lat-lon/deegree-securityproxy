package org.deegree.securityproxy.authentication.ows.raster.repository;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.domain.LimitedServiceVersion;
import org.deegree.securityproxy.authentication.ows.domain.UnlimitedServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.GeometryFilterInfo;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.authentication.ows.raster.RasterUser;
import org.deegree.securityproxy.authentication.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Loads {@link UserDetails} from a {@link DataSource}.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class RasterUserDaoImpl implements UserDao {

    private static final Logger LOG = Logger.getLogger( RasterUserDaoImpl.class );

    private static final List<String> SUPPORTED_SERVICE_NAMES = asList( new String[] { "WCS", "WMS", "WPS", "WFS",
                                                                                      "CSW", "WMTS" } );

    @Autowired
    private DataSource source;

    private final String schemaName;

    private final String tableName;

    private final String headerColumn;

    private final String nameColumn;

    private final String passwordColumn;

    private final String serviceTypeColumn;

    private final String serviceVersionColumn;

    private final String operationTypeColumn;

    private final String serviceNameColumn;

    private final String internalServiceUrlColumn;

    private final String layerNameColumn;

    private final String subscriptionStartColumn;

    private final String subscriptionEndColumn;

    private final String geometryLimitColumn;

    private List<String> additionalRequestParametersColumns;

    /**
     * 
     * @param schemaName
     *            may be <code>null</code> or empty
     * @param tableName
     *            never <code>null</code>
     * @param nameColumn
     *            never <code>null</code>
     * @param passwordColumn
     *            may be <code>null</code> or empty
     * @param layerNameColumn
     *            may be <code>null</code> or empty
     * @param serviceTypeColumn
     *            never <code>null</code>
     * @param operationTypeColumn
     *            may be <code>null</code> or empty
     * @param serviceNameColumn
     *            may be <code>null</code> or empty
     * @param internalServiceUrlColumn
     *            may be <code>null</code> or empty
     */
    public RasterUserDaoImpl( String schemaName, String tableName, String nameColumn, String passwordColumn,
                              String layerNameColumn, String serviceTypeColumn, String operationTypeColumn,
                              String serviceNameColumn, String internalServiceUrlColumn ) {
        this( schemaName, tableName, null, nameColumn, passwordColumn, serviceTypeColumn, null, operationTypeColumn,
              serviceNameColumn, internalServiceUrlColumn, layerNameColumn, null, null, null, null );
    }

    /**
     * 
     * @param schemaName
     *            may be <code>null</code> or empty
     * @param tableName
     *            never <code>null</code>
     * @param headerColumn
     *            may be <code>null</code> or empty
     * @param nameColumn
     *            never <code>null</code>
     * @param passwordColumn
     *            may be <code>null</code> or empty
     * @param serviceTypeColumn
     *            never <code>null</code>
     * @param serviceVersionColumn
     *            may be <code>null</code> or empty
     * @param operationTypeColumn
     *            may be <code>null</code> or empty
     * @param serviceNameColumn
     *            may be <code>null</code> or empty
     * @param internalServiceUrlColumn
     *            may be <code>null</code> or empty
     * @param layerNameColumn
     *            may be <code>null</code> or empty
     * @param subscriptionStartColumn
     *            may be <code>null</code> or empty
     * @param subscriptionEndColumn
     *            may be <code>null</code> or empty
     * @param geometryLimitColumn
     *            may be <code>null</code> or empty
     */
    public RasterUserDaoImpl( String schemaName, String tableName, String headerColumn, String nameColumn,
                              String passwordColumn, String serviceTypeColumn, String serviceVersionColumn,
                              String operationTypeColumn, String serviceNameColumn, String internalServiceUrlColumn,
                              String layerNameColumn, String subscriptionStartColumn, String subscriptionEndColumn,
                              String geometryLimitColumn ) {
        this( schemaName, tableName, headerColumn, nameColumn, passwordColumn, serviceTypeColumn, serviceVersionColumn,
              operationTypeColumn, serviceNameColumn, internalServiceUrlColumn, layerNameColumn,
              subscriptionStartColumn, subscriptionEndColumn, geometryLimitColumn, null );
    }

    /**
     * 
     * @param schemaName
     *            may be <code>null</code> or empty
     * @param tableName
     *            never <code>null</code>
     * @param headerColumn
     *            may be <code>null</code> or empty
     * @param nameColumn
     *            never <code>null</code>
     * @param passwordColumn
     *            may be <code>null</code> or empty
     * @param serviceTypeColumn
     *            never <code>null</code>
     * @param serviceVersionColumn
     *            may be <code>null</code> or empty
     * @param operationTypeColumn
     *            may be <code>null</code> or empty
     * @param serviceNameColumn
     *            may be <code>null</code> or empty
     * @param internalServiceUrlColumn
     *            may be <code>null</code> or empty
     * @param layerNameColumn
     *            may be <code>null</code> or empty
     * @param subscriptionStartColumn
     *            may be <code>null</code> or empty
     * @param subscriptionEndColumn
     *            may be <code>null</code> or empty
     * @param geometryLimitColumn
     *            may be <code>null</code> or empty
     * @param additionalRequestParametersColumns
     *            may be <code>null</code> or empty
     */
    public RasterUserDaoImpl( String schemaName, String tableName, String headerColumn, String nameColumn,
                              String passwordColumn, String serviceTypeColumn, String serviceVersionColumn,
                              String operationTypeColumn, String serviceNameColumn, String internalServiceUrlColumn,
                              String layerNameColumn, String subscriptionStartColumn, String subscriptionEndColumn,
                              String geometryLimitColumn, String[] additionalRequestParametersColumns ) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.headerColumn = headerColumn;
        this.nameColumn = nameColumn;
        this.passwordColumn = passwordColumn;
        this.serviceTypeColumn = serviceTypeColumn;
        this.serviceVersionColumn = serviceVersionColumn;
        this.operationTypeColumn = operationTypeColumn;
        this.serviceNameColumn = serviceNameColumn;
        this.internalServiceUrlColumn = internalServiceUrlColumn;
        this.layerNameColumn = layerNameColumn;
        this.subscriptionStartColumn = subscriptionStartColumn;
        this.subscriptionEndColumn = subscriptionEndColumn;
        this.geometryLimitColumn = geometryLimitColumn;
        if ( additionalRequestParametersColumns != null )
            this.additionalRequestParametersColumns = asList( additionalRequestParametersColumns );
        else
            this.additionalRequestParametersColumns = Collections.emptyList();
    }

    @Override
    public RasterUser retrieveUserById( String headerValue ) {
        if ( !checkIfNotNullOrEmpty( headerValue ) ) {
            LOG.info( "Could not find user: header value is null or empty!" );
            return null;
        }
        String jdbcString = generateSelectUserByHeaderSqlQuery();
        LOG.debug( "SQL-Statement to query user by id: '" + jdbcString + "'" );
        return retrieveUser( headerValue, jdbcString );
    }

    @Override
    public RasterUser retrieveUserByName( String name ) {
        if ( !checkIfNotNullOrEmpty( name ) ) {
            LOG.info( "Could not find user: name is null or empty!" );
            return null;
        }
        String jdbcString = generateSelectByNameSqlQuery();
        LOG.debug( "SQL-Statement to query user by name: '" + jdbcString + "'" );
        return retrieveUser( name, jdbcString );
    }

    private RasterUser retrieveUser( String selectByValue, String jdbcString ) {
        JdbcTemplate template = new JdbcTemplate( source );
        try {
            List<Map<String, Object>> rows;
            if ( checkIfNotNullOrEmpty( subscriptionStartColumn ) && checkIfNotNullOrEmpty( subscriptionEndColumn ) ) {
                Date now = new Date();
                rows = template.queryForList( jdbcString, selectByValue, now );
            } else
                rows = template.queryForList( jdbcString, selectByValue );
            return createUserForRows( rows );
        } catch ( DataAccessException e ) {
            LOG.error( "An error occured during retrieving the user:", e );
            return null;
        }
    }

    private boolean checkIfNotNullOrEmpty( String parameterValue ) {
        return !( parameterValue == null || "".equals( parameterValue ) );
    }

    private String generateSelectUserByHeaderSqlQuery() {
        return generateSqlQuery( headerColumn );
    }

    private String generateSelectByNameSqlQuery() {
        return generateSqlQuery( nameColumn );
    }

    private String generateSqlQuery( String whereClauseColumn ) {
        StringBuilder builder = new StringBuilder();
        builder.append( "SELECT " );
        builder.append( nameColumn ).append( "," );
        appendIfNotNull( builder, passwordColumn );
        appendIfNotNull( builder, headerColumn );
        appendIfNotNull( builder, serviceTypeColumn );
        appendIfNotNull( builder, serviceNameColumn );
        appendIfNotNull( builder, internalServiceUrlColumn );
        appendIfNotNull( builder, serviceVersionColumn );
        appendIfNotNull( builder, operationTypeColumn );
        appendIfNotNull( builder, layerNameColumn );
        appendIfNotNull( builder, geometryLimitColumn );
        removeLastCharIfNecessary( builder );
        appendAdditionaRequestParameters( builder );
        appendFrom( builder );
        appendWhere( whereClauseColumn, builder );
        return builder.toString();
    }

    private void appendAdditionaRequestParameters( StringBuilder builder ) {
        if ( additionalRequestParametersColumns != null && !additionalRequestParametersColumns.isEmpty() ) {
            for ( String additionalRequestParameter : additionalRequestParametersColumns ) {
                builder.append( "," );
                builder.append( additionalRequestParameter );
            }
        }
    }

    private void appendFrom( StringBuilder builder ) {
        builder.append( " FROM " );
        if ( schemaName != null && !"".equals( schemaName ) )
            builder.append( schemaName ).append( "." );
        builder.append( tableName );
    }

    private void appendWhere( String whereClauseColumn, StringBuilder builder ) {
        builder.append( " WHERE " );
        builder.append( whereClauseColumn ).append( " = ?" );
        if ( checkIfNotNullOrEmpty( subscriptionStartColumn ) && checkIfNotNullOrEmpty( subscriptionEndColumn ) ) {
            builder.append( " AND ? BETWEEN " );
            builder.append( subscriptionStartColumn ).append( " AND " );
            builder.append( subscriptionEndColumn );
        }
    }

    private void removeLastCharIfNecessary( StringBuilder builder ) {
        if ( builder.toString().endsWith( "," ) )
            if ( builder.length() > 0 )
                builder.deleteCharAt( builder.length() - 1 );
    }

    private void appendIfNotNull( StringBuilder builder, String column ) {
        if ( checkIfNotNullOrEmpty( column ) )
            builder.append( column ).append( "," );
    }

    private RasterUser createUserForRows( List<Map<String, Object>> rows ) {
        String name = null;
        String password = null;
        String accessToken = null;
        List<RasterPermission> authorities = new ArrayList<RasterPermission>();
        List<GeometryFilterInfo> geometrieFilter = new ArrayList<GeometryFilterInfo>();
        for ( Map<String, Object> row : rows ) {
            String serviceType = getAsString( row, serviceTypeColumn );
            if ( checkIfServiceTypeisSupported( serviceType ) ) {
                name = getAsString( row, nameColumn );
                password = getAsString( row, passwordColumn );
                accessToken = getAsString( row, headerColumn );
                authorities.add( createAuthority( serviceType, row ) );
                createGeometryFilter( geometrieFilter, row );
            }
        }
        if ( name != null )
            return new RasterUser( name, password, accessToken, authorities, geometrieFilter );
        return null;
    }

    private RasterPermission createAuthority( String serviceType, Map<String, Object> row ) {
        String serviceName = getAsString( row, serviceNameColumn );
        LimitedServiceVersion serviceVersion = parseServiceVersion( row );
        String operationType = retrieveOperationType( row );
        String layerName = getAsString( row, layerNameColumn );
        String internalServiceUrl = getAsString( row, internalServiceUrlColumn );
        Map<String, String[]> userRequestParameters = retrieveAdditionalRequestParams( row );
        return new RasterPermission( serviceType, operationType, serviceVersion, layerName, serviceName,
                        internalServiceUrl, userRequestParameters );
    }

    private Map<String, String[]> retrieveAdditionalRequestParams( Map<String, Object> row ) {
        Map<String, String[]> userRequestParameters = new HashMap<String, String[]>();
        for ( String additionalRequestParam : additionalRequestParametersColumns ) {
            String paramValue = getAsString( row, additionalRequestParam );
            if ( paramValue != null && !paramValue.isEmpty() )
                userRequestParameters.put( additionalRequestParam, new String[] { paramValue } );
        }
        return userRequestParameters;
    }

    private void createGeometryFilter( List<GeometryFilterInfo> geometryFilter, Map<String, Object> row ) {
        String layerName = getAsString( row, layerNameColumn );
        if ( layerName != null && !layerName.isEmpty() ) {
            String geometryLimit = getAsString( row, geometryLimitColumn );
            GeometryFilterInfo wcsGeometryFilter = new GeometryFilterInfo( layerName, geometryLimit );
            geometryFilter.add( wcsGeometryFilter );
        }
    }

    private boolean checkIfServiceTypeisSupported( String serviceType ) {
        return SUPPORTED_SERVICE_NAMES.contains( serviceType.toUpperCase() );
    }

    private String retrieveOperationType( Map<String, Object> row ) {
        return getAsString( row, operationTypeColumn );
    }

    private LimitedServiceVersion parseServiceVersion( Map<String, Object> row ) {
        if ( serviceVersionColumn == null )
            return new UnlimitedServiceVersion();
        String serviceVersion = getAsString( row, serviceVersionColumn );
        if ( serviceVersion != null && !serviceVersion.isEmpty() ) {
            return new LimitedOwsServiceVersion( serviceVersion );
        }
        return null;
    }

    private String getAsString( Map<String, Object> row, String columnName ) {
        return row.get( columnName ) != null ? (String) row.get( columnName ) : null;
    }

}