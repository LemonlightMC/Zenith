package com.lemonlightmc.zenith.v2;

import java.sql.SQLException;
import java.util.function.Function;

public interface TransactionCallback extends Function<DbStatement, Boolean> {
  @Override
  default Boolean apply(DbStatement dbStatement) {
    try {
      return this.runTransaction(dbStatement);
    } catch (Exception e) {
      return false;
    }
  }

  Boolean runTransaction(DbStatement stm) throws SQLException;
}
