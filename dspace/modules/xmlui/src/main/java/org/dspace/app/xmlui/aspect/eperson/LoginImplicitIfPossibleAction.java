package org.dspace.app.xmlui.aspect.eperson;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.sitemap.PatternException;
import org.dspace.app.xmlui.utils.AuthenticationUtil;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;

public class LoginImplicitIfPossibleAction extends AbstractAction {
	
	public Map act(Redirector redirector, SourceResolver resolver,	Map objectModel, String source, Parameters parameters)
			throws Exception {
		
		
		
		Request request = ObjectModelHelper.getRequest(objectModel);

		// The user has successfully logged in
		String redirectURL = request.getContextPath();
		
		try {
		
			
			Context context = ContextUtil.obtainContext(objectModel);
		
			EPerson eperson = context.getCurrentUser();

			if (null == eperson){
				// Try to implicit login
				context = AuthenticationUtil.authenticateImplicit(objectModel);
				eperson = context.getCurrentUser();
				// implicit login success 
				if (eperson == null){
					redirectURL+="/login";
					final HttpServletResponse httpResponse = (HttpServletResponse) objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT);
					httpResponse.sendRedirect(redirectURL);
					context.setCurrentUser(null);
				}
			}
			return new HashMap();
		} catch (Exception ex) {
			throw new PatternException("Unable to preform authentication", ex);
		}


	}

}
