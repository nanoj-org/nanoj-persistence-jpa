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

import java.io.Closeable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class PersistenceTransaction implements Closeable {

	private static long transactionCount = 0 ;
	
	private final long transactionId ;
	
	private final void log(String message) {
		System.out.println("LOG - Transaction : " + message);
	}
	
//	private final EntityManager getEntityManager() {
//		EntityManagerWrapper emw = EntityManagerWrapperThreadLocal.get();
//		if ( emw == null ) {
//			throw new IllegalStateException("No EntityManager available, cannot manage transaction");
//		}
//		return emw.getEntityManager() ;
//	}
	
	private synchronized long getNewTransactionId() {
		if ( transactionCount < 999999) {
			transactionCount++;
		}
		else {
			transactionCount = 1;
		}
		return transactionCount ;
	}
	
	protected PersistenceTransaction() {
		super();
		transactionId = getNewTransactionId();
		log("new transaction #"+transactionId);
//		// BEGIN TRANSACTION
//		getEntityManager().getTransaction().begin() ;
//		// If the transaction is already active -> IllegalStateException
		registerMainTransactionIfNecessary();
	}

	protected void beginTransaction() {
		log("begin transaction #"+transactionId);
		EntityManager em = null ;
		EntityManagerWrapper emw = EntityManagerWrapperThreadLocal.get();
		if ( emw == null ) {
			//EntityManagerWrapperThreadLocal.set(new EntityManagerWrapper(em));
			em = PersistenceManager.openEntityManager(this);
		}
		else {
			em = emw.getEntityManager();
		}
		if ( this.isMainTransaction() ) {
			em.getTransaction().begin() ;
			// If the transaction is already active -> IllegalStateException
		}
	}

	public long getId() {
		return this.transactionId ;
	}
	
	public void commit() {
		log("commit transaction #"+transactionId);
		EntityManagerWrapper emw = EntityManagerWrapperThreadLocal.get();
		if ( emw == null ) {
			throw new IllegalStateException("Cannot commit : no current EntityManager");
		}
		else {
			// COMMIT TRANSACTION ONLY IF THIS TRANSACTION IS THE "TOP TRANSACTION" 
			if ( this.isMainTransaction() ) {
				EntityManager em = emw.getEntityManager();
				log("COMMIT transaction #"+transactionId);
				em.getTransaction().commit();
			}
		}
	}
	
	/**
	 * Rollback the transaction if it has not been yet commited
	 */
	private void rollbackIfNotCommited( EntityManagerWrapper emw ) {
		EntityManager em = emw.getEntityManager();	
		
		EntityTransaction entityTransaction = em.getTransaction() ;
		// The transaction is still active and the "commit" has not been done (due to an Exception)
		if ( entityTransaction.isActive() ) {
			// ROLLBACK TRANSACTION
			log("ROLLBACK #"+transactionId);
			entityTransaction.rollback();
		}
		// else : the COMMIT has been done successfully before the close => do not rollback
	}
	
	@Override
	public void close()  {
		if ( this.isMainTransaction() ) {
			// END OF CURRENT MAIN TRANSACTION 
			log("close transaction #"+transactionId + " (MAIN TRANSACTION) ");
			
			// ROLLBACK IF NOT YET COMMITED
			EntityManagerWrapper emw = EntityManagerWrapperThreadLocal.get();
			if ( emw == null ) {
				throw new IllegalStateException("Cannot close : no current EntityManager");
			}
			else {
				rollbackIfNotCommited(emw);
			}
			
			// CLOSE ENTITY MANAGER IF IT BELONGS TO THIS TRANSACTION 
			if ( emw.isBelongToTransaction(transactionId) ) {
				PersistenceManager.closeEntityManager();
			}
			
			// UNREGISTER THIS MAIN TRANSACTION   
			unregisterMainTransaction();
		}
		else {
			log("close transaction #"+transactionId + " (not main transaction)");
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	// Current "main transaction" management
	//-------------------------------------------------------------------------------------------------
    private static final ThreadLocal<PersistenceTransaction> mainTransactionThreadLocal = new ThreadLocal<PersistenceTransaction>();

	private void registerMainTransactionIfNecessary() {
		if ( mainTransactionThreadLocal.get() == null ) {
			// No current main transaction => register this as "main transaction"
			log("register main transaction #"+transactionId );
			mainTransactionThreadLocal.set(this);
		}
	}
	
	private void unregisterMainTransaction() {
		log("unregister main transaction #"+transactionId );
		mainTransactionThreadLocal.remove();
	}
	
	private boolean isMainTransaction() {
		PersistenceTransaction mainTransaction = mainTransactionThreadLocal.get() ;
		if ( mainTransaction != null ) {
			return mainTransaction.getId() == this.getId() ;
		}
		else {
			throw new IllegalStateException("Current main transaction not found");
		}
	}
}
