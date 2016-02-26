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

public class EntityManagerWrapper {
	
	private final static long NO_TRANSACTION_ID = -1 ;
	private final static long NO_ENVIRONMENT_ID = -1 ;
	
	private final EntityManager entityManager ;
	
	private final void log(String message) {
		System.out.println("LOG - EntityManagerWrapper : " + message);
	}

	/**
	 * Contains the id of the transaction which has created the current EntityManager <br>
	 * or -1 if the current EntityManager has not been created by a transaction <br>
	 * for example : -1 if the EntityManager has been created by a ServletFilter ("Session In View Pattern") 
	 */
	private final long transactionId ;

	private final long environmentId ;
	
	/**
	 * Constructor <br>
	 * Creates an independent EntityManagerWrapper (not belonging to transaction or environment)
	 * @param em
	 */
	public EntityManagerWrapper(EntityManager em) {
		super();
		if ( em == null ) {
			throw new IllegalArgumentException("EntityManager is null");
		}
		this.entityManager = em ;
		log("NEW EntityManagerWrapper");
		this.transactionId = NO_TRANSACTION_ID ;
		this.environmentId = NO_ENVIRONMENT_ID ;
	}

	/**
	 * Constructor <br>
	 * Creates an EntityManagerWrapper associated with the given transaction
	 * @param em
	 * @param transaction
	 */
	public EntityManagerWrapper(EntityManager em, PersistenceTransaction transaction) {
		super();
		if ( em == null ) {
			throw new IllegalArgumentException("EntityManager is null");
		}
		if ( transaction == null ) {
			throw new IllegalArgumentException("Transaction is null");
		}
		this.entityManager = em ;
		log("NEW EntityManagerWrapper belonging to transaction #" + transaction.getId() );
		this.transactionId = transaction.getId() ;
		this.environmentId = NO_ENVIRONMENT_ID ;
	}

	/**
	 * Constructor <br>
	 * Creates an EntityManagerWrapper associated with the given environment
	 * @param em
	 * @param persistenceEnvironment
	 */
	public EntityManagerWrapper(EntityManager em, PersistenceEnvironment persistenceEnvironment) {
		super();
		if ( em == null ) {
			throw new IllegalArgumentException("EntityManager is null");
		}
		if ( persistenceEnvironment == null ) {
			throw new IllegalArgumentException("PersistenceEnvironment is null");
		}
		this.entityManager = em ;
		this.transactionId = NO_TRANSACTION_ID ;
		log("NEW EntityManagerWrapper belonging to environement #" + persistenceEnvironment.getId() );
		this.environmentId = persistenceEnvironment.getId() ;
	}

	/**
	 * Returns the real JPA EntityManager
	 * @return
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * Returns true if it belongs to the transaction identified by the given Id
	 * @param transactionId
	 * @return
	 */
	public boolean isBelongToTransaction(long transactionId) {
		return this.transactionId == transactionId ;
	}	
	
	/**
	 * Returns true if it belongs to the environment identified by the given Id
	 * @param environmentId
	 * @return
	 */
	public boolean isBelongToEnvironment(long environmentId) {
		return this.environmentId == environmentId ;
	}	
}
