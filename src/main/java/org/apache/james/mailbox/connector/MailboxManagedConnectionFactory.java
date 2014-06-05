package org.apache.james.mailbox.connector;

import java.beans.Transient;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionDefinition;
import javax.security.auth.Subject;

import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.acl.SimpleGroupMembershipResolver;
import org.apache.james.mailbox.acl.UnionMailboxACLResolver;
import org.apache.james.mailbox.maildir.MaildirMailboxSessionMapperFactory;
import org.apache.james.mailbox.maildir.MaildirStore;
import org.apache.james.mailbox.store.Authenticator;
import org.apache.james.mailbox.store.StoreMailboxManager;
import org.teiid.resource.spi.BasicConnectionFactory;
import org.teiid.resource.spi.BasicManagedConnectionFactory;
import org.teiid.resource.spi.ConnectionContext;
import org.teiid.resource.spi.WrappedConnection;
import org.teiid.resource.spi.WrappedConnectionFactory;

@ConnectionDefinition(connection = Connection.class, 
	connectionImpl = WrappedConnection.class, 
	connectionFactory = ConnectionFactory.class, 
	connectionFactoryImpl = WrappedConnectionFactory.class)
public class MailboxManagedConnectionFactory extends
		BasicManagedConnectionFactory {

	/** serialization id */
	private static final long serialVersionUID = 6010906357203750621L;

	private String maildirFolder;

	private transient MailboxManager mailboxManager;

	@Override
	public BasicConnectionFactory<MailboxConnectionImpl> createConnectionFactory()
			throws ResourceException {
		mailboxManager = new StoreMailboxManager<Integer>(
				new MaildirMailboxSessionMapperFactory(new MaildirStore(
						maildirFolder)), new Authenticator() {

					@Override
					public boolean isAuthentic(String userid,
							CharSequence passwd) {
						return true;
					}
				}, new UnionMailboxACLResolver(),
				new SimpleGroupMembershipResolver());

		return new BasicConnectionFactory<MailboxConnectionImpl>() {

			private static final long serialVersionUID = 6776424312546669476L;

			@Override
			public MailboxConnectionImpl getConnection()
					throws ResourceException {
				String username = "";
				String password = "";
				// if security-domain is specified and caller identity is used;
				// then use
				// credentials from subject
				Subject subject = ConnectionContext.getSubject();
				if (subject != null) {
					username = ConnectionContext.getUserName(subject,
							MailboxManagedConnectionFactory.this, username);
					password = ConnectionContext.getPassword(subject,
							MailboxManagedConnectionFactory.this, username,
							password);
				}

				return new MailboxConnectionImpl(username, password,
						MailboxManagedConnectionFactory.this);
			}
		};
	}

	MailboxManager getMailboxManager() {
		return mailboxManager;
	}

	public String getMaildirFolder() {
		return maildirFolder;
	}

	@ConfigProperty(defaultValue = "/tmp/maildir")
	public void setMaildirFolder(String maildirFolder) {
		this.maildirFolder = maildirFolder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((maildirFolder == null) ? 0 : maildirFolder.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailboxManagedConnectionFactory other = (MailboxManagedConnectionFactory) obj;
		if (!checkEquals(this.maildirFolder, other.maildirFolder))
			return false;
		return true;
	}

}
