package se.moshicon.credentials;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

import java.util.Objects;
import java.util.Properties;

/**
 * Mojo that exposes credentials from a configured Maven server entry as system properties.
 */
@Mojo(name = "export", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class ServerCredentialsMojo extends AbstractMojo {

    @Parameter(defaultValue = "${settings}", readonly = true, required = true)
    private Settings settings;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(property = "serverId", required = true)
    private String serverId;

    @Parameter(property = "usernameProperty", defaultValue = "server.username")
    private String usernameProperty;

    @Parameter(property = "passwordProperty", defaultValue = "server.password")
    private String passwordProperty;

    @Override
    public void execute() throws MojoExecutionException {
        Objects.requireNonNull(settings, "settings must not be null");
        Objects.requireNonNull(session, "session must not be null");

        Server server = settings.getServer(serverId);
        if (server == null) {
            throw new MojoExecutionException("No server configuration found for id '" + serverId + "'.");
        }

        String username = server.getUsername();
        String password = server.getPassword();

        if (username == null) {
            throw new MojoExecutionException("Server '" + serverId + "' does not define a username.");
        }
        if (password == null) {
            throw new MojoExecutionException("Server '" + serverId + "' does not define a password.");
        }

        Properties systemProperties = session.getSystemProperties();
        systemProperties.setProperty(usernameProperty, username);
        systemProperties.setProperty(passwordProperty, password);

        getLog().info("Exported credentials for server '" + serverId + "' to properties '" + usernameProperty
                + "' and '" + passwordProperty + "'.");
    }
}
