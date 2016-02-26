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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class PersistenceFactory {

	private final static String  PROPERTIES_FILE_NAME  = "/META-INF/persistence.properties";
	private final static String  PERSISTENCE_UNIT_NAME = "persistenceUnitName";

	private final static PersistenceFactory persistenceFactory = new PersistenceFactory(); 
		
	private final String persistenceUnitName ; 
	private final EntityManagerFactory emf ; 
	
	private PersistenceFactory() {
		super();
		final Properties properties = new Properties();
		InputStream stream = this.getClass().getResourceAsStream(PROPERTIES_FILE_NAME) ;
		if ( stream == null ) {
			throw new RuntimeException("Cannot found propertie file " + PROPERTIES_FILE_NAME );
		}
		try {
		    properties.load(stream);
		}
		catch ( IOException e ) {
			throw new RuntimeException("IOException : Cannot load propertie from file " + PROPERTIES_FILE_NAME );
		}
		finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new RuntimeException("IOException : Cannot close file stream " + PROPERTIES_FILE_NAME );
			}
		}
		
		persistenceUnitName = properties.getProperty(PERSISTENCE_UNIT_NAME);
		if ( persistenceUnitName != null ) {
			// Create and keep the EntityManagerFactory
			emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		}
		else {
			throw new RuntimeException("Property '" + PERSISTENCE_UNIT_NAME + "' not found in file '" + PROPERTIES_FILE_NAME + "'");
		}
	}
	
	private EntityManagerFactory getEntityManagerFactory() {
		return this.emf;
	}
	
	/**
	 * PersistenceFactory singleton initialization <br>
	 * Creates the single instance if not yet initialized
	 * @return
	 */
	public final static void init() {
		// Just force the class loading 
	}

	/**
	 * Returns the persistence unit name for the current PersistenceFactory
	 * @return
	 */
	public final static String getPersistenceUnitName() {
		return persistenceFactory.persistenceUnitName;
	}

	public final static EntityManager createEntityManager() {
		return persistenceFactory.getEntityManagerFactory().createEntityManager();
	}
}
