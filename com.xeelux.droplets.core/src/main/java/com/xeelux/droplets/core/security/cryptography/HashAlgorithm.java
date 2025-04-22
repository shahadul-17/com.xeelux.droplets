package com.xeelux.droplets.core.security.cryptography;

public enum HashAlgorithm {

    /**
     * 128-bits MD5 (Message Digest 5).
     */
    MD5("MD5"),
    /**
     * 160-bits SHA1 (Secure Hash Algorithm 1).
     */
    SHA160("SHA-1"),
    /**
     * 160-bits SHA1 (Secure Hash Algorithm 1).
     */
    SHA1_160("SHA-1"),
    /**
     * 256-bits SHA2 (Secure Hash Algorithm 2).
     */
    SHA256("SHA-256"),
    /**
     * 256-bits SHA2 (Secure Hash Algorithm 2).
     */
    SHA2_256("SHA-256"),
    /**
     * 384-bits SHA2 (Secure Hash Algorithm 2).
     */
    SHA384("SHA-384"),
    /**
     * 384-bits SHA2 (Secure Hash Algorithm 2).
     */
    SHA2_384("SHA-384"),
    /**
     * 512-bits SHA2 (Secure Hash Algorithm 2).
     */
    SHA512("SHA-512"),
    /**
     * 512-bits SHA2 (Secure Hash Algorithm 2).
     */
    SHA2_512("SHA-512"),
    /**
     * 256-bits SHA3 (Secure Hash Algorithm 3).
     */
    SHA3_256("SHA3-256"),
    /**
     * 384-bits SHA3 (Secure Hash Algorithm 3).
     */
    SHA3_384("SHA3-384"),
    /**
     * 512-bits SHA3 (Secure Hash Algorithm 3).
     */
    SHA3_512("SHA3-512");

    private final String name;

    HashAlgorithm(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
