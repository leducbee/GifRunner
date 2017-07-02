package video;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class VideoRunnerMainApp extends Application {
	private final static String MEDIA_URL = "src/main/resources/videos/KizunaAi.mp4";
	private static String ICON_URL = "D:/Git/GifRunner/GifRunner/src/main/resources/icons/small_anime-face.png";

	private final static String LABEL_MUTE = "Mute";
	private final static String LABEL_UNMUTE = "UnMute";

	private final static Double TASK_BARK_HEIGHT = 40.0;

	private Stage stage;
	private MediaPlayer mediaPlayer;

	private MenuItem volumnItem;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		Platform.setImplicitExit(false);
		addApptoTray();

		Pane root = new Pane();

		// detect screen
		Screen runningScreen = Screen.getPrimary();
		Rectangle2D screenBound = runningScreen.getVisualBounds();
		List<Screen> listScreen = Screen.getScreens().stream().filter(e -> !e.equals(Screen.getPrimary()))
				.collect(Collectors.toList());
		if (!listScreen.isEmpty()) {
			runningScreen = listScreen.get(0);
		}

		// set coordinates stage
		if (runningScreen != null) {
			screenBound = runningScreen.getVisualBounds();
		}

		String path = new File(MEDIA_URL).getAbsolutePath().toString();

		mediaPlayer = new MediaPlayer(new Media(new File(path).toURI().toString()));
		mediaPlayer.setAutoPlay(true);
		mediaPlayer.setOnEndOfMedia(() -> {
			mediaPlayer.seek(Duration.ZERO);
		});
		MediaView mediaView = new MediaView(mediaPlayer);

		// Fit size
		mediaView.setFitWidth(screenBound.getWidth());
		mediaView.setFitHeight(screenBound.getHeight() + TASK_BARK_HEIGHT);

		root.getChildren().addAll(mediaView);

		Scene scene = new Scene(root, screenBound.getWidth(), screenBound.getHeight() + TASK_BARK_HEIGHT);
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setX(screenBound.getMinX());
		stage.setY(screenBound.getMinY());
		stage.show();
	}

	private void addApptoTray() {
		try {
			Toolkit.getDefaultToolkit();

			if (!SystemTray.isSupported()) {
				System.out.println("No system tray support, application exiting.");
				Platform.exit();
			}

			SystemTray tray = SystemTray.getSystemTray();
			Image image = ImageIO.read(new File(ICON_URL));
			TrayIcon trayIcon = new java.awt.TrayIcon(image);

			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			volumnItem = new MenuItem(LABEL_MUTE);
			volumnItem.addActionListener(e -> onChangeVolumn());

			MenuItem hideItem = new MenuItem("Hide");
			hideItem.addActionListener(e -> Platform.runLater(this::hideStage));

			MenuItem exitItem = new java.awt.MenuItem("Exit");
			exitItem.addActionListener(event -> {
				tray.remove(trayIcon);
				Platform.exit();
			});

			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(volumnItem);
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
		}
	}

	private void hideStage() {
		if (stage != null) {
			stage.hide();
		}
	}

	private void onChangeVolumn() {
		mediaPlayer.setMute(!mediaPlayer.isMute());
		volumnItem.setLabel(mediaPlayer.isMute() ? LABEL_UNMUTE : LABEL_MUTE);
	}
}
