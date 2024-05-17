package com.ccp.jn.sync.status.login;

public enum StatusExistsLoginEmail implements StatusEndpointsLogin{
	invalidEmail(400),
	lockedToken(403),
	missingEmail(404),
	lockedPassword(421),
	loginConflict(409),
	missingPassword(202),
	missingAnswers(201),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private StatusExistsLoginEmail(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
