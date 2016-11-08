package app.ui.componentes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public final class Toast {
	public static void makeText(Window padre, String toastMsg, int toastDelay, int fadeInDelay, int fadeOutDelay) {
		Stage toastStage = new Stage();
		toastStage.initOwner(padre);
		toastStage.setResizable(false);
		toastStage.initStyle(StageStyle.TRANSPARENT);

		Text text = new Text(toastMsg);
		text.setFont(Font.font("Verdana", 20));
		text.setFill(Color.RED);

		StackPane root = new StackPane(text);
		root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.2); -fx-padding: 30px;");
		root.setOpacity(0);

		Scene scene = new Scene(root);
		scene.setFill(Color.TRANSPARENT);
		toastStage.setScene(scene);

		final double midX = (padre.getX() + padre.getWidth() / 2);
		final double midY = (padre.getY() + padre.getHeight());

		toastStage.widthProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue.intValue() > 1){
				toastStage.setX(midX - newValue.intValue() / 2);
			}
		});

		toastStage.heightProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue.intValue() > 1){
				toastStage.setY(midY - newValue.intValue());
			}
		});

		toastStage.show();

		Timeline fadeInTimeline = new Timeline();
		KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(fadeInDelay), new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 1));
		fadeInTimeline.getKeyFrames().add(fadeInKey1);
		fadeInTimeline.setOnFinished((ae) -> {
			new Thread(() -> {
				try{
					Thread.sleep(toastDelay);
				} catch(InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Timeline fadeOutTimeline = new Timeline();
				KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 0));
				fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
				fadeOutTimeline.setOnFinished((aeb) -> toastStage.close());
				fadeOutTimeline.play();
			}).start();
		});
		fadeInTimeline.play();
	}
}