<!DOCTYPE HTML>
<html>
	<body>		
		<table rules="all" style="border-color: #666;" cellpadding="10">
		    <tr>
		        <td><strong>Advisor Name:</strong></td>
		        <td>${ADVISOR_NAME}</td>
		    </tr>
		    <tr>
		        <td><strong>Advisor Email:</strong></td>
		        <td>${ADVISOR_EMAIL}</td>
		    </tr>
		    <tr>
		        <td><strong>Completion Date: </strong></td>
		        <td>${COMPLETED_ON}</td>
		    </tr>
		    <tr>
		        <td><strong>iQuantify Score: </strong></td>
		        <td>${I_QUANTIFY_SCORE}</td>
		    </tr>
		</table>
		
		<br /><br />
		
		<table rules="all" style="border-color: #666;" cellpadding="10">
		    <tr>
		        <th><strong>Questions</strong></td>
		        <th><strong>Responses</strong></td>
		    </tr>
		    ${QUESTIONS_ANSWERS}
		</table>
		
		<br /><br />
		
		<p>Please reach out to ${ADVISOR_NAME} to walk the advisor through his/her iQuantify assessment and to learn more about how CFG can help prepare the advisor's business for the impending DOL legislation.</p>
		
		<p>CONFIDENTIALITY NOTICE: The information in this message and the files transmitted with it is confidential, may be subject to a legal privilege and is intended only for the use of the named recipients. If you are not the intended recipient, please advise the sender immediately by reply e-mail and delete this message and any attachments without retaining a copy or disseminating this message. Please be aware that the use of any confidential or personal information may be restricted by state and federal privacy laws.</p>
	</body>
</html>