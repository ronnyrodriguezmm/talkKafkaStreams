package com.vass.talks.kafka.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationStats {
    private static final Logger logger = LoggerFactory.getLogger(LocationStats.class);
    private String name;
    private long count;
    private double sum;

    public LocationStats name(String name) {
        this.name = name;
        return this;
    }
    public LocationStats accumulate(long count, double value) {
        this.count+=count;
        this.sum+=value;
        logger.info("accumulate {}",this);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LocationStats{");
        sb.append("name='").append(name).append('\'');
        sb.append(", count=").append(count);
        sb.append(", sum=").append(sum);
        sb.append('}');
        return sb.toString();
    }
}
