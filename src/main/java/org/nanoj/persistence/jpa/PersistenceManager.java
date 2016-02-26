/**
 *  Copyright (C) 2013-2016 Laurent GUERIN - NanoJ project org. ( http://www.nanoj.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
