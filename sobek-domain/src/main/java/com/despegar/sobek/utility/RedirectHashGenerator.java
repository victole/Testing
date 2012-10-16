package com.despegar.sobek.utility;

import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;

import com.despegar.framework.utils.string.StringUtils;
import com.google.common.base.Joiner;

public class RedirectHashGenerator {
    private String privateKey;

    public String getHashRedirection(String language, Set<Long> destination, String fullName, String checkIn, String checkOut) {

        String result = "";
        String checkInDate;
        String checkOutDate;
        String name;

        this.validateLanguageAndDestinationNotNullOrEmpty(language, destination);

        checkInDate = (checkIn == null) ? "" : checkIn;
        checkOutDate = (checkOut == null) ? "" : checkOut;
        name = (fullName == null) ? "" : fullName;

        if (destination.size() == 1) {
            result = this.getHashForOneDestination(language, destination.iterator().next(), name, checkInDate, checkOutDate);
        } else {
            result = this.getHashForMultipleDestination(language, destination, name);
        }
        return result;
    }

    private String getHashForOneDestination(String language, Long destination, String fullName, String checkIn,
        String checkOut) {
        return this.getFormattedHash(language, destination, fullName, checkIn, checkOut, this.privateKey);
    }

    private String getHashForMultipleDestination(String language, Set<Long> destinationIds, String fullName) {
        String destination = Joiner.on("").join(destinationIds);
        return this.getFormattedHash(language, destination, fullName, this.privateKey);
    }

    private String getFormattedHash(Object... words) {
        byte[] bytes = StringUtils.concat(words).getBytes();
        return new String(Hex.encodeHex(DigestUtils.md5(bytes)));
    }

    private void validateLanguageAndDestinationNotNullOrEmpty(String language, Set<Long> destination) {
        Assert.notNull(language, "Argument language cannot be null");
        Assert.notNull(destination, "Argument destination cannot be null");
        Assert.hasText(language, "Argument language cannot be empty");
        Assert.notEmpty(destination, "Argument destination cannot be null nor empty");
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
