package icontray;

import java.awt.MenuItem;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class IconTrayMainApp extends Application {

	private Stage stage;

	@Override
	public void start(final Stage stage) {
		this.stage = stage;

		Platform.setImplicitExit(false);
		addAppToTray();
		stage.initStyle(StageStyle.TRANSPARENT);
		StackPane layout = new StackPane(createContent());
		layout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
		layout.setPrefSize(300, 200);
		layout.setOnMouseClicked(event -> hideStage());
		Scene scene = new Scene(layout);
		scene.setFill(Color.TRANSPARENT);

		stage.setScene(scene);
		stage.show();
	}

	private Node createContent() {
		Label hello = new Label("hello, world");
		hello.setStyle("-fx-font-size: 40px; -fx-text-fill: forestgreen;");
		Label instructions = new Label("(click to hide)");
		instructions.setStyle("-fx-font-size: 12px; -fx-text-fill: orange;");

		VBox content = new VBox(10, hello, instructions);
		content.setAlignment(Pos.CENTER);

		return content;
	}

	private void addAppToTray() {
		try {
			java.awt.Toolkit.getDefaultToolkit();

			if (!java.awt.SystemTray.isSupported()) {
				System.out.println("No system tray support, application exiting.");
				Platform.exit();
			}

			java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
			java.awt.Image image = ImageIO
					.read(new File("D:/Git/GifRunner/GifRunner/src/main/resources/icons/face1.png"));
			java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			MenuItem hideItem = new MenuItem("Hide");
			hideItem.addActionListener(e -> Platform.runLater(this::hideStage));

			MenuItem exitItem = new java.awt.MenuItem("Exit");
			exitItem.addActionListener(event -> {
				tray.remove(trayIcon);
				Platform.exit();
			});

			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(hideItem);
			popup.addSeparator();
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);

			tray.add(trayIcon);
		} catch (java.awt.AWTException | IOException e) {
			System.out.println("Unable to init system tray");
			e.printStackTrace();
		}
	}

	private void showStage() {
		if (stage != null) {
			stage.show();
			stage.toFront();
		}
	}

	private void hideStage() {
		stage.hide();
	}

	public static void main(String[] args) throws IOException, java.awt.AWTException {
		launch(args);
	}
}
