package ru.kpfu.itis;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import ru.kpfu.itis.entities.Sprite;
import ru.kpfu.itis.screens.NewScreen;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Game extends Application {
    private static Stage window;
    Scene scene, scene1;
    private static JavaTanksServer gameServer;

    public static void main(String[] args) {
        gameServer = JavaTanksServer.getInstance();
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
//<<<<<<< HEAD
        window.setMinWidth(1280);
        window.setMinHeight(720);
//        FXMLLoader loader = new FXMLLoader();
//        URL xmlUrl = getClass().getResource("/fxml/mainPage.fxml");
//        loader.setLocation(xmlUrl);
//        HBox root = loader.load();
//////
//        scene = new Scene(root);
//        window.setScene(scene);
//=======
        NewScreen main = new NewScreen("/fxml/mainPage.fxml", window);
        main.newScene();
        window.setTitle("A simple FXML Example");
        window.show();
    }

    @FXML
    private void registerUser() {

    }

    @FXML
    private void exit(ActionEvent event) {
        event.consume();
        System.out.println("Good bye, Mother Fucker!");
        Platform.exit();
    }

    @FXML
    private void regScene(ActionEvent event) throws IOException {
        event.consume();

        NewScreen screen = new NewScreen("/fxml/registration.fxml", Game.window);
        screen.newScene();

    }


    @FXML
    private void gameCanvas(ActionEvent event) throws IOException {
        event.consume();

        window.setTitle("GAME");


//        Todo working with ImageViews
//        Todo стрелять через ПРОБЕЛ

        Group root = new Group();
        Scene theScene = new Scene(root);

        Canvas canvas = new Canvas(1280, 720);
        root.getChildren().add(canvas);

        ArrayList<String> input = new ArrayList<>();


//        ОЧЕРЕДИ ДВИЖЕНИЙ ТАНКА
        theScene.setOnKeyPressed(
                e -> {
                    String code = e.getCode().toString();
                    if (!input.contains(code))
                        input.add(code);
                });

        theScene.setOnKeyReleased(
                e -> {
                    String code = e.getCode().toString();
                    input.remove(code);
                });

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Font theFont = Font.font("Helvetica", FontWeight.BOLD, 24);
        gc.setFont(theFont);
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        Sprite player = new Sprite();
        player.setImage("/images/player.png");
        player.setPosition(0, 0);

        Sprite enemy = new Sprite();
        enemy.setImage("/images/enemy.png");
        enemy.setPosition(1024, 670);
        enemy.render(gc);

        ArrayList<Sprite> moneybagList = new ArrayList<>();

        Thread thread = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                try {
                    Thread.sleep(4000);
                    Sprite moneybag = new Sprite();
                    moneybag.setImage("/images/moneybag.png");
                    double px = 1950 * Math.random() + 50;
                    double py = 650 * Math.random() + 20;
                    moneybag.setPosition(px, py);
                    moneybagList.add(moneybag);
                } catch (InterruptedException e) {
                }
            }
        });

        thread.start();
        LongValue lastNanoTime = new LongValue(System.nanoTime());

        IntValue score = new IntValue(0);

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                // calculate time since last update.
                if (score.value * 100 >= 1000) {
                   if (thread.isAlive())  thread.interrupt();
                    System.out.println("VICTORY");
                    System.exit(0);
                }
                double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
                lastNanoTime.value = currentNanoTime;

                // game logic

                player.setVelocity(0, 0);
                if (input.contains("LEFT"))
                    player.addVelocity(-100, 0);
                if (input.contains("RIGHT"))
                    player.addVelocity(100, 0);
                if (input.contains("UP"))
                    player.addVelocity(0, -100);
                if (input.contains("DOWN"))
                    player.addVelocity(0, 100);

                player.update(elapsedTime);

                // collision detection

                Iterator<Sprite> moneybagIter = moneybagList.iterator();
                while (moneybagIter.hasNext()) {
                    Sprite moneybag = moneybagIter.next();
                    if (player.intersects(moneybag)) {
                        moneybagIter.remove();
                        score.value++;
                    }
                }

                // render
                gc.clearRect(0, 0, 1280, 720);
                player.render(gc);
                enemy.render(gc);

                for (Sprite moneybag : moneybagList)
                    moneybag.render(gc);

                String pointsText = "Cash: $" + (100 * score.value);
                gc.fillText(pointsText, 360, 36);
                gc.strokeText(pointsText, 360, 36);
            }
        }.start();
        window.setScene(theScene);
//        window.show();

    }

    @FXML
    private void operator(ActionEvent event) {
        event.consume();
    }

}
