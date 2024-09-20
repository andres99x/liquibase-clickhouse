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
package liquibase.ext.clickhouse.sqlgenerator;

import liquibase.ext.clickhouse.params.ClusterConfig;
import liquibase.ext.clickhouse.params.LiquibaseClickHouseConfig;
import liquibase.ext.clickhouse.params.LiquibaseConfigVisitor;
import liquibase.ext.clickhouse.params.StandaloneConfig;

public abstract class LiquibaseSqlTemplate<T> implements LiquibaseConfigVisitor<T> {

    @Override
    public T visit(ClusterConfig object) {
        return visitDefault(object);
    }

    @Override
    public T visit(StandaloneConfig object) {
        return visitDefault(object);
    }

    public T visitDefault(LiquibaseClickHouseConfig config) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
