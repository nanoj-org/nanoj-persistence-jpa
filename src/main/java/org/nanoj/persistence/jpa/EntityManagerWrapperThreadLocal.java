package org.nanoj.persistence.jpa;


public class EntityManagerWrapperThreadLocal {
	
    private static final ThreadLocal<EntityManagerWrapper> emwThreadLocal = new ThreadLocal<EntityManagerWrapper>();

    protected static void set(EntityManagerWrapper em) {
    	emwThreadLocal.set(em);
    }

    protected static void remove() {
    	emwThreadLocal.remove();
    }

    public static EntityManagerWrapper get() {
        return emwThreadLocal.get();
    }
}
