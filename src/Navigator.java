import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ContextMenuBuilder;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class Navigator extends TreeView {
	
	public Navigator(TreeItem ti, TabView tabs) {
		super(ti);
		SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
		
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 2) {
						Node node = mouseEvent.getPickResult().getIntersectedNode();
						// Accept clicks only on node cells, and not on empty spaces of the TreeView
						if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
							String name = (String) ((TreeItem) getSelectionModel().getSelectedItem())
									.getValue();

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
				} else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
					// System.out.println("egg");
				}
			}

		});

		@SuppressWarnings("deprecation")
		ContextMenu rootContextMenu = ContextMenuBuilder.create()
				.items(MenuItemBuilder.create().text("View").onAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						Connection connection = null;
						try {
							// create a database connection
							connection = DriverManager.getConnection("jdbc:sqlite:full_records.db");

							Statement statement = connection.createStatement();
							statement.setQueryTimeout(30); // set timeout to 30 sec.

							ResultSet resultSet = statement.executeQuery("SELECT * FROM entry WHERE strain_name = '"
									+ ((TreeItem) getSelectionModel().getSelectedItem()).getValue() + "'");

							StrainTab temp = new StrainTab(new Strain(resultSet), false);
							tabs.getTabs().add(temp);
							selectionModel.select(temp);

						} catch (SQLException e) {
							System.err.println(e.getMessage());
						}
					}
				}).build(), MenuItemBuilder.create().text("Edit").onAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						Connection connection = null;
						try {
							// create a database connection
							connection = DriverManager.getConnection("jdbc:sqlite:full_records.db");

							Statement statement = connection.createStatement();
							statement.setQueryTimeout(30); // set timeout to 30 sec.

							ResultSet resultSet = statement.executeQuery("SELECT * FROM entry WHERE strain_name = '"
									+ ((TreeItem) getSelectionModel().getSelectedItem()).getValue() + "'");

							StrainTab temp = new StrainTab(new Strain(resultSet), true);
							tabs.getTabs().add(temp);
							selectionModel.select(temp);

						} catch (SQLException e) {
							System.err.println(e.getMessage());
						}
					}
				}).build()).build();

		setContextMenu(rootContextMenu);
	}

}