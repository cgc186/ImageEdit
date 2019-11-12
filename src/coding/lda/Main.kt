package lda

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import java.net.URL

import java.util.Objects

class Main : Application() {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        try {
            val root =
                FXMLLoader.load<AnchorPane>(Objects.requireNonNull<URL>(javaClass.classLoader.getResource("/UI.fxml")))//FR/UI.fxml
            val scene = Scene(root)
            primaryStage.scene = scene
            primaryStage.title = "图像识别"
            primaryStage.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(*args)
        }
    }
}