package com.xeelux.droplets.core.dependencyinjection;

public interface ServiceProvider {

    /**
     * Retrieves the instance of the service class.
     * @param serviceClass Service class of which the instance shall be retrieved.
     * @return Returns the instance of the provided service class.
     * @param <Type> Type of the service class.
     */
    <Type> Type get(final Class<Type> serviceClass);

    /**
     * Retrieves the instance of the service class.
     * @param key Key corresponding to which the service shall be registered/retrieved.
     * @param serviceClass Service class of which the instance shall be retrieved.
     * @return Returns the instance of the provided service class.
     * @param <Type> Type of the service class.
     */
    <Type> Type get(final String key, final Class<Type> serviceClass);

    /**
     * Retrieves the instance of the service class.
     * @implNote This method shall be used to register services that has constructor
     * arguments. Once the service is registered, instantiator is no longer required.
     * @param serviceClass Service class of which the instance shall be retrieved.
     * @param instantiator The instance returned by the instantiator shall be
     *                     registered to the service provider.
     * @return Returns the instance of the provided service class.
     * @param <Type> Type of the service class.
     */
    <Type> Type get(final Class<Type> serviceClass, final ServiceInstantiator<Type> instantiator);

    /**
     * Retrieves the instance of the service class.
     * @implNote This method shall be used to register services that has constructor
     * arguments. Once the service is registered, instantiator is no longer required.
     * @param key Key corresponding to which the service shall be registered/retrieved.
     * @param serviceClass Service class of which the instance shall be retrieved.
     * @param instantiator The instance returned by the instantiator shall be
     *                     registered to the service provider.
     * @return Returns the instance of the provided service class.
     * @param <Type> Type of the service class.
     */
    <Type> Type get(final String key, final Class<Type> serviceClass, final ServiceInstantiator<Type> instantiator);

    /**
     * This method is used to get the singleton service provider.
     * @return Returns the singleton service provider.
     */
    static ServiceProvider getSingleton() {
        return SingletonServiceProvider.getInstance();
    }
}
