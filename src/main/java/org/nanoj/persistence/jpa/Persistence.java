package org.nanoj.persistence.jpa;

public class Persistence {

	public final static PersistenceTransaction beginTransaction() {
		PersistenceTransaction transaction = new PersistenceTransaction();
		transaction.beginTransaction();
		return transaction;
	}

	public final static PersistenceEnvironment getPersistenceEnvironment() {
		PersistenceEnvironment environement = new PersistenceEnvironment();
		environement.openEnvironment();
		return environement ;
	}
}
