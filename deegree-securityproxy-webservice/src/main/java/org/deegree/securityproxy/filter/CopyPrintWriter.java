package org.deegree.securityproxy.filter;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * see http://stackoverflow.com/questions/3242236/capture-and-log-the-response-body
 * 
 * 
 * @version $Revision: $, $Date: $
 */
public class CopyPrintWriter extends PrintWriter {

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public CopyPrintWriter( Writer writer ) {
        super( writer );
    }

    @Override
    public void write( int c ) {
        baos.write( c );
        super.write( c );
    }

    @Override
    public void write( char[] chars, int offset, int length ) {
        byte[] bytes = new String( chars ).getBytes();
        baos.write( bytes, offset, length );
        super.write( chars, offset, length );
    }

    @Override
    public void write( String string, int offset, int length ) {
        baos.write( string.getBytes(), offset, length );
        super.write( string, offset, length );
    }

    public byte[] getCopy() {
        return baos.toByteArray();
    }

}