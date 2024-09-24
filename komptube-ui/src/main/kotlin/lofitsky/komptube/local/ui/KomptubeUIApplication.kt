package lofitsky.komptube.local.ui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import kotlin.concurrent.thread


class KomptubeUIApplication : Application() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(KomptubeUIApplication::class.java)
        }
    }

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(KomptubeUIApplication::class.java.getResource("komptube.fxml"))
        val scene = Scene(fxmlLoader.load(), 600.0, 200.0)
        stage.maxWidth = 600.0
        stage.minWidth = 600.0
        stage.maxHeight = 250.0
        stage.minHeight = 250.0
        stage.title = "Komptube"
        stage.scene = scene

        (scene.lookup("#urlField") as TextField).text = getTextFromClipboard()

        HealthCheckOnClickListeners.setScene(scene)
        val thread1 = thread { HealthCheckOnClickListeners.kompHealthCheckOnClickListener.handle() }
        val thread2 = thread { HealthCheckOnClickListeners.phoneHealthCheckOnClickListener.handle() }

        thread1.join()
        thread2.join()

        stage.show()
    }

    private fun getTextFromClipboard(): String =
        (Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as String)
            .takeIf { it.startsWith("http") }
            ?: ""
}
