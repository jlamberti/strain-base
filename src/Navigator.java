import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * <h1>Navigator</h1> The list database view of the application. Allows for
 * viewing of - Search Results - Full Records - Lab Members Search results can
 * be found here and opened by a double or right mouse click.
 * <p>
 * <b>Note:</b> Will eventually add dynamic right clicking based on class of
 * tree item selected.
 *
 * @author John Aldo Lamberti
 * @version 1.0
 * @since 03-01-2018
 */
public class Navigator extends TreeView<String> {

	/**
	 * Creates a Navigator (TreeView) with a given TabView where StrainTabs can be
	 * sent.
	 * <p>
	 * 
	 * @param tabs
	 *            TabView where StrainTabs can be sent.
	 * @see TabView
	 * @see StrainTab
	 */
	@SuppressWarnings("unchecked")
	public Navigator(TabView tabs) {
		super(new TreeItem<String>("root"));

		// Second layer nodes: visible roots
		TreeItem<String> searchResults = new TreeItem<String>("Search Results");
		TreeItem<String> fullRecords = new TreeItem<String>("Full Records");
		TreeItem<String> labMembers = new TreeItem<String>("Lab Members");
		getRoot().getChildren().addAll(searchResults, fullRecords, labMembers);

		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:full_records.db");

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			ResultSet strains = statement.executeQuery("SELECT * FROM entry");
			ResultSetMetaData rsmd = strains.getMetaData();

			while (strains.next()) {
				StrainTreeItem temp = new StrainTreeItem(strains.getString(1));
				int r = 1;
				while (r++ < rsmd.getColumnCount()) {
					String columnData = strains.getString(r);
					if (columnData != null)
						if (!columnData.isEmpty())
							temp.getChildren().add(new TreeItem<String>(rsmd.getColumnName(r) + ": " + columnData));
				}
				fullRecords.getChildren().add(temp);
			}

			ResultSet uniqueLab = statement.executeQuery("SELECT DISTINCT strain_created_by FROM entry");
			while (uniqueLab.next()) {
				TreeItem<String> member = new TreeItem<String>(uniqueLab.getString(1));

				Statement statement2 = connection.createStatement();
				statement2.setQueryTimeout(30); // set timeout to 30 sec.
				String test = "SELECT * FROM entry WHERE strain_created_by = '" + uniqueLab.getString(1) + "'";
				ResultSet memberInfo = statement2.executeQuery(test);
				ResultSetMetaData rsmd2 = memberInfo.getMetaData();
				while (memberInfo.next()) {
					StrainTreeItem currStrain = new StrainTreeItem(memberInfo.getString(1));
					int r = 1;
					while (r++ < rsmd2.getColumnCount()) {
						String columnData = memberInfo.getString(r);
						if (columnData != null)
							if (!columnData.isEmpty())
								currStrain.getChildren()
										.add(new TreeItem<String>(rsmd2.getColumnName(r) + ": " + columnData));
						// && !rsmd2.getColumnLabel(r).equals("strain_created_by")
					}
					member.getChildren().add(currStrain);
				}
				labMembers.getChildren().add(member);
			}
		}

		catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) { // Use SQLException class instead.
				System.err.println(e);
			}
		}

		SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();

		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 2) {
						Node node = mouseEvent.getPickResult().getIntersectedNode();
						// Accept clicks only on node cells, and not on empty spaces of the TreeView
						if (node instanceof Text || (node instanceof TreeCell && ((Labeled) node).getText() != null)) {
							String name = (String) getSelectionModel().getSelectedItem().getValue();

							Connection connection = null;
							try {
								// create a database connection
								connection = DriverManager.getConnection("jdbc:sqlite:full_records.db");

								Statement statement = connection.createStatement();
								statement.setQueryTimeout(30); // set timeout to 30 sec.

								ResultSet resultSet = statement
										.executeQuery("SELECT * FROM entry WHERE strain_name = '" + name + "'");

								StrainTab temp = new StrainTab(new Strain(resultSet));
								tabs.getTabs().add(temp);
								selectionModel.select(temp);

							} catch (SQLException e) {
								System.err.println(e.getMessage());
							}
						}
					}
				}
			}
		});

		MenuItem view = new MenuItem("View");
		view.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (getSelectionModel().getSelectedItem() instanceof StrainTreeItem) {
					Connection connection = null;
					try {
						// create a database connection
						connection = DriverManager.getConnection("jdbc:sqlite:full_records.db");

						Statement statement = connection.createStatement();
						statement.setQueryTimeout(30); // set timeout to 30 sec.

						ResultSet resultSet = statement.executeQuery("SELECT * FROM entry WHERE strain_name = '"
								+ getSelectionModel().getSelectedItem().getValue() + "'");

						Strain result = new Strain(resultSet);
						String resultName = result.get(result.getKeys().get(0));
						boolean found = false;
						for (Tab tab : tabs.getTabs()) {
							if (tab.getText().equals(resultName)) {
								found = true;
								((StrainTab) tab).readOnly();
								selectionModel.select(tab);
							}
						}

						if (!found) {
							StrainTab newTab = new StrainTab(result, false);
							tabs.getTabs().add(newTab);
							selectionModel.select(newTab);
						}
					} catch (SQLException e1) {
						System.err.println(e1.getMessage());
					}
				}
			}
		});

		MenuItem edit = new MenuItem("Edit");
		edit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (getSelectionModel().getSelectedItem() instanceof StrainTreeItem) {
					Connection connection = null;
					try {
						// create a database connection
						connection = DriverManager.getConnection("jdbc:sqlite:full_records.db");

						Statement statement = connection.createStatement();
						statement.setQueryTimeout(30); // set timeout to 30 sec.

						ResultSet resultSet = statement.executeQuery("SELECT * FROM entry WHERE strain_name = '"
								+ getSelectionModel().getSelectedItem().getValue() + "'");

						Strain result = new Strain(resultSet);
						String resultName = result.get(result.getKeys().get(0));
						boolean found = false;
						for (Tab tab : tabs.getTabs()) {
							if (tab.getText().equals(resultName)) {
								found = true;
								((StrainTab) tab).writable();
								selectionModel.select(tab);
							}
						}

						if (!found) {
							StrainTab newTab = new StrainTab(result, true);
							tabs.getTabs().add(newTab);
							selectionModel.select(newTab);
						}

					} catch (SQLException e1) {
						System.err.println(e1.getMessage());
					}
				}
			}
		});

		final ContextMenu rootContextMenu = new ContextMenu();
		rootContextMenu.getItems().addAll(view, edit);
		setContextMenu(rootContextMenu);
	}
}