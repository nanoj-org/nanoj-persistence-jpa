package org.nanoj.persistence.jpa;

import javax.persistence.EntityManager;

public class PersistenceManager {

	private final static void log(String msg) { 
		System.out.println("LOG - PersistenceContext : " + msg );
	}
	
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Opens a new EntityManager and keep it in the current thread (with "ThreadLocal") <br>
	 * @return
	 */
	public final static EntityManager openEntityManager() {
		log("openEntityManager()...");
		EntityManager em = PersistenceFactory.createEntityManager();
		log("em created");
		EntityManagerWrapperThreadLocal.set(new EntityManagerWrapper(em));
		log("em stored in ThreadLocal");
		return em;
	}
	
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Open a new EntityManager and keep it in the current thread (with "ThreadLocal")
	 * @param transaction the transaction creating the EntityManager, or null if not created by a transaction 
	 */
	public final static EntityManager openEntityManager(PersistenceTransaction transaction) {
		log("openEntityManager(PersistenceTransaction)...");
		if ( transaction == null ) {
			throw new IllegalArgumentException("Transaction is null");
		}
		EntityManager em = PersistenceFactory.createEntityManager();
		log("em created");
		EntityManagerWrapperThreadLocal.set(new EntityManagerWrapper(em, transaction));
		log("em stored in ThreadLocal");
		return em;
	}
	
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Open a new EntityManager and keep it in the current thread (with "ThreadLocal")
	 * @param transaction the transaction creating the EntityManager, or null if not created by a transaction 
	 */
	public final static EntityManager openEntityManager(PersistenceEnvironment environment) {
		log("openEntityManager(PersistenceEnvironment)...");
		if ( environment == null ) {
			throw new IllegalArgumentException("PersistenceEnvironment is null");
		}
		EntityManager em = PersistenceFactory.createEntityManager();
		log("em created");
		EntityManagerWrapperThreadLocal.set(new EntityManagerWrapper(em, environment));
		log("em stored in ThreadLocal");
		return em;
	}
	
	//-----------------------------------------------------------------------------------------------------
	/**
	 * Closes the current EntityManager located in the current thread (with "ThreadLocal") <br>
	 * and remove it from the thread
	 */
	public final static void closeEntityManager() {
		log("closeEntityManager()...");
		EntityManagerWrapper emw = EntityManagerWrapperThreadLocal.get();
		if ( emw != null ) {
			
			// Close the EntityManager
			EntityManager em = emw.getEntityManager();
			log("em.close()");
			em.close();
			
			// Remove the EntityManagerWrapper from the thread
			log("EntityManagerThreadLocal.remove()");
			EntityManagerWrapperThreadLocal.remove();
		}
	}
}
