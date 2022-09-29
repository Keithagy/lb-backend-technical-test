package com.backendtestka.helpers;

import java.sql.SQLException;

public class SQLResourceNotFoundException extends SQLException {
    public SQLResourceNotFoundException(String reason) {
        super(reason);
    }
}

