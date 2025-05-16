package be.labil.anacarde.application.service;

import java.io.IOException;

public interface DatabaseService {

	void dropDatabase();
	void createDatabase() throws IOException;
}
