package lofitsky.komptube.local.ui

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView


class HealthCheckButtonOnClickListenerFactory(private val scene: Scene) {
    companion object {
        class HealthCheckButtonOnClickListener(
            private val serviceController: ServiceController,
            private val scene: Scene,
            private val buttonId: String,
            private val imgActivePath: String,
            private val imgInactivePath: String,
        ) {
            private val button by lazy { scene.lookup(buttonId) as Button }
            private val imgActive by lazy { Image(this::class.java.classLoader.getResourceAsStream(imgActivePath)) }
            private val imgInactive by lazy { Image(this::class.java.classLoader.getResourceAsStream(imgInactivePath)) }

            fun handle() {
                button.apply {
                    isDisable = true
                    serviceController.checkHealth { isAlive ->
                        graphic = ImageView(if(isAlive) imgActive else imgInactive)
                        isDisable = false
                    }
                }
            }
        }
    }

    fun create(
        serviceController: ServiceController,
        buttonId: String,
        imgActivePath: String,
        imgInactivePath: String,
    ): HealthCheckButtonOnClickListener
        = HealthCheckButtonOnClickListener(serviceController, scene, buttonId, imgActivePath, imgInactivePath)
}
