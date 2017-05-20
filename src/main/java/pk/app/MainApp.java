package pk.app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FlowPane root = new FlowPane();
		
		// detect screen
		Screen runningScreen = Screen.getPrimary();
		Rectangle2D screenBound = new Rectangle2D(0, 0, 0, 0);
		List<Screen> listScreen = Screen.getScreens().stream().filter(e -> !e.equals(Screen.getPrimary()))
				.collect(Collectors.toList());
		if (!listScreen.isEmpty()) {
			runningScreen = listScreen.get(0);
		}

		// set coordinates stage
		if (runningScreen != null) {
			screenBound = runningScreen.getVisualBounds();
			stage.setX(screenBound.getMinX());
			stage.setY(screenBound.getMinY());
		}
		
		// add element
		final Rectangle2D finalScreenBound = screenBound;
		List<ImageView> listImageView = new ArrayList<>();
		listImageView.add(new ImageView(new Image("monkey-eat-banana-001.gif")));
		listImageView.add(new ImageView(new Image("monkey-eat-banana-002.gif")));
		listImageView.add(new ImageView(new Image("monkey-eat-banana-003.gif")));
		listImageView.add(new ImageView(new Image("monkey-eat-banana-004.gif")));
		
		listImageView.forEach(e -> {
			e.setFitWidth(finalScreenBound.getWidth()/2);
			e.setFitHeight(finalScreenBound.getHeight()/2);
		});
		
		root.getChildren().addAll(listImageView);
		

		Scene scene = new Scene(root, screenBound.getWidth(), screenBound.getHeight());
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
