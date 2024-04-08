Forked from [liquibase-clickhouse](https://github.com/MEDIARITHMICS/liquibase-clickhouse) to enable support of clickhouse cluster.

Maven dependency:

```
<dependency>
    <groupId>com.genestack</groupId>
    <artifactId>liquibase-clickhouse</artifactId>
    <version>Latest version</version>
</dependency>
```

The cluster mode can be activated by adding the **_liquibaseClickhouse.conf_** file to the classpath (liquibase/lib/).```
cluster {
    clusterName="{cluster}"
    tableZooKeeperPathPrefix="/clickhouse/tables/{database}/"
    tableReplicaName="s{shard}r{replica}"
}

The difference from the original mediarithmics dependency in abscence of {shard} placeholder which allows Clickhouse
Keeper to treat both liquibase tables as replicas for entire cluster.

In this mode, liquibase will create its own tables as replicated.<br/>
All changes in these files will be replicated on the entire cluster.<br/>
Your updates should also affect the entire cluster either by using ON CLUSTER clause, or by using replicated tables.

<hr/>

###### Important changes
 - The version 0.8.0 is not back-compatible with previous versions.
 - From the version 0.8.0 the extension adapted for the liquibase v4.26, Java baseline changed to 17, clickhouse baseline is
24.1.6. Changed synchronization to more adapted one for cluster environment.

 - From the version 0.7.0 the liquibase-clickhouse supports replication on a cluster. Liquibase v4.6.1.

 - From the version 0.6.0 the extension adapted for the liquibase v4.3.5.
