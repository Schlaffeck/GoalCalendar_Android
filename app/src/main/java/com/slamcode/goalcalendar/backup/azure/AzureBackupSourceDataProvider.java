package com.slamcode.goalcalendar.backup.azure;

import com.slamcode.goalcalendar.authentication.AuthenticationProvider;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;
import com.slamcode.goalcalendar.backup.BackupRestorer;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupWriter;

import java.util.Locale;

public class AzureBackupSourceDataProvider implements BackupSourceDataProvider {

    public static final String SOURCE_TYPE = "AZURE_STORAGE";
    public static final String SOURCE_DISPLAY_TYPE = "Web";
    // TODO: Move to resources or provide specific exception type
    private static final String SIGN_IN_REQUIRED_MESSAGE = "Requires sign in";
    private final AuthenticationProvider authenticationProvider;
    private final AzureBackupWriterRestorer azureBackupWriterRestorer;

    public AzureBackupSourceDataProvider(AuthenticationProvider authenticationProvider, AzureBackupWriterRestorer azureBackupWriterRestorer)
    {
        this.authenticationProvider = authenticationProvider;
        this.azureBackupWriterRestorer = azureBackupWriterRestorer;
    }

    @Override
    public String getSourceType() {
        return SOURCE_TYPE;
    }

    @Override
    public String getUserSourceIdentifier() {
        AuthenticationResult authenticationResult = this.validateAuthentication();

        return String.format("%s_%s", authenticationResult.getUserId(), authenticationResult.getAuthenticationProviderId());
    }

    @Override
    public SourceDisplayData getDisplayData(Locale locale) {
        if(this.isAuthenticated())
            return new SourceDisplayData(SOURCE_DISPLAY_TYPE);

        return new SourceDisplayData(SOURCE_DISPLAY_TYPE, false, SIGN_IN_REQUIRED_MESSAGE);
    }

    @Override
    public BackupWriter getBackupWriter() {
        this.validateAuthentication();
        return this.azureBackupWriterRestorer;
    }

    @Override
    public BackupRestorer getBackupRestorer() {
        this.validateAuthentication();
        return this.azureBackupWriterRestorer;
    }

    private boolean isAuthenticated()
    {
        AuthenticationResult authenticationResult = this.authenticationProvider.getCurrentAuthenticationData();
        return authenticationResult != null
            && authenticationResult.isSignedIn()
            && authenticationResult.isOauthAvailable();
    }

    private AuthenticationResult validateAuthentication()
    {
        if(!this.isAuthenticated())
            throw new UnsupportedOperationException();

        return this.authenticationProvider.getCurrentAuthenticationData();
    }
}
