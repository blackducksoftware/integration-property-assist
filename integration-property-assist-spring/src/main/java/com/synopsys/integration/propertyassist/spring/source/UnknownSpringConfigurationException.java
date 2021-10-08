/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.spring.source;

public class UnknownSpringConfigurationException extends Exception {
    public UnknownSpringConfigurationException(String msg) {
        super(msg);
    }
}
