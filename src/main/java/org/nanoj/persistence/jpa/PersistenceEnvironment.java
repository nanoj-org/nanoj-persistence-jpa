package org.nanoj.persistence.jpa;

import java.io.Closeable;

public class PersistenceEnvironment implements Closeable {
	
	private static long environmentCount = 0 ;
	
	private final long environmentId ;
	
	private final void log(String message) {
		System.out.println("LOG - PersistenceEnvironment : " + message);
	}
	
	private synchronized long getNewEnvironmentId() {
		if ( environmentCount < 999999) {
			environmentCount++;
		}
		else {
			environmentCount = 1;
		}
		return environmentCount ;
	}
	
	protected PersistenceEnvironment() {
		super();
		environmentId = getNewEnvironmentId();
		log("new environment #"+environmentId);
	}
	
	public long getId() {
		return this.environmentId ;
	}
	
	protected void openEnvironment() {
		log("open environment #"+environmentId);
		EntityManagerWrapper emw = EntityManagerWrapperThreadLocal.get();
		if ( emw == null ) {
			PersistenceManager.openEntityManager(this);
		}
//		else {
//			em = emw.getEntityManager();
//		}
	}

	@Override
	public void close() {
		log("close environment #"+environmentId);
		EntityManagerWrapper emw = EntityManagerWrapperThreadLocal.get();
		if ( emw == null ) {
			throw new IllegalStateException("Cannot close : no current EntityManagerWrapper");
		}
		
		// CLOSE ENTITY MANAGER IF IT BELONGS TO THIS ENVIRONMENT 
		if ( emw.isBelongToEnvironment(environmentId) ) {
			PersistenceManager.closeEntityManager();
		}
	}

}
