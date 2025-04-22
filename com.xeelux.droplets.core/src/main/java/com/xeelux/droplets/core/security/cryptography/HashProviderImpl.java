package com.xeelux.droplets.core.security.cryptography;

import com.xeelux.droplets.core.text.Encoder;
import com.xeelux.droplets.core.text.Encoding;
import com.xeelux.droplets.core.utilities.CollectionUtilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

class HashProviderImpl implements HashProvider {

    HashProviderImpl() { }

    @Override
    public byte[] computeHash(final byte[] bytes, final HashAlgorithm algorithm) throws Exception {
        // creates an instance of message digest...
        final var messageDigest = createMessageDigest(algorithm);
        // updates the digest...
        messageDigest.update(bytes);

        // computes the hash and performs any final operations necessary (e.g. padding)...
        final var computedHash = messageDigest.digest();

        return computedHash;
    }

    @Override
    public byte[] computeHash(final String message, final HashAlgorithm algorithm) throws Exception {
        // converts the message into an array of bytes...
        final var messageAsByteArray = message.getBytes(StandardCharsets.UTF_8);

        // computes hash of the message...
        return computeHash(messageAsByteArray, algorithm);
    }

    @Override
    public String computeHash(
            final String message,
            final HashAlgorithm algorithm,
            final Encoding encoding) throws Exception {
        // computes hash of the message...
        final var computedHash = computeHash(message, algorithm);
        // encodes the computed hash using the specified encoding...
        final var encodedComputedHash = Encoder.encode(computedHash, encoding);

        // returns the encoded hash...
        return encodedComputedHash;
    }

    @Override
    public boolean isMatched(
            final byte[] bytes,
            final byte[] preComputedHashAsBytes,
            final HashAlgorithm algorithm) throws Exception {
        // computes hash of the message...
        final var computedHash = computeHash(bytes, algorithm);
        // checks if the recently computed hash is equal to the pre-computed hash...
        final var matched = CollectionUtilities.sequenceEqual(computedHash, preComputedHashAsBytes);

        return matched;
    }

    @Override
    public boolean isMatched(
            final String message,
            final byte[] preComputedHashAsBytes,
            final HashAlgorithm algorithm) throws Exception {
        // computes hash of the message...
        final var computedHash = computeHash(message, algorithm);
        // checks if the recently computed hash is equal to the pre-computed hash...
        final var matched = CollectionUtilities.sequenceEqual(computedHash, preComputedHashAsBytes);

        return matched;
    }

    @Override
    public boolean isMatched(
            final String message,
            final String preComputedHash,
            final HashAlgorithm algorithm,
            final Encoding preComputedHashEncoding) throws Exception {
        // computes hash of the message...
        final var computedHash = computeHash(message, algorithm);
        // decodes the pre-computed hash...
        final var preComputedHashAsBytes = Encoder.decode(preComputedHash, preComputedHashEncoding);
        // checks if the recently computed hash is equal to the pre-computed hash...
        final var matched = CollectionUtilities.sequenceEqual(computedHash, preComputedHashAsBytes);

        return matched;
    }

    private static MessageDigest createMessageDigest(final HashAlgorithm algorithm) throws Exception {
        // creates a new instance of message digest by algorithm name...
        final var messageDigest = MessageDigest.getInstance(algorithm.getName());

        // returns the message digest...
        return messageDigest;
    }
}
