package com.xeelux.droplets.core.dependencyinjection;

import com.xeelux.droplets.core.utilities.StringUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SingletonServiceProvider implements ServiceProvider {

    private final Logger logger = LogManager.getLogger(SingletonServiceProvider.class);
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);      // <-- this lock is used for thread synchronization...
    // NOTE: READ LOCK CAN BE ACQUIRED BY MULTIPLE THREADS SIMULTANEOUSLY
    // WHEN NO OTHER THREAD HAS ACQUIRED THE WRITE LOCK...
    private final Lock readLock = readWriteLock.readLock();
    // NOTE: WRITE LOCK CAN ONLY BE ACQUIRED BY A SINGLE THREAD...
    private final Lock writeLock = readWriteLock.writeLock();
    private final Map<String, Object> instanceMap = new HashMap<>();

    private static final ServiceProvider serviceProvider = new SingletonServiceProvider();

    private SingletonServiceProvider() { }

    private <Type> boolean isInstanceAvailable(final Object instance, final Class<Type> serviceClass) {
        return instance != null && serviceClass.isAssignableFrom(instance.getClass());
    }

    private Object getInstance(final String key) {
        // we'll retrieve the instance from the map...
        // NOTE: IF MAP DOES NOT CONTAIN ANY ENTRY FOR
        // THE NAME, RETURNS NULL...
        final var instance = instanceMap.get(key);

        // returns the instance...
        return instance;
    }

    private <Type> Object createInstance(
            final String key,
            final Class<Type> serviceClass,
            final ServiceInstantiator<Type> instantiator) {
        Object instance;

        try {
            // if the instantiator is not provided, we shall try to use reflection
            // to initialize an instance of the provided service class...
            if (instantiator == null) {
                // for the sake of simplicity, we'll only focus on no-argument constructor...
                final var constructor = serviceClass.getDeclaredConstructor();
                constructor.setAccessible(true);        // constructor might be private...
                instance = constructor.newInstance();
            } else {
                // otherwise, we shall utilize the instantiator to
                // initialize an instance of the provided service class...
                instance = instantiator.instantiate();

                // if the instantiate() method returns null...
                if (instance == null) {
                    // we shall write a warning log...
                    logger.log(Level.WARN, "Instantiator returned 'null' for \"{}\".", key);
                }
            }

            // logger.log(Level.DEBUG, "Service instantiation succeeded for \"" + key + "\".");
        } catch (final Exception exception) {
            logger.log(Level.ERROR, "An exception occurred while instantiating service of \"{}\".", key, exception);

            return null;
        }

        return instance;
    }

    @Override
    public <Type> Type get(final Class<Type> serviceClass) {
        return get(serviceClass, null);
    }

    @Override
    public <Type> Type get(final String key, final Class<Type> serviceClass) {
        // returns the singleton instance of the service...
        return get(key, serviceClass, null);
    }

    @Override
    public <Type> Type get(
            final Class<Type> serviceClass,
            final ServiceInstantiator<Type> instantiator) {
        // retrieves the type name from the service class (if not null)...
        final var typeName = serviceClass == null     // <-- if service class is null...
                ? StringUtilities.getEmptyString()          // <-- we shall assign an empty string...
                : serviceClass.getTypeName();               // <-- otherwise, we shall retrieve the type name...

        // returns the singleton instance of the service...
        return get(typeName, serviceClass, instantiator);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <Type> Type get(
            final String key,
            final Class<Type> serviceClass,
            final ServiceInstantiator<Type> instantiator) {
        // if service class is null, we shall return null...
        if (serviceClass == null) { return null; }

        // sanitizing the key...
        final var sanitizedKey = StringUtilities.getDefaultIfNullOrWhiteSpace(
                key, StringUtilities.getEmptyString(), true);

        // if the key is empty, we shall not proceed any further...
        if (StringUtilities.isEmpty(sanitizedKey)) { return null; }

        readLock.lock();            // <-- read synchronization starts here...

        // retrieves the instance if exists or null if it doesn't...
        var instance = getInstance(sanitizedKey);

        readLock.unlock();          // <-- read synchronization ends here...

        // checks if the instance retrieved from the map is available...
        var instanceAvailable = isInstanceAvailable(instance, serviceClass);

        // if instance is available...
        if (instanceAvailable) {
            // logger.log(Level.DEBUG, "Reusing existing service instance for \"" + sanitizedKey + "\".");

            // we'll return the instance...
            return (Type) instance;
        }

        // logger.log(Level.DEBUG, "Instantiating service named \"" + sanitizedKey + "\".");

        // NOTE: ANOTHER THREAD SHALL WAIT HERE IF INSTANCE IS NOT FOUND.
        // BECAUSE, WE CAN ALLOW ONLY ONE THREAD TO CREATE INSTANCE AND PLACE
        // IT ON THE MAP...
        // we shall acquire the write lock...
        writeLock.lock();       // <-- write synchronization starts here...

        // we'll try to retrieve the instance again...
        // NOTE: THIS IS BECAUSE, WHEN THE THREAD WAS WAITING
        // FOR ANOTHER THREAD TO RELEASE THE WRITE LOCK,
        // THE INSTANCE MIGHT HAVE ALREADY BEEN CREATED...
        instance = getInstance(sanitizedKey);
        // we shall check again if the instance retrieved from the map is available...
        instanceAvailable = isInstanceAvailable(instance, serviceClass);

        // if the instance is available this time...
        if (instanceAvailable) {
            // we shall release the write lock...
            writeLock.unlock();     // <-- write synchronization ends here...

            // logger.log(Level.DEBUG, "Reusing existing service instance for \"" + sanitizedKey + "\".");

            // we'll return the instance...
            return (Type) instance;
        }

        // otherwise, if the instance is not available,
        // we shall create a new instance...
        instance = createInstance(sanitizedKey, serviceClass, instantiator);

        // we shall put the instance in the map...
        instanceMap.put(sanitizedKey, instance);

        // we shall release the write lock...
        writeLock.unlock();     // <-- write synchronization ends here...

        // we'll return the instance...
        return (Type) instance;
    }

    static ServiceProvider getInstance() {
        return serviceProvider;
    }
}
