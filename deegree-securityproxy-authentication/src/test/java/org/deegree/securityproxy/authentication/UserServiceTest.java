package org.deegree.securityproxy.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.deegree.securityproxy.authentication.repository.UserDao;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceTest {

    private static final String KNOWN_USER_NAME = "knownUser";

    @Test
    public void testLoadUserByUsernameWithKnownUsernameShouldReturnDetails()
                            throws Exception {
        UserDao userDao = mockUserDao();
        UserService userService = new UserService( userDao );
        UserDetails userDetails = userService.loadUserByUsername( KNOWN_USER_NAME );
        assertThat( userDetails, is( notNullValue() ) );
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameWithUnknownUsernameShouldFail()
                            throws Exception {
        UserDao userDao = mockUserDao();
        UserService user = new UserService( userDao );
        user.loadUserByUsername( "notKnown" );
    }

    private UserDao mockUserDao() {
        UserDao userDao = mock( UserDao.class );
        when( userDao.retrieveUserByName( KNOWN_USER_NAME ) ).thenReturn( mock( UserDetails.class ) );
        return userDao;
    }

}