package eu.visceral.registration.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */

public class PasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        // Get the first password from the attribute.
        UIInput passwordComponent = (UIInput) component.getAttributes().get("password");

        // Get the value from the UIInput component.
        String password = (String) passwordComponent.getValue();

        // Get the value entered in the second string from the component
        // parameter passed to the method.
        String confirm = (String) value;

        // Compare both fields.
        if (!password.equals(confirm)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords dont match!!!", null));
        }
    }

}
