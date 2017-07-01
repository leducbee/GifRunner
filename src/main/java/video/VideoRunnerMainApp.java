package video;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
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
	private final static Double TASK_BARK_HEIGHT = 40.0;

	@Override
	public void start(Stage stage) throws Exception {
		Pane root = new Pane();
		// detect screen
		Screen runningScreen = Screen.getPrimary();
		Rectangle2D screenBound = runningScreen.getVisualBounds();
		List<Screen> listScreen = Screen.getScreens().stream().filter(e -> !e.equals(Screen.getPrimary())).collect(Collectors.toList());
		if (!listScreen.isEmpty()) {
			runningScreen = listScreen.get(0);
		}

		// set coordinates stage
		if (runningScreen != null) {
			screenBound = runningScreen.getVisualBounds();
		}
		stage.setX(screenBound.getMinX());
		stage.setY(screenBound.getMinY());

		String path = new File(MEDIA_URL).getAbsolutePath().toString();

		MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File(path).toURI().toString()));
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
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
