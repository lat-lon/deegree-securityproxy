package org.deegree.securityproxy.request;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.deegree.securityproxy.request.OwsServiceVersion;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class OwsServiceVersionTest {

    @Test
    public void testConstructor() {
        OwsServiceVersion owsServiceVersion = new OwsServiceVersion( "1.6.9" );
        assertThat( owsServiceVersion.getVersionX(), is( 1 ) );
        assertThat( owsServiceVersion.getVersionY(), is( 6 ) );
        assertThat( owsServiceVersion.getVersionZ(), is( 9 ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullValue() {
        new OwsServiceVersion( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyValue() {
        new OwsServiceVersion( "" );
    }

    @Test
    public void testgetVersionString() {
        OwsServiceVersion owsServiceVersion = new OwsServiceVersion( 1, 6, 9 );
        assertThat( owsServiceVersion.getVersionString(), is( "1.6.9" ) );
    }

    @Test
    public void testCompareToEqual() {
        OwsServiceVersion version = new OwsServiceVersion( 1, 6, 9 );
        assertThat( version.compareTo( new OwsServiceVersion( 1, 6, 9 ) ), is( 0 ) );
    }

    @Test
    public void testCompareToLess() {
        OwsServiceVersion version = new OwsServiceVersion( 1, 6, 9 );
        assertThat( version.compareTo( new OwsServiceVersion( 1, 3, 9 ) ), is( 1 ) );
    }

    @Test
    public void testCompareToGreater() {
        OwsServiceVersion version = new OwsServiceVersion( 1, 6, 9 );
        assertThat( version.compareTo( new OwsServiceVersion( 1, 7, 0 ) ), is( -1 ) );
    }

}