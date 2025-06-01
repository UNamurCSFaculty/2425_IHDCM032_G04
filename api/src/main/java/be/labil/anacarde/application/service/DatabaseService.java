package be.labil.anacarde.application.service;

import java.io.IOException;

public interface DatabaseService {

	boolean isInitialized();
	void dropDatabase() throws IOException;
	void createDatabase() throws IOException;
}
