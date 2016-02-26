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
