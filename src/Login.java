import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * <h1>Login</h1> Login window presented on application start. Takes a usename
 * and password. Will eventually let user choose which lab database to access.
 * <p>
 * <b>Note:</b> No username or password functionality atm, will be implemented
 * later.
 *
 * @author John Aldo Lamberti
 * @version 1.0
 * @since 03-01-2018
 */
public class Login extends Stage {

	/**
	 * Creates a Login window.
	 * <p>
	 */
	public Login() {
		setTitle("Login");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("Welcome");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label userName = new Label("User Name:");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label pw = new Label("Password:");
		grid.add(pw, 0, 2);

		PasswordField pwBox = new PasswordField();
		grid.add(pwBox, 1, 2);

		Button btn = new Button("Sign in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);

		final Text actiontarget = new Text();
		grid.add(actiontarget, 1, 6);

		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Sign in button pressed");
			}
		});

		Scene scene = new Scene(grid);

		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		setX(bounds.getWidth() * (1.0 / 3));
		setY(bounds.getHeight() * (1.0 / 3));
		setWidth(bounds.getWidth() * (1.0 / 3));
		setHeight(bounds.getHeight() * (1.0 / 3));

		setScene(scene);
		setAlwaysOnTop(true);
		setResizable(false);
		show();
	}
}