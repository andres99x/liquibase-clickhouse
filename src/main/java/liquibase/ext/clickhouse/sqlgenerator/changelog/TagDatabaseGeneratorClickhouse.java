/*-
 * #%L
 * Liquibase extension for ClickHouse
 * %%
 * Copyright (C) 2020 - 2024 Genestack LTD
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package liquibase.ext.clickhouse.sqlgenerator.changelog;

import liquibase.ext.clickhouse.database.ClickHouseDatabase;

import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.TagDatabaseGenerator;
import liquibase.statement.core.TagDatabaseStatement;

public class TagDatabaseGeneratorClickhouse extends TagDatabaseGenerator {
  @Override
  public int getPriority() {
    return PRIORITY_DATABASE;
  }

  @Override
  public boolean supports(TagDatabaseStatement statement, Database database) {
    return database instanceof ClickHouseDatabase;
  }

  @Override
  public Sql[] generateSql(
      TagDatabaseStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
    String tagEscaped =
        DataTypeFactory.getInstance()
            .fromObject(statement.getTag(), database)
            .objectToSql(statement.getTag(), database);

    var maxIDSubQuery =
        String.format(
            "(SELECT ID FROM %s.%s FINAL LIMIT 1)",
            database.getLiquibaseCatalogName(),
            database.getDatabaseChangeLogTableName());
    return new Sql[] {
      ChangelogEntries.updateStatement(
          database, maxIDSubQuery, it -> it.put(ChangelogColumns.TAG, tagEscaped))
    };
  }
}
