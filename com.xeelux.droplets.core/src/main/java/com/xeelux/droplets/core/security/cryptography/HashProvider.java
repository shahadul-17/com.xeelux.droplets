package com.xeelux.droplets.core.security.cryptography;

import com.xeelux.droplets.core.dependencyinjection.ServiceProvider;
import com.xeelux.droplets.core.text.Encoding;

public interface HashProvider {

    byte[] computeHash(final byte[] bytes, final HashAlgorithm algorithm) throws Exception;

    byte[] computeHash(final String message, final HashAlgorithm algorithm) throws Exception;

    String computeHash(final String message, final HashAlgorithm algorithm, final Encoding encoding) throws Exception;

    boolean isMatched(final byte[] bytes, final byte[] preComputedHashAsBytes, final HashAlgorithm algorithm) throws Exception;

    boolean isMatched(final String message, final byte[] preComputedHashAsBytes, final HashAlgorithm algorithm) throws Exception;

    boolean isMatched(final String message, final String preComputedHash, final HashAlgorithm algorithm, final Encoding preComputedHashEncoding) throws Exception;

    static HashProvider getInstance() {
        final var serviceProvider = ServiceProvider.getSingleton();
        final var hashProvider = serviceProvider.get(HashProvider.class, HashProviderImpl::new);

        return hashProvider;
    }
}
