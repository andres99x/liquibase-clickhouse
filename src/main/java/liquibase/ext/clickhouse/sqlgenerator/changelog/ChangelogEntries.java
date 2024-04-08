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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import liquibase.database.Database;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;

public final class ChangelogEntries {
    private ChangelogEntries() {
    }

    public static Sql updateStatement(
        Database database,
        String idSelector,
        Consumer<EnumMap<ChangelogColumns, String>> replacementCallback
    ) {
        var replacements = new EnumMap<ChangelogColumns, String>(ChangelogColumns.class);
        replacementCallback.accept(replacements);
        String selectedColumns =
            Arrays.stream(ChangelogColumns.values())
                .map(it -> replacements.containsKey(it) ? replacements.get(it) : it.name())
                .collect(Collectors.joining(", "));
        String query =
            String.format(
                "INSERT INTO %s.%s SELECT %s from %s.%s final where ID = %s limit 1;",
                database.getLiquibaseCatalogName(),
                database.getDatabaseChangeLogTableName(),
                selectedColumns,
                database.getLiquibaseCatalogName(),
                database.getDatabaseChangeLogTableName(),
                idSelector
            );
        return new UnparsedSql(query);
    }
}
