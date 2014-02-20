package org.deegree.securityproxy.wcs.authentication.repository;

import static java.util.Arrays.asList;
import static org.deegree.securityproxy.wcs.domain.WcsServiceVersion.parseVersions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.deegree.securityproxy.authentication.repository.UserDao;
import org.deegree.securityproxy.wcs.authentication.WcsGeometryFilterInfo;
import org.deegree.securityproxy.wcs.authentication.WcsPermission;
import org.deegree.securityproxy.wcs.authentication.WcsUser;
import org.deegree.securityproxy.wcs.domain.WcsOperationType;
import org.deegree.securityproxy.wcs.domain.WcsServiceVersion;
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
        if ( !checkParameter( headerValue ) )
            return null;
        JdbcTemplate template = new JdbcTemplate( source );
        String jdbcString = generateSqlQuery();
        try {
            Date now = new Date();
            List<Map<String, Object>> rows = template.queryForList( jdbcString, headerValue, now );
            return createUserForRows( rows );
        } catch ( DataAccessException e ) {
            return null;
        }
    }

    private boolean checkParameter( String headerValue ) {
        return !( headerValue == null || "".equals( headerValue ) );
    }

    private String generateSqlQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append( "SELECT " );
        builder.append( userNameColumn ).append( "," );
        builder.append( passwordColumn ).append( "," );
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
        builder.append( headerColumn ).append( " = ? AND ? BETWEEN " );
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
        List<WcsPermission> authorities = new ArrayList<WcsPermission>();
        List<WcsGeometryFilterInfo> geometrieFilter = new ArrayList<WcsGeometryFilterInfo>();
        Map<String, String> userRequestParameters = new HashMap<String, String>();
        for ( Map<String, Object> row : rows ) {
            if ( checkIfWcsServiceType( row ) ) {
                username = getAsString( row, userNameColumn );
                password = getAsString( row, passwordColumn );
                addAuthorities( authorities, row );
                createGeometryFilter( geometrieFilter, row );
                addAdditionalRequestParams( userRequestParameters, row );
            }
        }
        if ( username != null && password != null )
            return new WcsUser( username, password, authorities, geometrieFilter, userRequestParameters );
        return null;
    }

    private void addAuthorities( Collection<WcsPermission> authorities, Map<String, Object> row ) {
        String serviceName = getAsString( row, serviceNameColumn );
        List<WcsServiceVersion> serviceVersions = getServiceVersions( row );
        WcsOperationType operationType = getOperationType( row );
        String layerName = getAsString( row, layerNameColumn );
        String internalServiceUrl = getAsString( row, internalServiceUrlColumn );
        for ( WcsServiceVersion serviceVersion : serviceVersions ) {
            authorities.add( new WcsPermission( operationType, serviceVersion, layerName, serviceName,
                                                internalServiceUrl ) );
        }
    }

    private void addAdditionalRequestParams( Map<String, String> userRequestParameters, Map<String, Object> row ) {
        for ( String additionalRequestParam : additionalRequestParameters ) {
            String paramValue = getAsString( row, additionalRequestParam );
            if ( paramValue != null && !paramValue.isEmpty() )
                userRequestParameters.put( additionalRequestParam, paramValue );
        }
    }

    private void createGeometryFilter( List<WcsGeometryFilterInfo> geometryFilter, Map<String, Object> row ) {
        String coverageName = getAsString( row, layerNameColumn );
        if ( coverageName != null && !coverageName.isEmpty() ) {
            String geometryLimit = getAsString( row, geometryLimitColumn );
            WcsGeometryFilterInfo wcsGeometryFilter = new WcsGeometryFilterInfo( coverageName, geometryLimit );
            geometryFilter.add( wcsGeometryFilter );
        }
    }

    private boolean checkIfWcsServiceType( Map<String, Object> row ) {
        String serviceType = getAsString( row, serviceTypeColumn );
        return SERVICE_NAME.equals( serviceType.toUpperCase() );
    }

    private WcsOperationType getOperationType( Map<String, Object> row ) {
        String operationType = getAsString( row, operationTypeColumn );
        if ( operationType != null )
            return WcsOperationType.valueOf( operationType.toUpperCase() );
        return null;
    }

    private List<WcsServiceVersion> getServiceVersions( Map<String, Object> row ) {
        String serviceVersion = getAsString( row, serviceVersionColumn );
        return parseVersions( serviceVersion );
    }

    private String getAsString( Map<String, Object> row, String columnName ) {
        return row.get( columnName ) != null ? (String) row.get( columnName ) : null;
    }

}