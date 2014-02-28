package org.deegree.securityproxy.authentication.ows.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class LimitedOwsServiceVersionTest {

    private static final OwsServiceVersion VERSION_090 = new OwsServiceVersion( 0, 9, 0 );

    private static final OwsServiceVersion VERSION_100 = new OwsServiceVersion( 1, 0, 0 );

    private static final OwsServiceVersion VERSION_110 = new OwsServiceVersion( 1, 1, 0 );

    @Test
    public void testConstructorEqualSingle()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "= 1.1.0" );
        assertThat( parsed.getVersion(), is( VERSION_110 ) );
        assertThat( parsed.getLimitType(), is( "==" ) );
    }

    @Test
    public void testConstructorEqualMultiple()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "== 1.1.0" );
        assertThat( parsed.getVersion(), is( VERSION_110 ) );
        assertThat( parsed.getLimitType(), is( "==" ) );
    }

    @Test
    public void testConstructorSmallerThanSingle()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "< 1.1.0" );
        assertThat( parsed.getVersion(), is( VERSION_110 ) );
        assertThat( parsed.getLimitType(), is( "<" ) );
    }

    @Test
    public void testConstructorGreaterThanSingle()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "> 1.1.0" );
        assertThat( parsed.getVersion(), is( VERSION_110 ) );
        assertThat( parsed.getLimitType(), is( ">" ) );
    }

    @Test
    public void testConstructorSmallerThanMultiple()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "<= 1.1.0" );
        assertThat( parsed.getVersion(), is( VERSION_110 ) );
        assertThat( parsed.getLimitType(), is( "<=" ) );
    }

    @Test
    public void testConstructorSmallerThanMultipleSwitched()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "=< 1.1.0" );
        assertThat( parsed.getVersion(), is( VERSION_110 ) );
        assertThat( parsed.getLimitType(), is( "<=" ) );
    }

    @Test
    public void testConstructorGreaterThanMultiple()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( ">= 1.0.0" );
        assertThat( parsed.getVersion(), is( VERSION_100 ) );
        assertThat( parsed.getLimitType(), is( ">=" ) );
    }

    @Test
    public void testConstructorGreaterThanMultipleSwitched()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "=> 1.0.0" );
        assertThat( parsed.getVersion(), is( VERSION_100 ) );
        assertThat( parsed.getLimitType(), is( ">=" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNull() {
        new LimitedOwsServiceVersion( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmpty() {
        new LimitedOwsServiceVersion( "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNotMatching() {
        new LimitedOwsServiceVersion( "greater 1.0.0" );
    }

    @Test
    public void testContainsInLimitGreaterEqual()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "=> 1.0.0" );
        assertThat( parsed.contains( VERSION_110 ), is( true ) );
    }

    @Test
    public void testContainsInLimitEqualGreaterEqual()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "=> 1.0.0" );
        assertThat( parsed.contains( VERSION_100 ), is( true ) );
    }

    @Test
    public void testContainsOutOfLimitGreaterEqual()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "=> 1.0.0" );
        assertThat( parsed.contains( VERSION_090 ), is( false ) );
    }

    @Test
    public void testContainsInLimitGreater()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "> 1.0.0" );
        assertThat( parsed.contains( VERSION_110 ), is( true ) );
    }

    @Test
    public void testContainsInLimitEqualGreater()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "> 1.0.0" );
        assertThat( parsed.contains( VERSION_100 ), is( false ) );
    }

    @Test
    public void testContainsOutOfLimitGreater()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "> 1.0.0" );
        assertThat( parsed.contains( VERSION_090 ), is( false ) );
    }

    @Test
    public void testContainsInLimitEqual()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "= 1.0.0" );
        assertThat( parsed.contains( VERSION_110 ), is( false ) );
    }

    @Test
    public void testContainsInLimitEqualEqua()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "= 1.0.0" );
        assertThat( parsed.contains( VERSION_100 ), is( true ) );
    }

    @Test
    public void testContainsOutOfLimitEqual()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "= 1.0.0" );
        assertThat( parsed.contains( VERSION_090 ), is( false ) );
    }

    @Test
    public void testContainsInLimitLessEqual()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "<= 1.0.0" );
        assertThat( parsed.contains( VERSION_110 ), is( false ) );
    }

    @Test
    public void testContainsInLimitEqualLessEqual()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "=< 1.0.0" );
        assertThat( parsed.contains( VERSION_100 ), is( true ) );
    }

    @Test
    public void testContainsOutOfLimitLessEqual()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "<= 1.0.0" );
        assertThat( parsed.contains( VERSION_090 ), is( true ) );
    }

    @Test
    public void testContainsInLimitLess()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "< 1.0.0" );
        assertThat( parsed.contains( VERSION_110 ), is( false ) );
    }

    @Test
    public void testContainsInLimitEqualLess()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "< 1.0.0" );
        assertThat( parsed.contains( VERSION_100 ), is( false ) );
    }

    @Test
    public void testContainsOutOfLimitLess()
                            throws Exception {
        LimitedOwsServiceVersion parsed = new LimitedOwsServiceVersion( "< 1.0.0" );
        assertThat( parsed.contains( VERSION_090 ), is( true ) );
    }

}