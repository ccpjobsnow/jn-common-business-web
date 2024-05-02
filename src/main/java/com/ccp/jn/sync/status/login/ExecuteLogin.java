package com.ccp.jn.sync.status.login;

public enum ExecuteLogin implements EndpointsLogin{
	invalidEmail(400),
	lockedToken(403),
	missingEmail(404),
	lockedPassword(423),
	wrongPassword(401),
	passwordLockedRecently(429),
	loginConflict(409),
	missingPassword(202),
	expectedStatus(200),
	;

	public final int status;
	
	
	
	private ExecuteLogin(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
