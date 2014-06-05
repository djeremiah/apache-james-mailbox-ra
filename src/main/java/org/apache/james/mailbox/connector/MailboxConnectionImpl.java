package org.apache.james.mailbox.connector;

import javax.resource.ResourceException;
import javax.resource.spi.SecurityException;

import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.exception.BadCredentialsException;
import org.apache.james.mailbox.exception.MailboxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teiid.resource.spi.BasicConnection;

public class MailboxConnectionImpl extends BasicConnection {
	
	private static final Logger LOG = LoggerFactory.getLogger(MailboxConnectionImpl.class);
	
	private final MailboxManager mailboxManager;
	private final MailboxSession mailboxSession;
	
	public MailboxConnectionImpl(final String username, final String password, final MailboxManagedConnectionFactory mcf) throws ResourceException{
		mailboxManager = mcf.getMailboxManager();
		try {
			mailboxSession = mailboxManager.login(username, password, LOG);
		} catch (BadCredentialsException e) {
			throw new SecurityException("Invalid mailbox credentials", e);
		} catch (MailboxException e) {
			throw new ResourceException("Could not log into mailbox", e);
		}
		
	}

	@Override
	public void close() throws ResourceException {
		try {
			mailboxManager.logout(mailboxSession, false);
		} catch (MailboxException e) {
			throw new ResourceException("Could not close mailbox: " + mailboxSession.getSessionId(),e);
		}
	}

}
