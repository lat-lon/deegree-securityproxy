package org.deegree.securityproxy.request;

/**
 * 
 * Encapsulates an ows service version with three values (x.y.z, e.g 1.2.0).
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class OwsServiceVersion implements Comparable<OwsServiceVersion> {

    private final int versionX;

    private final int versionY;

    private final int versionZ;

    /**
     * @param versionString
     *            to parse, must match to the expected pattern (x.y.z), never <code>null</code> or empty
     * @throws IllegalArgumentException
     *             if versionString is <code>null</code> or empty or does not match to the expected pattern
     */
    public OwsServiceVersion( String versionString ) {
        if ( versionString == null || versionString.isEmpty() )
            throw new IllegalArgumentException( exceptionString( versionString ) );
        String[] split = versionString.split( "\\." );
        if ( split.length != 3 )
            throw new IllegalArgumentException( exceptionString( versionString ) );
        try {
            versionX = Integer.parseInt( split[0] );
            versionY = Integer.parseInt( split[1] );
            versionZ = Integer.parseInt( split[2] );
        } catch ( NumberFormatException e ) {
            throw new IllegalArgumentException( exceptionString( versionString ) );
        }
    }

    /**
     * Creates a version from the three ints.
     * 
     * @param versionX
     * @param versionY
     * @param versionZ
     */
    public OwsServiceVersion( int versionX, int versionY, int versionZ ) {
        this.versionX = versionX;
        this.versionY = versionY;
        this.versionZ = versionZ;
    }

    /**
     * @return the version as string (x.y.z)
     */
    public String getVersionString() {
        return versionX + "." + versionY + "." + versionZ;
    }

    /**
     * @return the versionX
     */
    public int getVersionX() {
        return versionX;
    }

    /**
     * @return the versionY
     */
    public int getVersionY() {
        return versionY;
    }

    /**
     * @return the versionZ
     */
    public int getVersionZ() {
        return versionZ;
    }

    @Override
    public int compareTo( OwsServiceVersion versionToCompare ) {
        int compareToX = Integer.valueOf( versionX ).compareTo( versionToCompare.versionX );
        if ( compareToX == 0 ) {
            int compareToY = Integer.valueOf( versionY ).compareTo( versionToCompare.versionY );
            if ( compareToY == 0 ) {
                return Integer.valueOf( versionZ ).compareTo( versionToCompare.versionZ );
            }
            return compareToY;
        }
        return compareToX;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + versionX;
        result = prime * result + versionY;
        result = prime * result + versionZ;
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        OwsServiceVersion other = (OwsServiceVersion) obj;
        if ( versionX != other.versionX )
            return false;
        if ( versionY != other.versionY )
            return false;
        if ( versionZ != other.versionZ )
            return false;
        return true;
    }

    private String exceptionString( String versionString ) {
        return "Version " + versionString + " is not a valid ows service version (e.g. 2.1.0)";
    }

}