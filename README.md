# maven-server-credentials-exporter

`maven-server-credentials-exporter` is a tiny Maven plugin that republishes the credentials from a `<server>` entry in your `settings.xml` as Maven system properties. Once exported, those properties are available to any other plugin running in the same build (for example `exec-maven-plugin`, `wagon-maven-plugin`, or custom extensions) without having to duplicate credentials or leak them into the project POM.

## Why this plugin?

* **Reuse trusted credentials** – Build agents such as Jenkins already inject server credentials into `settings.xml`. The plugin lets you forward those credentials to tools that cannot read the Maven settings file directly.
* **Avoid duplicate secrets** – By reading the credentials Maven has already decrypted, you keep passwords out of your POMs, profiles, or environment variables.
* **Interoperate with CLI tooling** – Exported properties can be referenced by shell commands (e.g. `curl`) or other plugins, keeping automation scripts simple and secure.

## Example Usage

```xml
<plugin>
  <groupId>se.moshicon</groupId>
  <artifactId>maven-server-credentials-exporter</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <id>export-creds</id>
      <phase>initialize</phase>
      <goals><goal>export</goal></goals>
      <configuration>
        <serverId>nexus-releases</serverId>
        <usernameProperty>nexus.user</usernameProperty>
        <passwordProperty>nexus.pass</passwordProperty>
      </configuration>
    </execution>
  </executions>
</plugin>

<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.5.0</version>
  <executions>
    <execution>
      <id>call-api</id>
      <phase>deploy</phase>
      <goals><goal>exec</goal></goals>
      <configuration>
        <executable>curl</executable>
        <arguments>
          <argument>-u</argument>
          <argument>${nexus.user}:${nexus.pass}</argument>
          <argument>https://nexus.example.com/api/.../</argument>
        </arguments>
      </configuration>
    </execution>
  </executions>
</plugin>
```

## Tips

* Use descriptive property names (e.g. `nexus.user`) to avoid collisions with other plugins.
* Pair this plugin with Jenkins Configuration-as-Code `serverCredentialMappings` to bridge Jenkins secrets with arbitrary build steps.
* Because only property **names** are logged, secrets remain hidden from build output while still being usable by other tooling.
