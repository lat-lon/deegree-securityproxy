package org.deegree.securityproxy.authentication.ows.repository;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.deegree.securityproxy.authentication.ows.GeometryFilterInfo;
import org.deegree.securityproxy.authentication.ows.RasterPermission;
import org.deegree.securityproxy.authentication.ows.WcsUser;
import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
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
public class WcsUserDaoImpl implements UserDao {

    private static final String SERVICE_NAME = "WCS";

    @Autowired
    private DataSource source;

    private final String schemaName;

    private final String tableName;

    private final String headerColumn;

    private final String userNameColumn;

    private final String passwordColumn;

    private final String serviceTypeColumn;

    private final String serviceVersionColumn;

    private final String operationTypeColumn;

    private final String serviceNameColumn;

    private final String internalServiceUrlColumn;

    private final String layerNameColumn;

    private final String subscriptionStart;

    private final String subscriptionEnd;

    private final String geometryLimitColumn;

    private List<String> additionalRequestParameters;

    public WcsUserDaoImpl( String schemaName, String tableName, String headerColumn, String userNameColumn,
                           String passwordColumn, String serviceTypeColumn, String serviceVersionColumn,
                           String operationTypeColumn, String serviceNameColumn, String internalServiceUrlColumn,
                           String layerNameColumn, String subscriptionStart, String subscriptionEnd,
                           String geometryLimitColumn ) {
        this( schemaName, tableName, headerColumn, userNameColumn, passwordColumn, serviceTypeColumn,
              serviceVersionColumn, operationTypeColumn, serviceNameColumn, internalServiceUrlColumn, layerNameColumn,
              subscriptionStart, subscriptionEnd, geometryLimitColumn, null );
    }

    public WcsUserDaoImpl( String schemaName, String tableName, String headerColumn, String userNameColumn,
                           String passwordColumn, String serviceTypeColumn, String serviceVersionColumn,
                           String operationTypeColumn, String serviceNameColumn, String internalServiceUrlColumn,
                           String layerNameColumn, String subscriptionStart, String subscriptionEnd,
                           String geometryLimitColumn, String[] additionalRequestParameters ) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.headerColumn = headerColumn;
        this.userNameColumn = userNameColumn;
        this.passwordColumn = passwordColumn;
        this.serviceTypeColumn = serviceTypeColumn;
        this.serviceVersionColumn = serviceVersionColumn;
        this.operationTypeColumn = operationTypeColumn;
        this.serviceNameColumn = serviceNameColumn;
        this.internalServiceUrlColumn = internalServiceUrlColumn;
        this.layerNameColumn = layerNameColumn;
        this.subscriptionStart = subscriptionStart;
        this.subscriptionEnd = subscriptionEnd;
        this.geometryLimitColumn = geometryLimitColumn;
        if ( additionalRequestParameters != null )
            this.additionalRequestParameters = asList( additionalRequestParameters );
        else
            this.additionalRequestParameters = Collections.emptyList();
    }

    @Override
    public WcsUser retrieveUserById( String headerValue ) {
        if ( !checkParameterNotNullOrEmpty( headerValue ) )
            return null;
        String jdbcString = generateSelectUserByHeaderSqlQuery();
        return retrieveUser( headerValue, jdbcString );
    }

    @Override
    public WcsUser retrieveUserByName( String userName ) {
        if ( !checkParameterNotNullOrEmpty( userName ) )
            return null;
        String jdbcString = generateSelectByUserNameSqlQuery();
        return retrieveUser( userName, jdbcString );
    }

    private WcsUser retrieveUser( String selectByValue, String jdbcString ) {
        JdbcTemplate template = new JdbcTemplate( source );
        try {
            Date now = new Date();
            List<Map<String, Object>> rows = template.queryForList( jdbcString, selectByValue, now );
            return createUserForRows( rows );
        } catch ( DataAccessException e ) {
            return null;
        }
    }

    private boolean checkParameterNotNullOrEmpty( String headerValue ) {
        return !( headerValue == null || "".equals( headerValue ) );
    }

    private String generateSelectUserByHeaderSqlQuery() {
        return generateSqlQuery( headerColumn );
    }

    private String generateSelectByUserNameSqlQuery() {
        return generateSqlQuery( userNameColumn );
    }

    private String generateSqlQuery( String whereClauseColumn ) {
        StringBuilder builder = new StringBuilder();
        builder.append( "SELECT " );
        builder.append( userNameColumn ).append( "," );
        builder.append( passwordColumn ).append( "," );
        builder.append( headerColumn ).append( "," );
        builder.append( serviceTypeColumn ).append( "," );
        builder.append( serviceNameColumn ).append( "," );
        builder.append( internalServiceUrlColumn ).append( "," );
        builder.append( serviceVersionColumn ).append( "," );
        builder.append( operationTypeColumn ).append( "," );
        builder.append( layerNameColumn ).append( "," );
        builder.append( geometryLimitColumn );
        for ( String additionalRequestParameter : additionalRequestParameters ) {
            builder.append( "," );
            builder.append( additionalRequestParameter );
        }
        appendFrom( builder );
        builder.append( " WHERE " );
        builder.append( whereClauseColumn ).append( " = ? AND ? BETWEEN " );
        builder.append( subscriptionStart ).append( " AND " );
        builder.append( subscriptionEnd );
        return builder.toString();
    }

    private void appendFrom( StringBuilder builder ) {
        builder.append( " FROM " );
        if ( schemaName != null && !"".equals( schemaName ) )
            builder.append( schemaName ).append( "." );
        builder.append( tableName );
    }

    private WcsUser createUserForRows( List<Map<String, Object>> rows ) {
        String username = null;
        String password = null;
        String accessToken = null;
        List<RasterPermission> authorities = new ArrayList<RasterPermission>();
        List<GeometryFilterInfo> geometrieFilter = new ArrayList<GeometryFilterInfo>();
        for ( Map<String, Object> row : rows ) {
            if ( checkIfWcsServiceType( row ) ) {
                username = getAsString( row, userNameColumn );
                password = getAsString( row, passwordColumn );
                accessToken = getAsString( row, headerColumn );
                addAuthorities( authorities, row );
                createGeometryFilter( geometrieFilter, row );
            }
        }
        if ( username != null && password != null )
            return new WcsUser( username, password, accessToken, authorities, geometrieFilter );
        return null;
    }

    private void addAuthorities( Collection<RasterPermission> authorities, Map<String, Object> row ) {
        String serviceName = getAsString( row, serviceNameColumn );
        LimitedOwsServiceVersion serviceVersion = parseServiceVersion( row );
        String operationType = retrieveOperationType( row );
        String layerName = getAsString( row, layerNameColumn );
        String internalServiceUrl = getAsString( row, internalServiceUrlColumn );
        Map<String, String[]> userRequestParameters = retrieveAdditionalRequestParams( row );
        authorities.add( new RasterPermission( operationType, serviceVersion, layerName, serviceName, internalServiceUrl,
                                            userRequestParameters ) );
    }

    private Map<String, String[]> retrieveAdditionalRequestParams( Map<String, Object> row ) {
        Map<String, String[]> userRequestParameters = new HashMap<String, String[]>();
        for ( String additionalRequestParam : additionalRequestParameters ) {
            String paramValue = getAsString( row, additionalRequestParam );
            if ( paramValue != null && !paramValue.isEmpty() )
                userRequestParameters.put( additionalRequestParam, new String[] { paramValue } );
        }
        return userRequestParameters;
    }

    private void createGeometryFilter( List<GeometryFilterInfo> geometryFilter, Map<String, Object> row ) {
        String coverageName = getAsString( row, layerNameColumn );
        if ( coverageName != null && !coverageName.isEmpty() ) {
            String geometryLimit = getAsString( row, geometryLimitColumn );
            GeometryFilterInfo wcsGeometryFilter = new GeometryFilterInfo( coverageName, geometryLimit );
            geometryFilter.add( wcsGeometryFilter );
        }
    }

    private boolean checkIfWcsServiceType( Map<String, Object> row ) {
        String serviceType = getAsString( row, serviceTypeColumn );
        return SERVICE_NAME.equals( serviceType.toUpperCase() );
    }

    private String retrieveOperationType( Map<String, Object> row ) {
        return getAsString( row, operationTypeColumn );
    }

    private LimitedOwsServiceVersion parseServiceVersion( Map<String, Object> row ) {
        String asString = getAsString( row, serviceVersionColumn );
        if ( asString != null && !asString.isEmpty() )
            return new LimitedOwsServiceVersion( asString );
        return null;
    }

    private String getAsString( Map<String, Object> row, String columnName ) {
        return row.get( columnName ) != null ? (String) row.get( columnName ) : null;
    }

}