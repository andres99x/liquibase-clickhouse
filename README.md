Forked from [liquibase-clickhouse](https://github.com/MEDIARITHMICS/liquibase-clickhouse) to enable support of clickhouse cluster.

The main difference from the original version is the ability to work with Clickhouse in a cluster mode
using zookeeper for storing information about migrations.

Maven dependency:

```
<dependency>
    <groupId>com.genestack</groupId>
    <artifactId>liquibase-clickhouse</artifactId>
    <version>Latest version</version>
</dependency>
```

The cluster mode can be activated by adding the **_liquibaseClickhouse.conf_** file
to the classpath (liquibase/lib/). The file name can be changed by setting the system property
`liquibase.clickhouse.configfile`. The file should contain the following properties:
```
cluster {
    clusterName="{cluster}"
    tableZooKeeperPathPrefix="/liquibase"
}
```
where `clusterName` is the name of the ClickHouse cluster where the migrations tables will be stored,
and `tableZooKeeperPathPrefix` is the prefix of the path in zookeeper where the migrations tables will
be stored.

To use this plugin in cluster mode, you need to enable the `KeeperMap` table engine
by adding the following line to the clickhouse-server configuration file:
```xml
<clickhouse>
  ...
  <keeper_map_path_prefix>
    /keeper/path/to/liquibase
  </keeper_map_path_prefix>
</clickhouse>
```

These example configuration will configure the plugin to create two
paths in your zookeeper:
- `/keeper/path/to/liquibase/liquibase/DATABASECHANGELOG`
- `/keeper/path/to/liquibase/liquibase/DATABASECHANGELOGLOCK`
<hr/>

###### Important changes
 - Since version 0.8.1 the plugin uses Zookeeper for storing information about migrations.
 - The version 0.8.0 is not back-compatible with previous versions.
 - From the version 0.8.0 the extension adapted for the liquibase v4.26, Java baseline changed to 17, clickhouse baseline is
24.1.6. Changed synchronization to more adapted one for cluster environment.

 - From the version 0.7.0 the liquibase-clickhouse supports replication on a cluster. Liquibase v4.6.1.

 - From the version 0.6.0 the extension adapted for the liquibase v4.3.5.


### Uploading to maven central:

1) Make sure that token for upload sucessfully configured in `settings.xml` file:

  ```shell
  └──╼ $ cat ~/.m2/settings.xml                                                                                                                                             <aws:091468197733>
  <settings>
      <servers>
          <server>
            <id>central</id>
            <username>token-key</username>
            <password>token-secret-key</password>
          </server>
      </servers>
  </settings>
  ```

2) Import public and private gpg key locally (located in keybase)

  ```shell
  gpg --import genestack-secret.pgp
  gpg --import genestack-public.pgp
  ```

3) OPTIONAL! Make sure it's public key is published.

  ```shell
  gpg --list-keys
  gpg --keyserver keyserver.ubuntu.com --search-key FINGERPRINT_FROM_FIRST_COMMAND
  ```

4) Finally publish the package

  ```shell
    MAVEN_GPG_PASSPHRASE='passphrase' ./mvnw clean deploy
  ```
