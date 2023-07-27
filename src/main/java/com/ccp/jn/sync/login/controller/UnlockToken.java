package com.ccp.jn.sync.login.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.jn.sync.common.business.ResetTable;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.ccp.process.CcpProcess;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnBusinessEntity;

public class UnlockToken {
	
	@CcpDependencyInject
	private CcpPasswordHandler passwordHandler;

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	
	private CcpProcess decisionTree = values ->{
		
		return new ValidatePassword(this.passwordHandler, JnBusinessEntity.request_unlock_token_answered)
				.addStep(200, new ResetTable(this.mensageriaSender,"tries", 3, JnBusinessEntity.unlock_token_tries)
						.addStep(200, new TransferDataBetweenTables(JnBusinessEntity.locked_token, JnBusinessEntity.unlocked_token)
							)
						)
				.addStep(401, new EvaluateTries(JnBusinessEntity.unlock_token_tries, 401, 429)
						.addStep(429, JnBusinessEntity.locked_password.getSaver(429))
				)
				.goToTheNextStep(values).values;
		
	};

	@CcpDependencyInject
	private CcpDbCrud crud;
	
	public void execute (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));
		this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.login_token).returnStatus(404).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.locked_token).returnStatus(422).and()
			.ifThisIdIsNotPresentInTable(JnBusinessEntity.request_unlock_token).returnStatus(420).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.failed_unlock_token).returnStatus(403).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.request_unlock_token_answered).executeAction(this.decisionTree).andFinally()
		.endThisProcedure()
		;

		
	}
}
