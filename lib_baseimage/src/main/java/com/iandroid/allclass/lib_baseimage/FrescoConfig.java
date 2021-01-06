package com.iandroid.allclass.lib_baseimage;

/**
 * created by wangkm
 * on 2020-7-7
 */
public class FrescoConfig {
    private String localDir;

    public FrescoConfig(Builder builder) {
        this.localDir = builder.localDir;
    }

    public String getLocalDir() {
        return localDir;
    }

    public static final class Builder {
        private String localDir;


        public Builder() {
        }

        public Builder localDir(String localDir) {
            this.localDir = localDir;
            return this;
        }

        public FrescoConfig build() {
            return new FrescoConfig(this);
        }

    }
}
