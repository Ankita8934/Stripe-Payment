package com.stripe.integration.dto;

import java.util.Objects;

public final class SubscriptionCancelRecord {
    private final String status;

    public SubscriptionCancelRecord(String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SubscriptionCancelRecord) obj;
        return Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "SubscriptionCancelRecord[" +
                "status=" + status + ']';
    }

}
