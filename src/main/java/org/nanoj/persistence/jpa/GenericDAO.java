package org.nanoj.persistence.jpa;

import javax.persistence.EntityManager;

public abstract class GenericDAO {

	protected EntityManager getEntityManager() {
		EntityManagerWrapper entityManagerWrapper = EntityManagerWrapperThreadLocal.get();
		if ( entityManagerWrapper != null ) {
			return entityManagerWrapper.getEntityManager();
		}
		else {
			throw new IllegalStateException("No current EntityManager");
		}
	}
}
