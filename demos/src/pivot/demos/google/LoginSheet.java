package pivot.demos.google;

import java.io.IOException;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.util.AuthenticationException;

import pivot.serialization.SerializationException;
import pivot.util.concurrent.Task;
import pivot.util.concurrent.TaskExecutionException;
import pivot.util.concurrent.TaskListener;
import pivot.wtk.ApplicationContext;
import pivot.wtk.Button;
import pivot.wtk.ButtonPressListener;
import pivot.wtk.Component;
import pivot.wtk.Form;
import pivot.wtk.Label;
import pivot.wtk.MessageType;
import pivot.wtk.PushButton;
import pivot.wtk.Sheet;
import pivot.wtk.SheetCloseListener;
import pivot.wtk.TextInput;
import pivot.wtk.Window;
import pivot.wtkx.WTKXSerializer;

/**
 * Login sheet.
 *
 * @author gbrown
 */
public class LoginSheet extends Sheet {
	/**
	 * Task that logs into contacts service asynchronously.
	 *
	 * @author gbrown
	 */
	private class LoginTask extends Task<Void> {
		private String username;
		private String password;

		public LoginTask(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public Void execute() throws TaskExecutionException {
			try {
				contactsService.setUserCredentials(username, password);
			} catch(AuthenticationException exception) {
				throw new TaskExecutionException(exception);
			}

			return null;
		}
	}

	private ContactsService contactsService = null;
	private boolean authenticated = false;

	private Form userCredentialsForm;
	private TextInput usernameTextInput;
	private TextInput passwordTextInput;
	private Label errorMessageLabel;
	private PushButton okButton;
	private PushButton cancelButton;

	public LoginSheet(ContactsService contactsService)
		throws IOException, SerializationException {
		this.contactsService = contactsService;

		WTKXSerializer wtkxSerializer = new WTKXSerializer();
		Component content = (Component)wtkxSerializer.readObject(getClass().getResource("login.wtkx"));
		setContent(content);

		userCredentialsForm = (Form)wtkxSerializer.getObjectByName("userCredentialsForm");
		usernameTextInput = (TextInput)wtkxSerializer.getObjectByName("usernameTextInput");
		passwordTextInput = (TextInput)wtkxSerializer.getObjectByName("passwordTextInput");
		errorMessageLabel = (Label)wtkxSerializer.getObjectByName("errorMessageLabel");
		okButton = (PushButton)wtkxSerializer.getObjectByName("okButton");
		okButton.getButtonPressListeners().add(new ButtonPressListener() {
			public void buttonPressed(Button button) {
				close(true);
			}
		});

		cancelButton = (PushButton)wtkxSerializer.getObjectByName("cancelButton");
		cancelButton.getButtonPressListeners().add(new ButtonPressListener() {
			public void buttonPressed(Button button) {
				close(false);
			}
		});
	}

	@Override
	public void open(Window owner, SheetCloseListener sheetCloseListener) {
		super.open(owner, sheetCloseListener);

		ApplicationContext.queueCallback(new Runnable() {
			public void run() {
				usernameTextInput.requestFocus();
			}
		});
	}

	@Override
	public void close(boolean result) {
		if (result
			&& !authenticated) {
			// Ensure that the user has provided values for username and password;
			// validate in reverse order so the top-most form element gets the focus
			String password = passwordTextInput.getText();
			if (password.length() == 0) {
				Form.setFlag(passwordTextInput, new Form.Flag(MessageType.ERROR,
					"Password is required."));
				passwordTextInput.requestFocus();
			} else {
				Form.setFlag(passwordTextInput, (Form.Flag)null);
			}

			String username = usernameTextInput.getText();
			if (username.length() == 0) {
				Form.setFlag(usernameTextInput, new Form.Flag(MessageType.ERROR,
					"Username is required."));
				usernameTextInput.requestFocus();
			} else {
				Form.setFlag(usernameTextInput, (Form.Flag)null);
			}

			if (userCredentialsForm.getFlaggedFieldCount(null) == 0) {
				// The user provided a username and password; attempt to log
				// into the contacts service using the asynchronous login task
				errorMessageLabel.setText(null);
				enableForm(false);

				LoginTask loginTask = new LoginTask(username, password);
				loginTask.execute(new TaskListener<Void>() {
					public void taskExecuted(Task<Void> task) {
						// The user successfully logged in
						authenticated = true;
						enableForm(true);
						errorMessageLabel.setText(null);
						close(true);
					}

					public void executeFailed(Task<Void> task) {
						// The provided credentials were invalid
						errorMessageLabel.setText("Invalid username or password.");
						enableForm(true);
						usernameTextInput.requestFocus();
					}
				});
			} else {
				// Prompt the user to provide the missing information
				errorMessageLabel.setText("Some required information is missing.");
				ApplicationContext.beep();
			}
		} else {
			super.close(result);
		}
	}

	public String getUsername() {
		return usernameTextInput.getText();
	}

	private void enableForm(boolean enabled) {
		usernameTextInput.setEnabled(enabled);
		passwordTextInput.setEnabled(enabled);
		okButton.setEnabled(enabled);
		cancelButton.setEnabled(enabled);
	}
}
