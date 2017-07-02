package video;

import java.awt.Dimension;
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
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.util.Duration;

// TODO MDL refactor, extract to other class
public class VideoRunnerSwingMainApp extends JFrame {

	private static final long serialVersionUID = 1L;

	private final static String MEDIA_URL = "src/main/resources/videos/KizunaAi.mp4";
	private static String ICON_URL = "D:/Git/GifRunner/GifRunner/src/main/resources/icons/small_anime-face.png";

	private final static String LABEL_MUTE = "Mute";
	private final static String LABEL_UNMUTE = "UnMute";

	private final static Double TASK_BARK_HEIGHT = 40.0;

	private int locationX;
	private int locationY;

	private MediaPlayer mediaPlayer;
	private MediaView mediaView;

	private MenuItem volumnItem;

	private JFXPanel fxPanel;

	public VideoRunnerSwingMainApp() {
		// decoration
		setType(Type.UTILITY);
		setUndecorated(true);

		// TODO MDL check again the window size
		setSize(new Dimension(1600, 900));

		toBack();

		// position

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// embed fx into swing
		fxPanel = new JFXPanel();

		VideoRunnerSwingMainApp.this.getContentPane().add(fxPanel);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				// set scene in JFXPanel
				fxPanel.setScene(createFxScene());
				setLocation(locationX, locationY);

				// show frame
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						VideoRunnerSwingMainApp.this.setVisible(true);
					}
				});
			}
		});
	}

	private Scene createFxScene() {
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
		mediaView = new MediaView(mediaPlayer);

		// Fit size
		mediaView.setFitWidth(screenBound.getWidth());
		mediaView.setFitHeight(screenBound.getHeight() + TASK_BARK_HEIGHT);

		// Set location
		locationX = (int) screenBound.getMinX();
		locationY = (int) screenBound.getMinY();

		root.getChildren().addAll(mediaView);

		Scene scene = new Scene(root);

		return scene;
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

			MenuItem changeItem = new MenuItem("Change");
			changeItem.addActionListener(e -> Platform.runLater(this::onChangeBackground));

			volumnItem = new MenuItem(LABEL_MUTE);
			volumnItem.addActionListener(e -> onChangeVolumn());

			MenuItem hideItem = new MenuItem("Hide");
			hideItem.addActionListener(e -> Platform.runLater(this::hideStage));

			MenuItem exitItem = new java.awt.MenuItem("Exit");
			exitItem.addActionListener(event -> {
				tray.remove(trayIcon);
				System.exit(0);
			});

			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(changeItem);
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

	private Object onChangeBackground() {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(null);
		if (file != null && "mp4".equals(FilenameUtils.getExtension(file.getName()))) {
			// TODO MDL extract this to a method
			mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
			mediaPlayer.setAutoPlay(true);
			mediaPlayer.setOnEndOfMedia(() -> {
				mediaPlayer.seek(Duration.ZERO);
			});
			mediaView.setMediaPlayer(mediaPlayer);
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	private void showStage() {
		show();
		toFront();
	}

	@SuppressWarnings("deprecation")
	private void hideStage() {
		hide();
	}

	private void onChangeVolumn() {
		mediaPlayer.setMute(!mediaPlayer.isMute());
		volumnItem.setLabel(mediaPlayer.isMute() ? LABEL_UNMUTE : LABEL_MUTE);
	}
	
	@SuppressWarnings("unused")
	private void changeScreen() {
		// TODO MDL implement this method
	}

	public static void main(String[] args) {
		new VideoRunnerSwingMainApp();
	}
}