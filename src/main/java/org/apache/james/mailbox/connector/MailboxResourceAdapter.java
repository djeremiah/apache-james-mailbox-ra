package org.apache.james.mailbox.connector;

import javax.resource.spi.AuthenticationMechanism;
import javax.resource.spi.AuthenticationMechanism.CredentialInterface;
import javax.resource.spi.Connector;

import org.teiid.resource.spi.BasicResourceAdapter;

@Connector(reauthenticationSupport=false,
	authMechanisms = { 
		@AuthenticationMechanism(authMechanism = "BasicPassword", 
				credentialInterface = CredentialInterface.PasswordCredential) })
public class MailboxResourceAdapter extends BasicResourceAdapter {

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return true;
	}

}
