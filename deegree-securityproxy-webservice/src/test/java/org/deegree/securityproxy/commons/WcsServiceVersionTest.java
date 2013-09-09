package org.deegree.securityproxy.commons;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class WcsServiceVersionTest {

    @Test
    public void testParseVersions100()
                            throws Exception {
        List<WcsServiceVersion> parsed = WcsServiceVersion.parseVersions( "1.0.0" );
        List<WcsServiceVersion> expected = Collections.singletonList( WcsServiceVersion.VERSION_100 );
        assertThat( parsed, is( expected ) );
    }

    @Test
    public void testParseVersions110()
                            throws Exception {
        List<WcsServiceVersion> parsed = WcsServiceVersion.parseVersions( "= 1.1.0" );
        List<WcsServiceVersion> expected = Collections.singletonList( WcsServiceVersion.VERSION_110 );
        assertThat( parsed, is( expected ) );
    }

    @Test
    public void testParseVersionsSmallerThanSingle()
                            throws Exception {
        List<WcsServiceVersion> parsed = WcsServiceVersion.parseVersions( "< 1.1.0" );
        List<WcsServiceVersion> expected = Collections.singletonList( WcsServiceVersion.VERSION_100 );
        assertThat( parsed, is( expected ) );
    }

    @Test
    public void testParseVersionsSmallerThanMultiple()
                            throws Exception {
        List<WcsServiceVersion> parsed = WcsServiceVersion.parseVersions( "<= 1.1.0" );
        List<WcsServiceVersion> expected = new ArrayList<WcsServiceVersion>();
        expected.add( WcsServiceVersion.VERSION_100 );
        expected.add( WcsServiceVersion.VERSION_110 );
        assertThat( parsed, is( expected ) );
    }

    @Test
    public void testParseVersionsGreaterThanSingle()
                            throws Exception {
        List<WcsServiceVersion> parsed = WcsServiceVersion.parseVersions( "> 1.1.0" );
        List<WcsServiceVersion> expected = Collections.singletonList( WcsServiceVersion.VERSION_200 );
        assertThat( parsed, is( expected ) );
    }

    @Test
    public void testParseVersionsGreaterThanMultiple()
                            throws Exception {
        List<WcsServiceVersion> parsed = WcsServiceVersion.parseVersions( ">= 1.0.0" );
        List<WcsServiceVersion> expected = new ArrayList<WcsServiceVersion>();
        expected.add( WcsServiceVersion.VERSION_100 );
        expected.add( WcsServiceVersion.VERSION_110 );
        expected.add( WcsServiceVersion.VERSION_200 );
        assertThat( parsed, is( expected ) );
    }

    @Test
    public void testParseWrongVersion() {
        WcsServiceVersion.parseVersions( "<= 1.3.0" );
        WcsServiceVersion.parseVersions( "= 1.3.0" );
        WcsServiceVersion.parseVersions( "1.3.0" );
        WcsServiceVersion.parseVersions( ">= 1.3.0" );

    }
}
