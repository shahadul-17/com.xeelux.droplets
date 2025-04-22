package com.xeelux.droplets.core.utilities;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public final class CollectionUtilities {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static byte[] getEmptyByteArray() {
        return EMPTY_BYTE_ARRAY;
    }

    public static boolean sequenceEqual(final byte[] arrayA, final byte[] arrayB) {
        // if both the arrays are not of the same size, we shall return false...
        if (arrayA.length != arrayB.length) { return false; }

        // iterates over both the arrays...
        for (var i = 0; i < arrayA.length; i++) {
            // if both the values of same position don't match,
            // we shall return false...
            if (arrayA[i] != arrayB[i]) { return false; }
        }

        // otherwise, both the sequences matched
        // and we shall return true...
        return true;
    }

    public static <Type> boolean sequenceEqual(final Type[] arrayA, final Type[] arrayB) {
        // if both the arrays are not of the same size, we shall return false...
        if (arrayA.length != arrayB.length) { return false; }

        // iterates over both the arrays...
        for (var i = 0; i < arrayA.length; i++) {
            // if both the values of the same position don't match,
            // we shall return false...
            if (!arrayA[i].equals(arrayB[i])) { return false; }
        }

        // otherwise, both the sequences matched
        // and we shall return true...
        return true;
    }

    /**
     * Splits a given array of items into blocks/chunks.
     * @implNote 1. If the given array of items is null or empty,
     * this method returns an empty list as blocks.
     * 2. If the given number of items per block is less than or equal
     * to zero (0) or the items per block is greater than the
     * total number of items in the given array, we shall set the
     * total number of items as the item count per block.
     * @param items Array of items to split.
     * @param itemCountPerBlock The number of items each block/chunk shall contain.
     * @return Returns the blocks/chunks as a list of lists.
     * @param <Type> Type of the items contained by the given array.
     */
    public static <Type> List<List<Type>> split(
            final Type[] items,
            final int itemCountPerBlock) {
        // if the list is null or empty...
        final List<Type> itemList = items == null || items.length == 0
                ? new ArrayList<>(0)        // <-- we shall create an empty list...
                : Arrays.asList(items);                 // <-- otherwise, we shall create a list containing all the items in the array...

        // calling the split method that takes a list of items and item count per block...
        return split(itemList, itemCountPerBlock);
    }

    /**
     * Splits a given list of items into blocks/chunks.
     * @implNote 1. If the given list of items is null or empty,
     * this method returns an empty list as blocks.
     * 2. If the given number of items per block is less than or equal
     * to zero (0) or the items per block is greater than the
     * total number of items in the given list, we shall set the
     * total number of items as the item count per block.
     * @param items List of items to split.
     * @param itemCountPerBlock The number of items each block/chunk shall contain.
     * @return Returns the blocks/chunks as a list of lists.
     * @param <Type> Type of the items contained by the given list.
     */
    public static <Type> List<List<Type>> split(
            final List<Type> items,
            int itemCountPerBlock) {
        // if the list is null or empty...
        if (items == null || items.isEmpty()) {
            // we shall return empty blocks...
            return new ArrayList<>(0);
        }

        // storing the item count...
        final var itemCount = items.size();

        // if item count per block is less than or equal to zero (0) or,
        // greater than the total item count...
        if (itemCountPerBlock < 1 || itemCountPerBlock > itemCount) {
            // we shall assign the total item count as items per block...
            itemCountPerBlock = itemCount;
        }

        // calculating the number of remaining items that are not evenly fit...
        // NOTE: WE SHALL ADD THE REMAINING ITEMS TO OUR FIRST BLOCK...
        final var remainingItemCount = itemCount % itemCountPerBlock;
        // calculating the number of blocks required to hold the items...
        var blockCount = itemCount / itemCountPerBlock;

        // if we have remaining items, we shall add another block...
        if (remainingItemCount > 0) { ++blockCount; }

        // instantiating the blocks...
        final List<List<Type>> blocks = new ArrayList<>(blockCount);
        // this variable iterates over the given list...
        var i = 0;

        // iterates over all the blocks...
        for (var j = 0; j < blockCount; ++j) {
            // calculating the block size...
            // final var blockSize = itemCountPerBlock + remainingItemCount;
            // initializing a block of the appropriate type...
            final List<Type> block = new ArrayList<>(itemCountPerBlock);

            // iterates over all the elements of the block...
            for (var k = 0; i < itemCount && k < itemCountPerBlock; ++k) {
                // retrieves the item from the given list...
                final var item = items.get(i);

                // adding the item to the block...
                block.add(item);

                // incrementing the iteration variable for the given list of items...
                ++i;
            }

            // assigning the block to the list of blocks...
            blocks.add(block);
        }

        // finally, we shall return the blocks...
        return blocks;
    }

    /**
     * Evenly distributes the elements within the lists and merges them into a single list.
     * @param listOfLists List containing several lists that shall be distributed evenly
     *                    and merged into one single list.
     * @return A list containing all the elements distributed evenly.
     * @param <Type> Type of the items contained by the lists.
     */
    public static <Type> List<Type> mergeAndDistributeEvenly(final List<List<Type>> listOfLists) {
        if (listOfLists == null || listOfLists.isEmpty()) { return Collections.emptyList(); }

        // this holds the total number of elements in all the lists combined...
        var totalElementCount = 0;

        // calculating the total number of elements...
        for (var i = 0; i < listOfLists.size(); ++i) {
            final var items = listOfLists.get(i);

            if (items == null) { continue; }

            totalElementCount += items.size();
        }

        if (totalElementCount == 0) { return Collections.emptyList(); }

        // initializing an array to hold the indices of each list within the list...
        final int[] indices = new int[listOfLists.size()];
        // filling up the entire array with zeros (0)...
        Arrays.fill(indices, 0);

        // initializing a list to hold the elements...
        final List<Type> allElements = new ArrayList<>(totalElementCount);

        // executing a for loop from zero (0) to total element count...
        for (int i = 0, j = -1; i < totalElementCount; ) {
            // incrementing the index (j) of the list...
            ++j;

            // if 'j' is greater than or equal to the size of the list...
            if (j >= listOfLists.size()) {
                // we shall reset the index...
                j = 0;
            }

            final var elements = listOfLists.get(j);

            if (indices[j] >= elements.size()) { continue; }

            final var element = elements.get(indices[j]);

            // adding the element to the list of all elements...
            allElements.add(element);

            // incrementing index...
            ++indices[j];
            // incrementing the loop iteration variable, i...
            ++i;
        }

        // finally, we shall return all the elements...
        return allElements;
    }

    public static <Type> int indexOf(final Type item, final Type[] items) {
        // iterating over all the items...
        for (var i = 0; i < items.length; ++i) {
            // selecting the current item...
            final var currentItem = items[i];

            // if the current item is null...
            if (currentItem == null) {
                // and the given item is null; it means both are equal.
                // so we shall return the index...
                if (item == null) { return i; }
            }
            // else if the current item is equal to the given item,
            // we shall return the index...
            else if (currentItem.equals(item)) { return i; }
        }

        // otherwise, we shall return -1...
        return -1;
    }

    /**
     * Populates a map by taking key from keys array and value from values array.
     * @param keys An array of keys.
     * @param values An array of values.
     * @return A map containing key-value pairs.
     * @param <KeyType> Type of the keys.
     * @param <ValueType> Type of the values.
     */
    public static <KeyType, ValueType> Map<KeyType, ValueType> populateMap(final KeyType[] keys, final ValueType[] values) {
        if (keys == null || keys.length == 0 || values == null || values.length == 0) { return Collections.emptyMap(); }

        final var length = Math.min(keys.length, values.length);
        final Map<KeyType, ValueType> keyValuePairs = new HashMap<>(length * 2);

        for (var i = 0; i < length; ++i) {
            final var key = keys[i];
            final var value = values[i];

            keyValuePairs.put(key, value);
        }

        return keyValuePairs;
    }
}
